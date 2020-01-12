package lb.simplebase.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import lb.simplebase.action.AsyncResult;
import lb.simplebase.io.WritableStreamData;
import lb.simplebase.util.OptionalError;
import lb.simplebase.io.WritableFixedData.WritableBufferData;
import lb.simplebase.net.ClosedConnectionEvent.Cause;

public class NioNetworkConnection extends NetworkConnection{

	private final SocketChannel channel;
	private SelectionKey channelServerSelection;
	
	protected NioNetworkConnection(TargetIdentifier local, TargetIdentifier remote, NioNetworkManagerServer packetHandler,
			SocketChannel acceptedChannel, boolean isServer, Object payload) {
		super(local, remote, packetHandler, ConnectionState.fromChannel(acceptedChannel), isServer, payload);

		assert acceptedChannel.isBlocking();
		this.channel = acceptedChannel;
		
		if(acceptedChannel.isConnected()) {
			try {
				channel.configureBlocking(false);
				channelServerSelection = packetHandler.registerConnectionChannel(this);
				NetworkManager.NET_LOG.info("Network Connection: Channel connected, switched to non-blocking mode");
				if(channelServerSelection == null) {
					NetworkManager.NET_LOG.error("Network Connection: Could not register Channel at server manager. No data will be read from this channel");
				}
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("Network Connection: Could not switch to non-blocking mode");
			} //Switch to non-blocking node for selector
		}
	}

	protected SocketChannel getChannel() {
		return channel;
	}


	@Override
	public OptionalError<Boolean, IOException> connect(int timeout) {
		if(state == ConnectionState.UNCONNECTED) {
			try {
				getRemoteTargetId().connectSocket(() -> SocketActions.of(channel), 0);
				channel.configureBlocking(false); //Switch to non-blocking node for selector
				channelServerSelection = ((NioNetworkManagerServer) getNetworkManager()).registerConnectionChannel(this);
				NetworkManager.NET_LOG.info("Network Connection: Channel connected, switched to non-blocking mode");
				if(channelServerSelection == null) {
					NetworkManager.NET_LOG.error("Network Connection: Could not register Channel at server manager. No data will be read from this channel");
				}
				state = ConnectionState.OPEN;
				return OptionalError.ofValue(Boolean.FALSE, IOException.class);
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("An IO error occurred while trying to connect the SocketChannel", e);
				return OptionalError.ofException(e, Boolean.class);
			}
		} else {
			NetworkManager.NET_LOG.warn("Connection is already " + (getState() == ConnectionState.CLOSED ? "closed" : "connected")
					+ " and cannot be connected again");
			return OptionalError.ofValue(Boolean.FALSE, IOException.class);
		}
	}


	@Override
	public AsyncResult sendPacketToTarget(Packet packet) {
		if(getState() == ConnectionState.OPEN) {
			return AsyncNetTask.submitTask((f) -> {
				//1. Get packet data
				ByteBuffer dataToSend;
				try {
					dataToSend = createNioPacketData(packet, getNetworkManager());
				} catch (PacketMappingNotFoundException e) {
					f.setErrorAndMessage(e, "No mapping was found for packet type " + packet.getClass().getSimpleName());
					return;
				}
				//2. Write to channel
				dataToSend.flip();
				try {
					do {
						channel.write(dataToSend);
					} while(dataToSend.hasRemaining());
				} catch (IOException e) {
					f.setErrorAndMessage(e, "An IO error occurred while trying to write packet data to the connection channel");
					return;
				}
			});
		} else {
			return AsyncNetTask.createFailed(null, "Connection was not open");
		}
	}


	@Override
	public Optional<IOException> close() {
		NetworkManager.NET_LOG.debug("Closing connection, current state " + getState());
		if(state == ConnectionState.CLOSED) {
			NetworkManager.NET_LOG.info("Connection already closed");
			return Optional.empty();
		} else {
			try {
				channel.close();
				channelServerSelection.cancel();
				NetworkManager.NET_LOG.info("Closed Network connection to " + getRemoteTargetId());
				closeWithReason(Cause.EXPECTED);
				return Optional.empty();
			} catch (IOException e) {
				//If closing fails
				NetworkManager.NET_LOG.error("Closing the Socket failed with exception", e);
				closeWithReason(Cause.EXPECTED);
				return Optional.of(e);
			}
		}
	}


	@Override
	public boolean isLocalConnection() {
		return false;
	}
	
	protected  SelectionKey getServerSelectionKey() {
		return channelServerSelection;
	}
	
	
	protected static ByteBuffer createNioPacketData(Packet packet, PacketIdMappingContainer mapCon) throws PacketMappingNotFoundException {
		//First, check for a mapping for the packet class
		if(!mapCon.hasMappingFor(packet.getClass()))
			throw new PacketMappingNotFoundException("No mapping was found when trying to send packet", packet);
		final int packetId = mapCon.getMappingFor(packet.getClass()).getPacketId(); //The mapping must exist, otherwise ^^
		//Then move write packet data to a buffer
		final WritableStreamData packetData = new WritableStreamData();
		packet.writeData(packetData); //Write packet data
		final int packetDataLength = packetData.getLength();
		//Write data to buffer
		final WritableBufferData allData = new WritableBufferData(packetDataLength + 12);
		allData.write(PacketFactory.PACKETHEADER);
		allData.writeInt(packetId);
		allData.writeInt(packetDataLength);
		allData.write(packetData.internalArray());
		//create array
		return allData.getBuffer();
	}
	
}

package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

class RemoteNetworkConnection extends AbstractNetworkConnection{

	private final Socket connection;
	private final DataReceiverThread dataThread;
	private final PacketFactory factory;
	
	public RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, boolean isServer, Object payload) {
		this(source, target, packetHandler, new Socket(), isServer, payload);
	}
	
	public RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, Socket connectedSocket, boolean isServer, Object payload) {
		super(source, target, packetHandler, ConnectionState.fromSocket(connectedSocket), isServer, payload); //Create the state from the socket (that might be open from a server)
		connection = connectedSocket;
		factory = new PacketFactory(getNetworkManager(), this::handleReceivedPacket);
		dataThread = new DataReceiverThread(connection, factory, this);
		if(connectedSocket.isConnected()) dataThread.start(); //Begin when a live socket is used
	}
	
	@Override
	public PacketSendFuture sendPacketToTarget(Packet packet) {
		if(getState() == ConnectionState.OPEN) {
			return PacketSendFuture.create((f) -> {
				byte[] dataToSend;
				//1. Split packet into bytes
				try {
					dataToSend = factory.createPacketData(packet);
				} catch (PacketMappingNotFoundException e) {
					f.setErrorAndMessage(e, "No mapping was found for packet type " + packet.getClass().getSimpleName());
					return; //On error, abort here
				}
				//2. Try to send it through the connection
				try {
					connection.getOutputStream().write(dataToSend);
				} catch (IOException e) {
					f.setErrorAndMessage(e, "An IO error occurred while trying to write packet data to the connection");
					return;
				}
				//3. Done!
				f.setPacketSent(true);
			}).run();
		} else {
			return PacketSendFuture.quickFailed("Connection was not open");
		}
	}
	
	@Override
	public ConnectionStateFuture close() {
		ConnectionStateFuture superClose = super.close(); //Ignore result, it will always be a success
		NetworkManager.NET_LOG.debug("Closing connection, current state " + getState());
		if(superClose.getOldState() == ConnectionState.CLOSED) return superClose;
		return ConnectionStateFuture.create(superClose.getOldState(), (f) -> {
			try {
				connection.shutdownOutput();
				connection.close();
				f.setCurrentState(ConnectionState.CLOSED);
				NetworkManager.NET_LOG.info("Closing Network connection to " + getRemoteTargetId());
			} catch (IOException e) {
				//If closing fails
				f.setErrorAndMessage(e, "Closing the Socket failed with exception");
			}
		}).run();
	}

	@Override
	public ConnectionStateFuture connect(int timeout) {
		if(getState() == ConnectionState.UNCONNECTED) {
			return ConnectionStateFuture.create(getState(), (f) -> {
				try {
					if(timeout == 0) {
						connection.connect(getRemoteTargetId().getConnectionAddress());
					} else {
						connection.connect(getRemoteTargetId().getConnectionAddress(), timeout);
					}
					//After connecting successfully, start the listener thread
					dataThread.start();
					//And lastly set the state
					state = ConnectionState.OPEN;
					f.setCurrentState(state);
				} catch (SocketTimeoutException e) {
					f.setErrorAndMessage(e, "The timeout (" + timeout + "ms) expired before a connection could be made");
				} catch (IOException e) {
					f.setErrorAndMessage(e, "An IO error occurred while trying to connect the Socket");
				}
			}).run();
		} else {
			return ConnectionStateFuture.quickFailed("Connection is already " + (getState() == ConnectionState.CLOSED ? "closed" : "connected")
					+ " and cannot be connected again", getState());
		}
	}

	@Override
	public boolean isLocalConnection() {
		return false;
	}
	
}
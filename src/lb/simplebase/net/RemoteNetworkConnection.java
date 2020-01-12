package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;

import lb.simplebase.action.AsyncResult;
import lb.simplebase.net.ClosedConnectionEvent.Cause;
import lb.simplebase.util.OptionalError;

class RemoteNetworkConnection extends NetworkConnection{

	private final Socket connection;
	private final DataReceiverThread dataThread;
	private final PacketFactory factory;
	
	public RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, Socket connectedSocket, boolean isServer, Object payload) {
		super(source, target, packetHandler, ConnectionState.fromSocket(connectedSocket), isServer, payload); //Create the state from the socket (that might be open from a server)
		
		this.connection = connectedSocket;
		this.factory = new PacketFactory(getNetworkManager(), this::handleReceivedPacket);
		this.dataThread = new DataReceiverThread(connection, factory, this);
		
		if(connectedSocket.isConnected()) dataThread.start(); //Begin when a live socket is used
	}
	
	@Override
	public AsyncResult sendPacketToTarget(Packet packet) {
		if(getState() == ConnectionState.OPEN) {
			return AsyncNetTask.submitTask((f) -> {
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
			});
		} else {
			return AsyncNetTask.createFailed(null, "Connection was not open");
		}
	}
	
	@Override
	public Optional<IOException> close() {
		try { //The entire thing changes the state, so sync on write
			stateRW.writeLock().lock();
			NetworkManager.NET_LOG.debug("Closing connection, current state " + getState());
			if(state == ConnectionState.CLOSED) {
				NetworkManager.NET_LOG.info("Connection already closed");
				return Optional.empty();
			} else {
				try {
					connection.shutdownOutput();
					connection.close();
					NetworkManager.NET_LOG.info("Closed Network connection to " + getRemoteTargetId());
					closeWithReason(Cause.EXPECTED);
					return Optional.empty();
				} catch (IOException e) {
					NetworkManager.NET_LOG.error("Closing the Socket failed with exception", e);
					closeWithReason(Cause.EXPECTED); //It is expected to close, this IOException did not CAUSE closing the socket
					return Optional.of(e);
				}
			}
		} finally {
			stateRW.writeLock().unlock();
		}
	}

	@Override
	public synchronized OptionalError<Boolean, IOException> connect(int timeout) {
		try {
			stateRW.writeLock().lock();
			if(getState() == ConnectionState.UNCONNECTED) {
				try {
					getRemoteTargetId().connectSocket(() -> SocketActions.of(connection), timeout);
					//				connection.connect(getRemoteTargetId().getConnectionAddress(), timeout);
					//After connecting successfully, start the listener thread
					dataThread.start();
					//And lastly set the state
					state = ConnectionState.OPEN;
					return OptionalError.ofValue(Boolean.FALSE, IOException.class);
				} catch (SocketTimeoutException e) {
					NetworkManager.NET_LOG.warn("The timeout (" + timeout + "ms) expired before a connection could be made", e);
					return OptionalError.ofValue(Boolean.TRUE, IOException.class);
				} catch (IOException e) {
					NetworkManager.NET_LOG.warn("An IO error occurred while trying to connect the Socket", e);
					return OptionalError.ofException(e, Boolean.class);
				}
			} else {
				NetworkManager.NET_LOG.warn("Connection is already " + (getState() == ConnectionState.CLOSED ? "closed" : "connected")
						+ " and cannot be connected again");
				return OptionalError.ofValue(Boolean.FALSE, IOException.class);
			}
		} finally {
			stateRW.writeLock().unlock();
		}
	}

	@Override
	public boolean isLocalConnection() {
		return false;
	}
	
}
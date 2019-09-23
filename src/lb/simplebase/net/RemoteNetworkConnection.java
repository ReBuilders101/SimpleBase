package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import lb.simplebase.action.AsyncResult;

class RemoteNetworkConnection extends NetworkConnection{

	private final Socket connection;
	private final DataReceiverThread dataThread;
	private final PacketFactory factory;
	private final PacketContext context;
	
	public RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, Socket connectedSocket, boolean isServer, Object payload) {
		super(source, target, packetHandler, ConnectionState.fromSocket(connectedSocket), isServer, payload); //Create the state from the socket (that might be open from a server)
		
		this.connection = connectedSocket;
		this.factory = new PacketFactory(getNetworkManager(), this::handleReceivedPacket);
		this.dataThread = new DataReceiverThread(connection, factory, this);
		this.context = new PacketContext.PayloadPacketContext(isServer, packetHandler, this, payload);
		
		if(connectedSocket.isConnected()) dataThread.start(); //Begin when a live socket is used
	}
	
	@Override
	public AsyncResult sendPacketToTarget(Packet packet) {
		if(getState() == ConnectionState.OPEN) {
			return AsyncNetTask.createTask((f) -> {
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
			}).run();
		} else {
			return AsyncNetTask.createFailed(null, "Connection was not open");
		}
	}
	
	@Override
	public void close() {
		super.close(); //Ignore result, it will always be a success
		NetworkManager.NET_LOG.debug("Closing connection, current state " + getState());
		if(state == ConnectionState.CLOSED) {
			NetworkManager.NET_LOG.info("Connection already closed");
		} else {
			try {
				connection.shutdownOutput();
				connection.close();
				super.close();
				NetworkManager.NET_LOG.info("Closed Network connection to " + getRemoteTargetId());
			} catch (IOException e) {
				//If closing fails
				NetworkManager.NET_LOG.error("Closing the Socket failed with exception", e);
			}
		}
	}

	@Override
	public void connect(int timeout) {
		if(getState() == ConnectionState.UNCONNECTED) {
			try {
				connection.connect(getRemoteTargetId().getConnectionAddress(), timeout);
				//After connecting successfully, start the listener thread
				dataThread.start();
				//And lastly set the state
				state = ConnectionState.OPEN;
			} catch (SocketTimeoutException e) {
				NetworkManager.NET_LOG.warn("The timeout (" + timeout + "ms) expired before a connection could be made", e);
			} catch (IOException e) {
				NetworkManager.NET_LOG.error("An IO error occurred while trying to connect the Socket", e);
			}
		} else {
			NetworkManager.NET_LOG.warn("Connection is already " + (getState() == ConnectionState.CLOSED ? "closed" : "connected")
					+ " and cannot be connected again");
		}
	}

	@Override
	public boolean isLocalConnection() {
		return false;
	}

	@Override
	protected PacketContext getContext() {
		return context;
	}
	
}
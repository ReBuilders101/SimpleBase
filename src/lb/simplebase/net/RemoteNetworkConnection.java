package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import lb.simplebase.net.done.AbstractNetworkConnection;
import lb.simplebase.net.done.ConnectionState;
import lb.simplebase.net.done.NetworkManager;
import lb.simplebase.net.done.Packet;
import lb.simplebase.net.done.PacketMappingNotFoundException;
import lb.simplebase.net.done.TargetIdentifier;

public class RemoteNetworkConnection extends AbstractNetworkConnection{

	private final Socket connection;
	private final Thread socketListenerThread;
	private final int id;
	
	private static volatile int ID = 0; 
	
	public RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler) {
		this(source, target, packetHandler, new Socket());
	}

	protected RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, boolean connect)
			throws ConnectionStateException {
		this(source, target, packetHandler);
		if(connect)
			connect();
	}
	
	protected RemoteNetworkConnection(TargetIdentifier source, TargetIdentifier target, NetworkManager packetHandler, Socket connectedSocket) {
		super(source, target, packetHandler, ConnectionState.fromSocket(connectedSocket)); //Create the state from the socket (that might be open from a server)
		connection = connectedSocket;
		id = ID++; //Assign unique id
		//setup thread
		socketListenerThread = new Thread(this::waitForPacket);
		socketListenerThread.setDaemon(true);
		socketListenerThread.setName("RemoteNetworkConnection-" + id + "-SocketListener");
		socketListenerThread.start();
	}
	
	@Override
	public void sendPacketToTarget(Packet packet) throws ConnectionStateException {
		if(getState() == ConnectionState.OPEN) {
			byte[] data;
			try {
				data = getPacketFactory().createPacketData(packet); //try to make a packet
			} catch (PacketMappingNotFoundException e1) {
				throw new ConnectionStateException("The packet type could not be converted into an id and the packet could not be sent", e1, this, ConnectionState.OPEN);
			}
			try {
				connection.getOutputStream().write(data);
			} catch (IOException e) {
				throw new ConnectionStateException("An IOException occurred while trying to send a packet", e, this, ConnectionState.OPEN); //Maybe another exception type?
			}
		} else {
			throw new ConnectionStateException("The NetworkConnection was not open when a Packet was supposed to be sent", this, ConnectionState.OPEN);
		}
	}
	
	@Override
	public void close() {
		super.close();
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace(); //can't do much if the connection does not want to close
		}
	}

	private void waitForPacket(){
		while(!connection.isClosed()) {
			try {
				if(isConnectionOpen()) {
					byte b = (byte) connection.getInputStream().read();
					getPacketFactory().feed(b);
				}
				//Do something with the bytes
			}catch (SocketException e) {
				//Do nothing, socket is closed and loop will exit next iteration 
			} catch (IOException e) {
				e.printStackTrace();
			} catch (PacketMappingNotFoundException e) {
				e.printStackTrace();
				//probably a different error log
			}
		}
		close(); //close when while exits (this means the remote partner closed the connection 
	}

	@Override
	public void connect(int timeout) throws ConnectionStateException {
		if(getState() == ConnectionState.UNCONNECTED) {
			try {
				connection.connect(getRemoteTargetId().getConnectionAddress(), timeout);
			} catch (IOException e) {
				throw new ConnectionStateException("An exception occurred while opening the connection", e, this, ConnectionState.UNCONNECTED);
			}
			setConnectionState(ConnectionState.OPEN);
		} else if(getState() == ConnectionState.OPEN){
			throw new ConnectionStateException("The connection was already open", this, ConnectionState.UNCONNECTED);
		} else if (getState() == ConnectionState.CLOSED){
			throw new ConnectionStateException("The connection had already been closed", this, ConnectionState.UNCONNECTED);
		} else {
			throw new RuntimeException("I have no idea what is going on");
		}
	}

	@Override
	public boolean isLocalConnection() {
		return false;
	}
	
}
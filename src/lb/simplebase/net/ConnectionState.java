package lb.simplebase.net;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * Saves information about the state of a {@link NetworkConnection}.
 */
public enum ConnectionState {
	/**
	 * The information necessary to make a connection is present,
	 * but the connection has not yet been made. Data cannot be sent through this connection.
	 */
	UNCONNECTED(false),
	/**
	 * The connection has been opened / connected and not yet been closed.
	 * Data can be sent through this connection.
	 */
	OPEN(true),
	/**
	 * The connection has been closed. This means that data can no longer be sent through the connection.
	 * A closed connection can never be opened again.
	 */
	CLOSED(false);
	
	private final boolean canSend;
	
	private ConnectionState(boolean canSend) {
		this.canSend = canSend;
	}
	
	/**
	 * Whether data can be sent through the connection at this state.
	 * @return Whether data can be sent through the connection at this state
	 */
	public boolean canSendData() {
		return canSend;
	}
	
	/**
	 * The {@link ConnectionState} of a {@link NetworkConnection} created with this {@link Socket}.
	 * @param socket The {@link Socket} for the connection
	 * @return The state of the connection
	 */
	public static ConnectionState fromSocket(Socket socket) {
		if(!socket.isConnected()) {
			return ConnectionState.UNCONNECTED; //If never connected, then still unconnected
		} else if(!socket.isClosed()) {
			return ConnectionState.OPEN; //If never closed, open
		} else {
			return ConnectionState.CLOSED; //Else it is already closed
		}
//		if(socket.isClosed()) return CLOSED;
//		if(socket.isConnected()) return OPEN;
//		return UNCONNECTED;
	}

	public static ConnectionState fromChannel(SocketChannel channel) {
		if(channel.isOpen()) {
			return ConnectionState.OPEN;
		} else if (channel.isConnected()){
			return ConnectionState.CLOSED;
		} else {
			return ConnectionState.UNCONNECTED;
		}
	}
	
	public static ConnectionState fromSocket(ServerSocket socket) {
		if(!socket.isBound()) {
			return ConnectionState.UNCONNECTED; //If never connected, then still unconnected
		} else if(!socket.isClosed()) {
			return ConnectionState.OPEN; //If never closed, open
		} else {
			return ConnectionState.CLOSED; //Else it is already closed
		}
	}
}
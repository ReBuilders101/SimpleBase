package lb.simplebase.net;

import java.io.IOException;
import java.net.Socket;

import lb.simplebase.event.Event;

public final class ClosedConnectionEvent extends Event {

	protected ClosedConnectionEvent(AbstractNetworkConnection connection, Cause cause) {
		super(false);
		connectionContext = connection.getContext();
		this.remoteClosed = cause;
	}
	
	private final PacketContext connectionContext;
	private final Cause remoteClosed;
	
	public PacketContext getConnectionContext() {
		return connectionContext;
	}
	
	public Cause getCause() {
		return remoteClosed;
	}
	
	/**
	 * A set of reasons why a connection was closed
	 */
	public static enum Cause {
		/**
		 * The connection was closed because the close method was called on the {@link AbstractNetworkConnection} on the local side
		 */
		EXPECTED,
		/**
		 * The connection was closed because the thread that receives and processes incoming data from was interrupted and stopped 
		 */
		INTERRUPTED,
		/**
		 * The connection was closed because the remote side of the connection was closed
		 */
		REMOTE,
		/**
		 * The connection was closed because an underlying object, e.g. a {@link Socket}, was closed by non-API code
		 */
		EXTERNAL,
		/**
		 * The connection was closed because an {@link IOException} was thrown when interacting with an underlying object, e.g. a {@link Socket}
		 */
		IOEXCEPTION,
		/**
		 * The connection was closed for an unknown reason
		 */
		UNKNOWN;
	}
	
}

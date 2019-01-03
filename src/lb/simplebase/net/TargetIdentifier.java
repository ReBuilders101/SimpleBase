package lb.simplebase.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * All objects implementing this interface can be used to identify a network target.
 * Because {@link TargetIdentifier} is used as a key in {@link HashMap}s, all implementatioons should
 * implement the {@link Object#hashCode()} method.
 * @see NetworkTargetIdentifier
 * @see LocalTargetIdentifier
 *  */
public interface TargetIdentifier {
	
	/**
	 * An {@link InetSocketAddress} that con be used to open a {@link Socket}-based connection to this
	 * network target. If {@link #isLocalOnly()} returns <code>true</code>, this {@link TargetIdentifier} cannot be
	 * used for network traffic and may return <code>null</code>. Otherwise, a valid {@link InetAddress} must be returned.
	 * @return The connection address for a {@link Socket} connection
	 */
	public InetSocketAddress getConnectionAddress();
	
	/**
	 * Every {@link TargetIdentifier} holds a Sring ID that should be unique for every target in the program.
	 * @return The id of this {@link TargetIdentifier}
	 */
	public String getId();
	
	/**
	 * If <code>true</code>, this {@link TargetIdentifier} can only be used for network-style communication
	 * inside one program, but not to create a network connection.<br>
	 * If <code>true</code>, {@link #getConnectionAddress()} may return null or an invalid address.
	 * @return If this is a local target
	 */
	public boolean isLocalOnly();
	
	/**
	 * A {@link TargetIdentifier} that can be used for Network communication. It contais a valid
	 * {@link InetSocketAddress} that con be used to open {@link Socket}-based connections. 
	 */
	public static class NetworkTargetIdentifier implements TargetIdentifier{

		private final String id;
		private final InetSocketAddress address;
		
		/**
		 * Creates a new instance of {@link NetworkTargetIdentifier} that can be
		 * used for network communication.
		 * @param id The unique id that is used for this target
		 * @param ipAddress The Ip-Address of the network target.
		 * @param port The port on which the network connection will be connecting
		 * @throws UnknownHostException When the IP-Address is invalid
		 */
		public NetworkTargetIdentifier(String id, String ipAddress, int port) throws UnknownHostException {
				this.id = id;
				InetAddress inet = InetAddress.getByName(ipAddress);
				address = new InetSocketAddress(inet, port);
		}
		
		/**
		 * Returns the {@link InetSocketAddress} that can be used to connect a socket to this target.
		 * @return The {@link InetSocketAddress} for this target
		 */
		@Override
		public InetSocketAddress getConnectionAddress() {
			return address;
		}

		/**
		 * Because this implementation of {@link TargetIdentifier} can be used for network connections, this method
		 * always returns <code>false</code>.
		 * @return Always <code>false</code>
		 */
		@Override
		public boolean isLocalOnly() {
			return false;
		}

		/**
		 * An Id that is unique to this {@link TargetIdentifier} in the program.
		 * @return The Id of this network target
		 */
		@Override
		public String getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return "NetworkTargetIdentifier [id=" + id + ", address=" + address + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((address == null) ? 0 : address.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NetworkTargetIdentifier other = (NetworkTargetIdentifier) obj;
			if (address == null) {
				if (other.address != null)
					return false;
			} else if (!address.equals(other.address))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
	
	/**
	 * A {@link TargetIdentifier} that can <b>not</b> be used for Network communication. It does not store a
	 * {@link InetSocketAddress} and can only be used to identify a simulated network target inside the program.
	 */
	public static class LocalTargetIdentifier implements TargetIdentifier{
		
		private final String localId;
		
		/**
		 * Creates a new {@link LocalTargetIdentifier}
		 * @param localId The unique id that is used for this target
		 */
		public LocalTargetIdentifier(String localId) {
			this.localId = localId;
		}

		/**
		 * Because this implementation of {@link TargetIdentifier} does not support network communication,
		 * this method always returns <code>null</code>.
		 * @return Always <code>null</code>
		 * @see TargetIdentifier#getConnectionAddress()
		 */
		@Override
		public InetSocketAddress getConnectionAddress() {
			return null;
		}

		/**
		 * Because this implementation if {@link TargetIdentifier} does not support network communication,
		 * this method always returns <code>true</code>.
		 * @return Always <code>true</code>
		 */
		@Override
		public boolean isLocalOnly() {
			return true;
		}

		/**
		 * An Id that is unique to this {@link TargetIdentifier} in the program.
		 * @return The Id of this network target
		 */
		@Override
		public String getId() {
			return localId;
		}

		@Override
		public String toString() {
			return "LocalTargetIdentifier [localId=" + localId + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((localId == null) ? 0 : localId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LocalTargetIdentifier other = (LocalTargetIdentifier) obj;
			if (localId == null) {
				if (other.localId != null)
					return false;
			} else if (!localId.equals(other.localId))
				return false;
			return true;
		}
	}
}

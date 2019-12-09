package lb.simplebase.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A {@link TargetIdentifier} that can be used for Network communication. It contais a valid
 * {@link InetSocketAddress} that con be used to open {@link Socket}-based connections. 
 */
class NetworkTargetIdentifier implements TargetIdentifier{

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
		this(id, InetAddress.getByName(ipAddress), port);
	}
	
	public NetworkTargetIdentifier(String id, InetAddress ipAddress, int port) throws UnknownHostException {
		this(id, new InetSocketAddress(ipAddress, port));
	}
	
	/**
	 * Creates a new instance of {@link NetworkTargetIdentifier} that can be used for network
	 * communication.
	 * @param id The unique id that is used for this target
	 * @param address The {@link InetSocketAddress} that can be used to connect a {@link Socket} to this target
	 */
	public NetworkTargetIdentifier(String id, InetSocketAddress address) {
		this.id = id;
		this.address = address;
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

	@Override
	public boolean matches(String hostname, int port) {
		if(hostname == null) return false;
		InetSocketAddress testAddress = new InetSocketAddress(hostname, port);
		return testAddress.equals(address);
	}

	@Override
	public boolean matches(InetAddress address2, int port) {
		if(address2 == null) return false;
		InetSocketAddress testAddress = new InetSocketAddress(address2, port);
		return testAddress.equals(address);
	}

	@Override
	public boolean matches(InetSocketAddress address2) {
		return address.equals(address2);
	}
}
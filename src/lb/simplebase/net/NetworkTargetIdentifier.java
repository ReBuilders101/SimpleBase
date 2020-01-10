package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

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
	
	public NetworkTargetIdentifier(String id, InetAddress ipAddress, int port) {
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
	 * Because this implementation of {@link TargetIdentifier} can be used for network connections, this method
	 * always returns <code>false</code>.
	 * @return Always <code>false</code>
	 */
	@Override
	public boolean isLocalOnly() {
		return false;
	}
	
	protected InetSocketAddress getAddress() {
		return address;
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
	public <SocketType extends Socket> Optional<SocketType> connectSocket(Supplier<SocketType> socket, int timeout)
			throws IOException, SocketTimeoutException, SocketException {
		Objects.requireNonNull(socket, "Socket supplier must not be null");
		SocketType sock = Objects.requireNonNull(socket.get(), "Socket supplier must not return null");
		
		if(sock.isConnected()) throw new SocketException("Socket is already connected");
		
		sock.connect(address, timeout);
		
		return Optional.of(sock);
	}

	@Override
	public <SocketType extends ServerSocket> Optional<SocketType> bindSocket(Supplier<SocketType> socket)
			throws IOException, SocketException {
		Objects.requireNonNull(socket, "Socket supplier must not be null");
		SocketType sock = Objects.requireNonNull(socket.get(), "Socket supplier must not return null");
		
		if(sock.isBound()) throw new SocketException("Socket is already bound");
		
		sock.bind(address);
		
		return Optional.of(sock);
	}
	
	@Override
	public <SocketType extends DatagramSocket> Optional<SocketType> connectDatagram(Supplier<SocketType> socket, int timeout)
			throws IOException, SocketTimeoutException, SocketException {
		Objects.requireNonNull(socket, "Socket supplier must not be null");
		SocketType sock = Objects.requireNonNull(socket.get(), "Socket supplier must not return null");
		
		if(sock.isBound()) throw new SocketException("Socket is already bound");
		
		sock.bind(address);
		
		return Optional.of(sock);
	}

	@Override
	public <SocketType extends DatagramSocket> Optional<SocketType> bindDatagram(Supplier<SocketType> socket) throws IOException, SocketException {
		Objects.requireNonNull(socket, "Socket supplier must not be null");
		SocketType sock = Objects.requireNonNull(socket.get(), "Socket supplier must not return null");
		
		if(sock.isBound()) throw new SocketException("Socket is already bound");
		
		sock.bind(address);
		
		return Optional.of(sock);
	}

	@Override
	public String createConnectionInformation(boolean name, boolean ipData) {
		String text = "Network Target";
		if(name || ipData) text += ": ";
		if(name) text += name;
		if(ipData) text += "Ip/Port: " + address.toString();
		return text;
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
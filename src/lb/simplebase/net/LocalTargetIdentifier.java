package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A {@link TargetIdentifier} that can <b>not</b> be used for Network communication. It does not store a
 * {@link InetSocketAddress} and can only be used to identify a simulated network target inside the program.
 */
class LocalTargetIdentifier implements TargetIdentifier{
	
	private final String localId;
	
	/**
	 * Creates a new {@link LocalTargetIdentifier}
	 * @param localId The unique id that is used for this target
	 */
	public LocalTargetIdentifier(String localId) {
		this.localId = localId;
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
	public <T extends Socket> Optional<T> connectSocket(Supplier<T> socket, int timeout)
			throws IOException, SocketTimeoutException, SocketException {
		return Optional.empty();
	}

	@Override
	public <T extends ServerSocket> Optional<T> bindSocket(Supplier<T> socket) 
			throws IOException, SocketException {
		return Optional.empty();
	}

	@Override
	public <T extends DatagramSocket> Optional<T> connectDatagram(Supplier<T> socket, int timeout)
			throws IOException, SocketTimeoutException, SocketException {
		return Optional.empty();
	}

	@Override
	public <T extends DatagramSocket> Optional<T> bindDatagram(Supplier<T> socket) throws IOException, SocketException {
		return Optional.empty();
	}
	
	@Override
	public String createConnectionInformation(boolean name, boolean ipData) {
		return name ? "Local Target: " + name : "Local Target";
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
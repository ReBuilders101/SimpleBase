package lb.simplebase.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;

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

	//Local implementation never matches network code
	
	@Override
	public boolean matches(String address, int port) {
		return false;
	}

	@Override
	public boolean matches(InetAddress address, int port) {
		return false;
	}

	@Override
	public boolean matches(InetSocketAddress address) {
		return false;
	}
}
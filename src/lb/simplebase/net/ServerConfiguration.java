package lb.simplebase.net;

import java.util.function.BiPredicate;

public class ServerConfiguration {

	private ServerConfiguration()  {
		timeout = 0;
		noDelay = false;
		keepAlive = false;
		acceptor = (a,b) -> true;
		handlerThreads = 0; //Default thread pool
	}
	
	private int timeout;
	private boolean noDelay;
	private boolean keepAlive;
	
	private BiPredicate<NetworkManagerServer, ConnectionInformation> acceptor;
	
	private int handlerThreads;
	
	public int getTimeout() {
		return timeout;
	}
	
	//Amount of threads that will process inbound packets
	public int getHandlerThreadCount() {
		return handlerThreads;
	}
	
	public boolean getNoDelay() {
		return noDelay;
	}
	
	public boolean getKeepAlive() {
		return keepAlive;
	}
	
	public ServerConfiguration setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}
	
	public ServerConfiguration setNoDelay(boolean noDelay) {
		this.noDelay = noDelay;
		return this;
	}
	
	public ServerConfiguration setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	public ServerConfiguration setHandlerThreadCount(int count) {
		this.handlerThreads = count;
		return this;
	}
	
	public ServerConfiguration addConnectionHandler(BiPredicate<NetworkManagerServer, ConnectionInformation> canAccept) {
		acceptor = acceptor.and(canAccept);
		return this;
	}
	
	
	protected boolean canAcceptConnection(NetworkManagerServer nms, ConnectionInformation ca) {
		return acceptor.test(nms, ca);
	}
	
	public ServerConfiguration copy() {
		return new ServerConfiguration()
				.addConnectionHandler(acceptor)
				.setHandlerThreadCount(handlerThreads)
				.setKeepAlive(keepAlive)
				.setNoDelay(noDelay)
				.setTimeout(timeout);
	}
	
	public static ServerConfiguration create() {
		return new ServerConfiguration();
	}
	
}
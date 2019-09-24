package lb.simplebase.net;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

class NioServerConfig extends ServerConfig {

	private final ServerSocketChannel channel;
	
	protected NioServerConfig(ServerSocketChannel soc) throws IOException {
		super(soc == null ? null : soc.socket());
		channel = soc;
	}
	
	protected ServerSocketChannel configuredChannel() {
		return channel;
	}
	
}

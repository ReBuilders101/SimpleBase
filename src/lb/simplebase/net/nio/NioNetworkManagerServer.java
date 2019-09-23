package lb.simplebase.net.nio;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lb.simplebase.net.CommonServer;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.TargetIdentifier;

public class NioNetworkManagerServer extends CommonServer {

	private final ServerSocketChannel channel;
	private final Selector newConnectionSelector;
	private final Selector dataReceiverSelector;
	
	protected NioNetworkManagerServer(TargetIdentifier localId, int threads) {
		super(localId, threads);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void startServer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopServer() {
		// TODO Auto-generated method stub
		
	}
	
	protected SelectionKey registerConnectionChannel(SocketChannel createdConnection) {
		try {
			return createdConnection.register(dataReceiverSelector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			NetworkManager.NET_LOG.error("[Server Manager]: Tried to regsiter a closed channel. Channel data will not be read by server", e);
			return null;
		}
	}

	@Override
	protected void shutdown() {
		stopServer();
	}

}

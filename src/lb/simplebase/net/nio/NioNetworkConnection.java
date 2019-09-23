package lb.simplebase.net.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import lb.simplebase.action.AsyncResult;
import lb.simplebase.net.AsyncNetTask;
import lb.simplebase.net.ConnectionState;
import lb.simplebase.net.NetworkConnection;
import lb.simplebase.net.NetworkManager;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketContext;
import lb.simplebase.net.TargetIdentifier;

public class NioNetworkConnection extends NetworkConnection{

	private final SocketChannel channel;
	private final PacketContext context;
	private final SelectionKey channelServerSelection;
	
	protected NioNetworkConnection(TargetIdentifier local, TargetIdentifier remote, NioNetworkManagerServer packetHandler,
			ConnectionState initialState, boolean isServer, Object payload, SocketChannel acceptedChannel) {
		super(local, remote, packetHandler, initialState, isServer, payload);

		assert acceptedChannel.isBlocking(); //TODO think about blocking mode
		this.channel = acceptedChannel;
		this.context = new NioPacketContext(isServer, packetHandler, this, payload);
		
		channelServerSelection = packetHandler.registerConnectionChannel(getChannel());
		if(channelServerSelection == null) {
			NetworkManager.NET_LOG.fatal("[Network Connection]: Could not register Channel at server manager");
			throw new RuntimeException(); //TODO can we recover from this?
		}
	}

	
	protected SocketChannel getChannel() {
		return channel;
	}


	@Override
	public void connect(int timeout) {
		if(state == ConnectionState.UNCONNECTED)
		try {
			channel.connect(getRemoteTargetId().getConnectionAddress());
		} catch (IOException e) {
			NetworkManager.NET_LOG.error("An IO error occurred while trying to connect the SocketChannel", e);
		}
	}


	@Override
	public AsyncResult sendPacketToTarget(Packet packet) {
		channel.write(src);
	}


	@Override
	public boolean isLocalConnection() {
		return false;
	}


	@Override
	protected PacketContext getContext() {
		return context;
	}
	
	protected  SelectionKey getServerSelectionKey() {
		return channelServerSelection;
	}
}

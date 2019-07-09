package lb.simplebase.net;

import java.util.Set;

import lb.simplebase.net.done.NetworkManagerServer;
import lb.simplebase.net.done.Packet;
import lb.simplebase.net.done.TargetIdentifier;

public class SocketNetworkManagerServer implements NetworkManagerServer{

	@Override
	public PacketSendFuture sendPacketToClient(Packet packet, TargetIdentifier client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<TargetIdentifier> getCurrentClients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<TargetIdentifier> getCurrentClientsLive() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCurrentClient(TargetIdentifier client) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ConnectionStateFuture disconnectClient(TargetIdentifier client) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCurrentClientCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServerStateFuture startServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServerStateFuture stopServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isServerRunning() {
		// TODO Auto-generated method stub
		return false;
	}

}

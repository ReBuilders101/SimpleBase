package lb.simplebase.net.done;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import lb.simplebase.net.ConnectionStateFuture;
import lb.simplebase.net.MultiPacketSendFuture;
import lb.simplebase.net.PacketSendFuture;
import lb.simplebase.net.ServerStateFuture;

@ServerSide
public interface NetworkManagerServer {
	
	public PacketSendFuture sendPacketToClient(Packet packet, TargetIdentifier client);
	public default MultiPacketSendFuture sendPacketToClients(Packet packet, TargetIdentifier...clients) {
		PacketSendFuture[] results = new PacketSendFuture[clients.length];
		for(int i = 0; i < clients.length; i++) {
			TargetIdentifier client = clients[i];
			results[i] = sendPacketToClient(packet, client);
		}
		return MultiPacketSendFuture.of(results);
	}
	public default MultiPacketSendFuture sendPacketToClients(Packet packet, Iterable<TargetIdentifier> clients) {
		List<PacketSendFuture> results = new LinkedList<>();
		for(TargetIdentifier client : clients) {
			results.add(sendPacketToClient(packet, client));
		}
		return MultiPacketSendFuture.of(results);
	}
	public default MultiPacketSendFuture sendPacketToAllClients(Packet packet) {
		return sendPacketToClients(packet, getCurrentClients());
	}
	
	public default MultiPacketSendFuture sendCustomPacketToClients(Function<TargetIdentifier, Packet> mapper, TargetIdentifier...clients) {
		PacketSendFuture[] results = new PacketSendFuture[clients.length];
		for(int i = 0; i < clients.length; i++) {
			TargetIdentifier client = clients[i];
			results[i] = sendPacketToClient(mapper.apply(client), client);
		}
		return MultiPacketSendFuture.of(results);
	}
	public default MultiPacketSendFuture sendCustomPacketToClients(Function<TargetIdentifier, Packet> mapper, Iterable<TargetIdentifier> clients) {
		List<PacketSendFuture> results = new LinkedList<>();
		for(TargetIdentifier client : clients) {
			results.add(sendPacketToClient(mapper.apply(client), client));
		}
		return MultiPacketSendFuture.of(results);
	}
	public default MultiPacketSendFuture sendCustomPacketToAllClients(Function<TargetIdentifier, Packet> mapper) {
		return sendCustomPacketToClients(mapper, getCurrentClients());
	}
	
	/**
	 * @return A static view of the connection set at the time where this method was called
	 */
	public Set<TargetIdentifier> getCurrentClients();
	/**
	 * @return A view of the connection set that reflects all future modifications
	 */
	public Set<TargetIdentifier> getCurrentClientsLive();
	public boolean isCurrentClient(TargetIdentifier client);
	
	public ConnectionStateFuture disconnectClient(TargetIdentifier client);
	public int getCurrentClientCount();
	
	public ServerStateFuture startServer();
	public ServerStateFuture stopServer();
	public boolean isServerRunning();
	
}

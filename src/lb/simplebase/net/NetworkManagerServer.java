package lb.simplebase.net;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import lb.simplebase.action.AsyncResult;
import lb.simplebase.action.AsyncResultGroup;

@ServerSide
public interface NetworkManagerServer extends NetworkManagerCommon{
	
	/**
	 * Sends a packet to one client.<br>
	 * Sending is done on a different thread. To ensure that sending is complete, call
	 * {@link PacketSendFuture#sync()} or {@link PacketSendFuture#ensurePacketSent()}.
	 * @param packet The {@link Packet} that should be sent
	 * @param client The {@link TargetIdentifier} that this packet should be sent to
	 * @return A {@link PacketSendFuture} containing information about sending progress, success and errors
	 */
	public AsyncResult sendPacketToClient(Packet packet, TargetIdentifier client);
	
	public default AsyncResultGroup sendPacketToClients(Packet packet, TargetIdentifier...clients) {
		AsyncResult[] results = new AsyncResult[clients.length];
		for(int i = 0; i < clients.length; i++) {
			TargetIdentifier client = clients[i];
			results[i] = sendPacketToClient(packet, client);
		}
		return new AsyncResultGroup(results);
	}
	public default AsyncResultGroup sendPacketToClients(Packet packet, Iterable<TargetIdentifier> clients) {
		List<AsyncResult> results = new LinkedList<>();
		for(TargetIdentifier client : clients) {
			results.add(sendPacketToClient(packet, client));
		}
		return new AsyncResultGroup((AsyncResult[]) results.toArray());
	}
	public default AsyncResultGroup sendPacketToAllClients(Packet packet) {
		return sendPacketToClients(packet, getCurrentClients());
	}
	
	public default AsyncResultGroup sendCustomPacketToClients(Function<TargetIdentifier, Packet> mapper, TargetIdentifier...clients) {
		AsyncResult[] results = new AsyncResult[clients.length];
		for(int i = 0; i < clients.length; i++) {
			TargetIdentifier client = clients[i];
			results[i] = sendPacketToClient(mapper.apply(client), client);
		}
		return new AsyncResultGroup(results);
	}
	public default AsyncResultGroup sendCustomPacketToClients(Function<TargetIdentifier, Packet> mapper, Iterable<TargetIdentifier> clients) {
		List<AsyncResult> results = new LinkedList<>();
		for(TargetIdentifier client : clients) {
			results.add(sendPacketToClient(mapper.apply(client), client));
		}
		return new AsyncResultGroup((AsyncResult[]) results.toArray());
	}
	public default AsyncResultGroup sendCustomPacketToAllClients(Function<TargetIdentifier, Packet> mapper) {
		return sendCustomPacketToClients(mapper, getCurrentClients());
	}
	
	/**
	 * Creates a set of all remote {@link TargetIdentifier}s of the active connections.
	 * @return All clients connected to the server
	 */
	public Set<TargetIdentifier> getCurrentClients();

	/**
	 * Checks whether this server has a connection to the client.
	 * @param The remote {@link TargetIdentifier} of the client to search for
	 */
	public boolean isCurrentClient(TargetIdentifier client);
	
	/**
	 * Closes the connection to a client and removes it from the client list. To ensure that the connection is closed, call
	 * {@link ConnectionStateFuture#sync()}.
	 * @param client The {@link TargetIdentifier} of the client to remove
	 * @return A {@link ConnectionStateFuture} containing information about progress, success and errors
	 */
	public void disconnectClient(TargetIdentifier client);
	
	/**
	 * Returns the number of client connections that this server has active. 
	 * @return The amount of clients
	 */
	public int getCurrentClientCount();
	
	public void startServer();
	public void stopServer();
	
	/**
	 * The state of the server
	 * @return The state of the server
	 */
	public ServerState getServerState();
}

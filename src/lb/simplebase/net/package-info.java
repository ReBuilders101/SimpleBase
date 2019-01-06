/**
 * This package contains abstract interfaces and classes that can be used for network communication
 * <h3>Basic explaination of the concepts in this package</h3><p>
 * This package contains a basic client - server model.<br>
 * In a network there are severaly network parties. One party is represented by a {@link lb.simplebase.net.TargetIdentifier}.
 * It contains a unique id for the network party (which will often be referred to as target) and optionally information
 * necessary to make a network connection to the target.
 * </p><p>
 * There are two types of connections:
 * <ul><li>
 * The first one is a connection over the network, called remote connection or simply network connection.
 * Data is sent to the partner through a TCP stream based on the {@link java.net} package. To create a remote
 * network connection, the used {@link lb.simplebase.net.TargetIdentifier} must contain a valid url and port.
 * The {@link lb.simplebase.net.TargetIdentifier.NetworkTargetIdentifier} can be used to do this.<br>
 * In case of a remote connection, the connection parties are usually running in different programs/JVMs and
 * often on different computers / servers.
 * </li><li>
 * The second connection type is a local connection. The two connecting parties exist within the same application.
 * This is for example useful for games that support a singleplayer and multiplayer mode. This way, the same code can
 * be used for both modes. A local connection does not need network connection information, and so the
 * {@link lb.simplebase.net.TargetIdentifier.LocalTargetIdentifier} implementation can be used.
 * </li></ul>
 * The class {@link lb.simplebase.net.NetworkConnection} represents both types of connections.
 * <p>
 * A network party or target is represented by an instance of {@link lb.simplebase.net.NetworkManager}.
 * There are two different subclasses for clients an servers,
 * {@link lb.simplebase.net.NetworkManagerClient} and {@link lb.simplebase.net.NetworkManagerServer}
 * This class manages connections and allows you to send data to other targets, and handles received
 * data. This class will be the main interface point between this API and the program.
 * </p><p>
 * Data is sent between targets in packets. A {@link lb.simplebase.net.Packet} contains all data that should
 * be sent and a way to serialize and deserialize it to/from bytes.
 * </p>
 * <h3>Example implementation (local-only)</h3>
 * <ol><li>
 * Create the target identifiers<br>
 * Because two network targets exist within one program, they have to be distinguished by different target identifiers:<br>
 * <code>public static TargetIdentifier serverId = new TargetIdentifier.LocalTargetIdentifier("server");<br>
 * public static TargetIdentifier clientId = new TargetIdentifier.LocalTargetIdentifier("client");</code>
 * They should be declared <code>public static</code> so they are available everywhere in the program.
 * The string parameter should be unique to the TargetIdentifier.
 * </li><li>
 * Create the server network managers<br>
 * <code>public static NetworkManagerServer serverManager = new NetworkManagerServer(reciver, serverId);</code><br>
 * The <i>receiver</i> parameter is a {@link lb.simplebase.net.PacketReceiver} implementation (which is a functional interface).
 * The <i>serverId</i> is the previously declared TargetIdentifier.<br>
 * Note that a NetworkManagerServer that has been created with a local target identifier cannot accept remote connections.
 * </li><li>
 * Create the client network manager<br>
 * <code>public static NetworkManagerClient clientManager = new NetworkManagerClient(receiver, clientId, serverId);</code><br>
 * The <i>receiver</i> parameter is a {@link lb.simplebase.net.PacketReceiver} implementation (which is a functional interface).
 * The <i>clientId</i> is the previously declared TargetIdentifier.<br>
 * The <i>serverId</i> is the previously declared TargetIdentifier.<br>
 * The client needs the connection information of the server.
 * </li><li>
 * Create a connection<br>
 * <code>clientManager.openConnectionToServer();</code><br>
 * After the connection has been created, Packets can be sent from the network managers.
 * </li><li>
 * Sending data with packets
 * <ul><li>
 * From client to server<br>
 * <code>clientManager.sendPacketToServer(packet);</code><br>
 * The packet will be sent to the NetworkManagerServer's <i>packetReceiver</i>, which is set in the constructor.
 * </li><li>
 * From server to client<br>
 * <code>serverManager.sendPacketToClient(packet, clientId)</code><br>
 * Beacuse a server supports multiple connections to clients, the client has to be explicitly specified.
 * The <i>clientId</i> is the previously declared TargetIdentifier.<br>
 * Alternatively, the <code>sendToAllClients(packet)</code> method can be used to send the packet to all connected clients.
 * </li></ul>
 * </li><li>
 * Closing the connection
 * <ul><li>
 * On the client side<br>
 * <code>clientManager.close();</code><br>
 * The connection to the server will be closed.
 * No more packets can be sent to the server, and the <i>packetReceiver</i> wil receive no more packets.
 * </li><li>
 * On the server side<br>
 * <code>serverManager.closeConnectionTo(clientId)</code> will close the connection to this client.<br>
 * <code>serverManager.close();</code> will close all connections to the server.<br>
 * <code>serverManager.shutdown();</code> will close all connection, and no new connections can be made.
 * </li></ul>
 * </li></ol>
 * <h3>Packets</h3><p>
 * To send data, you need at least one {@link lb.simplebase.net.Packet} implementation.
 * A packet can have properties like any other object, and these properties have to be
 * seialized and deserialized with the <code>writeData</code> and <code>readData</code> methods.
 * </p><p>
 * To tell the network manager how to create a new packet from the byte data, and how to create the byte data from a packet,
 * it needs a {@link lb.simplebase.net.PacketIdMapping}. This mapping can be registered at a network manager and must be the same
 * for all targets in the network.<br>
 * The easiest way to create a mapping is with the <code>PacketIdMapping.create(id, packetClass, instanceSupplier);</code><br>
 * The <i>id</i> is a unique integer id for this packet type.<br>
 * The <i>packetClass</i> is the class of the packet implementation.<br>
 * The <i>instanceSupplier</i> is a <code>Supplier&lt;Packet&gt;</code> that creates a new, empty instance of the Packet implementation for every call.<br>
 * Example: <code>PacketIdMapping.create(5, TestPacket.class, TestPacket::new);</code>
 * </p><p>
 * If a program contains many packet type, the PacketIdMappings can be contained in an enum that implements that interface.
 * The enum class can then directly be handed to a network manager to read all containd mappings.
 * </p>
 */
package lb.simplebase.net;
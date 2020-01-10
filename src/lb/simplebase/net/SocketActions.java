package lb.simplebase.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public interface SocketActions<Type> {

	public void connect(SocketAddress endpoint, int timeout) throws IOException, SocketTimeoutException;
	public void bind(SocketAddress endpoint) throws IOException;
	
	public boolean isConnected();
	public boolean isBound();
	public boolean isClosed();
	
	public Type getNetObject();
	
	public static <SocketType extends Socket> SocketActions<SocketType> of(final SocketType socket) {
		return new SocketActions<SocketType>() {

			@Override
			public void connect(SocketAddress endpoint, int timeout) throws IOException, SocketTimeoutException {
				socket.connect(endpoint, timeout);
			}

			@Override
			public void bind(SocketAddress endpoint) throws IOException {
				socket.bind(endpoint);
			}

			@Override
			public boolean isConnected() {
				return socket.isConnected();
			}

			@Override
			public boolean isBound() {
				return socket.isBound();
			}

			@Override
			public boolean isClosed() {
				return socket.isClosed();
			}
			
			@Override
			public SocketType getNetObject() {
				return socket;
			}
		};
	}

	public static <SocketType extends ServerSocket> SocketActions<SocketType> of(final SocketType socket) {
		return new SocketActions<SocketType>() {

			@Override
			public void connect(SocketAddress endpoint, int timeout) throws IOException, SocketTimeoutException {
				return; //can't connect a serverSocket
			}

			@Override
			public void bind(SocketAddress endpoint) throws IOException {
				socket.bind(endpoint);
			}

			@Override
			public boolean isConnected() {
				return false; //Can't be connected
			}

			@Override
			public boolean isBound() {
				return socket.isBound();
			}

			@Override
			public boolean isClosed() {
				return socket.isClosed();
			}

			@Override
			public SocketType getNetObject() {
				return socket;
			}
		};
	}

	public static <SocketType extends DatagramSocket> SocketActions<SocketType> of(final SocketType socket) {
		return new SocketActions<SocketType>() {

			@Override
			public void connect(SocketAddress endpoint, int timeout) throws IOException, SocketTimeoutException {
				socket.connect(endpoint); //ignore time
			}

			@Override
			public void bind(SocketAddress endpoint) throws IOException {
				socket.bind(endpoint);
			}

			@Override
			public boolean isConnected() {
				return socket.isConnected();
			}

			@Override
			public boolean isBound() {
				return socket.isBound();
			}

			@Override
			public boolean isClosed() {
				return socket.isClosed();
			}

			@Override
			public SocketType getNetObject() {
				return socket;
			}
			
		};
	}
	
	public static <ChannelType extends SocketChannel> SocketActions<ChannelType> of(final ChannelType channel) {
		channelDisclaimer(channel);
		
		return new SocketActions<ChannelType>() {

			@Override
			public void connect(SocketAddress endpoint, int timeout) throws IOException, SocketTimeoutException {
				channel.connect(endpoint);
			}

			@Override
			public void bind(SocketAddress endpoint) throws IOException {
				channel.bind(endpoint);
			}

			@Override
			public boolean isConnected() {
				return channel.isConnected();
			}

			@Override
			public boolean isBound() {
				return channel.socket().isBound();
			}

			@Override
			public boolean isClosed() {
				return !channel.isOpen();
			}

			@Override
			public ChannelType getNetObject() {
				return channel;
			}
		};
	}
	
	public static <ChannelType extends ServerSocketChannel> SocketActions<ChannelType> of(final ChannelType channel) {
		channelDisclaimer(channel);
		
		return new SocketActions<ChannelType>() {

			@Override
			public void connect(SocketAddress endpoint, int timeout) throws IOException, SocketTimeoutException {
				return;
			}

			@Override
			public void bind(SocketAddress endpoint) throws IOException {
				channel.bind(endpoint);
			}

			@Override
			public boolean isConnected() {
				return false;
			}

			@Override
			public boolean isBound() {
				return channel.socket().isBound();
			}

			@Override
			public boolean isClosed() {
				return !channel.isOpen();
			}

			@Override
			public ChannelType getNetObject() {
				return channel;
			}
		};
	}

	public static <ChannelType extends DatagramChannel> SocketActions<ChannelType> of(final ChannelType channel) {
		channelDisclaimer(channel);
		
		return new SocketActions<ChannelType>() {

			@Override
			public void connect(SocketAddress endpoint, int timeout) throws IOException, SocketTimeoutException {
				channel.connect(endpoint);
			}

			@Override
			public void bind(SocketAddress endpoint) throws IOException {
				channel.bind(endpoint);
			}

			@Override
			public boolean isConnected() {
				return channel.isConnected();
			}

			@Override
			public boolean isBound() {
				return channel.socket().isBound();
			}

			@Override
			public boolean isClosed() {
				return !channel.isOpen();
			}

			@Override
			public ChannelType getNetObject() {
				return channel;
			}
		};
	}
	
	public static void channelDisclaimer(SelectableChannel channel) {
		if(!channel.isBlocking()) {
			NetworkManager.NET_LOG.debug("SocketActions Channel is in non-blocking mode. Connections may not be finished when method returns");
		}
		NetworkManager.NET_LOG.debug("SocketActions uses a Channel. connect() timeout will me ignored, Channels in blocking mode may wait indefinitely");
		
	}
}

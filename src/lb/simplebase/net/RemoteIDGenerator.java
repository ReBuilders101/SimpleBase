package lb.simplebase.net;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class RemoteIDGenerator implements Function<InetSocketAddress, TargetIdentifier>{
	
	private static final AtomicInteger counter = new AtomicInteger(0);
	
	public static TargetIdentifier generateID(InetSocketAddress address) {
		return new TargetIdentifier.NetworkTargetIdentifier("RemoteTarget-" + counter.getAndIncrement(), address);
	}

	@Override
	public TargetIdentifier apply(InetSocketAddress address) {
		return generateID(address);
	}
	
}

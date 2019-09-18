package lb.simplebase.net;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class RemoteIDGenerator implements Function<InetSocketAddress, TargetIdentifier>{
	
	private static final AtomicInteger counter = new AtomicInteger(0);
	private static BiFunction<InetSocketAddress, Integer, TargetIdentifier> genFunc = null;
	
	public static TargetIdentifier generateID(final InetSocketAddress address) {
		if(genFunc == null) {
			return TargetIdentifier.createNetwork("RemoteTarget-" + counter.getAndIncrement(), address);
		} else {
			return genFunc.apply(address, counter.getAndIncrement());
		}
	}

	public static void setGeneratorFunction(final BiFunction<InetSocketAddress, Integer, TargetIdentifier> func) {
		genFunc = func;
	}
	
	@Override
	public TargetIdentifier apply(final InetSocketAddress address) {
		return generateID(address);
	}
	
}

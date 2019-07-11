package lb.simplebase.net;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MultiPacketSendFuture implements Iterable<PacketSendFuture>, AsyncResult{

	private final List<PacketSendFuture> packetFutures;
	
	protected MultiPacketSendFuture(List<PacketSendFuture> list) {
		packetFutures = list;
	}
	
	public int getPacketCount() {
		return packetFutures.size();
	}
	
	public int getCurrentSuccessCount() { //All sent and no error
		return (int) packetFutures.stream().filter((f) -> f.isDone() && !f.hasError()).count();
	}
	
	public int getCurrentFailureCount() {
		return (int) packetFutures.stream().filter((f) -> f.isDone() && f.hasError()).count();
	}
	
	public boolean ensureAllPacketsSent() throws InterruptedException {
		sync();
		return getCurrentSuccessCount() == getPacketCount();
	}
	
	protected static MultiPacketSendFuture of(PacketSendFuture...futures) {
		return new MultiPacketSendFuture(Arrays.asList(futures));
	}

	protected static MultiPacketSendFuture of(Iterable<PacketSendFuture> futures) {
		List<PacketSendFuture> l = new LinkedList<>();
		for(PacketSendFuture f : futures) {
			l.add(f);
		}
		return new MultiPacketSendFuture(l);
	}

	@Override
	public boolean isQuickFailed() {
		return false;
	}

	@Override
	public void sync() throws InterruptedException {
		for(PacketSendFuture f : packetFutures) {
			f.sync();
		}
	}

	@Override
	public Iterator<PacketSendFuture> iterator() {
		return packetFutures.iterator();
	}

	
}

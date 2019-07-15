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
		return (int) packetFutures.stream().filter((f) -> f.isDone() && !f.isFailed()).count();
	}
	
	public int getDonePacketCount() {
		return (int) packetFutures.stream().filter((f) -> f.isDone()).count();
	}
	
	public int getCurrentFailureCount() {
		return (int) packetFutures.stream().filter((f) -> f.isDone() && f.isFailed()).count();
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
	public MultiPacketSendFuture sync() throws InterruptedException {
		for(PacketSendFuture f : packetFutures) {
			f.sync();
		}
		return this;
	}

	@Override
	public Iterator<PacketSendFuture> iterator() {
		return packetFutures.iterator();
	}

	@Override
	public boolean isFailed() {
		return getCurrentSuccessCount() == 0;
	}

	@Override
	public boolean isSuccess() {
		return getCurrentFailureCount() == 0;
	}

	@Override
	public boolean isDone() {
		return getDonePacketCount() == getPacketCount();
	}

	
}

package lb.simplebase.net.todo;

import java.util.function.Consumer;

import lb.simplebase.net.FutureState;

public class MultiPacketSendFuture extends FutureState{

	protected MultiPacketSendFuture(boolean failed, Consumer<FutureState> asyncTask) {
		super(failed, asyncTask);
		// TODO Auto-generated constructor stub
	}
	
	public static MultiPacketSendFuture of(PacketSendFuture...futures) {
		
	}

	public static MultiPacketSendFuture of(Iterable<PacketSendFuture> futures) {
		
	}

	
}

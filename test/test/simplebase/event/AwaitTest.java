package test.simplebase.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.event.AsyncEventBus;
import lb.simplebase.event.EventHandler;
import lb.simplebase.event.EventPriority;

public class AwaitTest {

	AsyncEventBus BUS;
	
	@BeforeEach
	void setUp() throws Exception {
		BUS = AsyncEventBus.createSingleThread();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() throws InterruptedException {
//		assertEquals(2, BUS.register(this.getClass()));
//		AwaitableEventResult res = BUS.postAwaitable(new TestEvent2(3), EventPriority.DEFAULT);
//		System.out.println("Anytime before");
//		res.awaitPriority();
//		if(!res.isCanceled()) System.out.println("Default");
//		res.getCurrentEvent().tryCancel();
//		res.allowCompletion();
//		System.out.println("Anytime after");
//		
//		System.out.println();
//		BUS.post(new TestEvent2(4));
//		System.out.println("AfterPost");
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public static void handler(TestEvent2 e) {
		System.out.println("High");
	}

	@EventHandler(priority = EventPriority.LOW)
	public static void handler2(TestEvent2 e) {
		System.out.println("Low");
	}
	
}

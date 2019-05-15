package test.simplebase.event;

import lb.simplebase.event.EventHandler;
import lb.simplebase.event.EventPriority;

public final class EventSubscribers {

	private EventSubscribers() {}
	
	static String[] res = new String[3];
	static int idx = 0;
	
	static boolean received = false;
	static boolean received2 = false;
	static boolean cancel;
	
	@EventHandler
	public static void handleTest1(TestEvent1 event) {
		System.out.println("Handled (1): " + event.getMessage());
		res[idx++] = "def" + event.getMessage();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public static void handleTest1Low(TestEvent1 event) {
		System.out.println("Handled (1Low): " + event.getMessage());
		res[idx++] = "low" + event.getMessage();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public static void handleTest1High(TestEvent1 event) {
		System.out.println("Handled (1High): " + event.getMessage());
		res[idx++] = "hig" + event.getMessage();
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public static void cancelHandler(TestEvent2 event) {
		if(cancel) event.tryCancel();
		System.out.println("Tried to cancel: " + cancel);
	}
	
	@EventHandler
	public static void normalHandler(TestEvent2 event) {
		received = true;
		System.out.println("Received normal");
	}
	
	@EventHandler(receiveCancelled = true, priority=EventPriority.LOW)
	public static void alwaysHandler(TestEvent2 event) {
		received2 = true;
		System.out.println("Received low/always");
	}
	
}

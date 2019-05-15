package test.simplebase.event;

import lb.simplebase.event.Event;

public class TestEvent1 extends Event{

	private final String message;
	
	public TestEvent1(String message) {
		super(false);
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}

package test.simplebase.event;

import lb.simplebase.event.Event;

public class TestEvent2 extends Event{
	
	private int num;
	
	public TestEvent2(int num) {
		super(true);
		this.num = num;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
}

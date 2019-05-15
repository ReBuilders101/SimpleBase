package test.simplebase.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.event.EventBus;

class EventTest {

	EventBus bus;
	
	@BeforeEach
	void setUp() throws Exception {
		bus = EventBus.create();
		EventSubscribers.idx = 0;
		EventSubscribers.received = false;
		EventSubscribers.received2 = false;
	}

	@AfterEach
	void tearDown() throws Exception {
		
	}

	@Test
	void registerTest() {
		assertEquals(6, bus.register(EventSubscribers.class));
	}
	
	@Test
	void postTest1() {
		bus.register(EventSubscribers.class);
		String text = "asdf";
		bus.post(new TestEvent1(text)); //Post is synchronous, so all handlers will have run after this call
		assertArrayEquals(new String[] {"hig" + text,  "def" + text, "low" + text }, EventSubscribers.res);
	}
	
	@Test
	void postCancelTest1() {
		bus.register(EventSubscribers.class);
		EventSubscribers.cancel = false;
		bus.post(new TestEvent2(3)); //Post is synchronous, so all handlers will have run after this call
		assertTrue(EventSubscribers.received);
		assertTrue(EventSubscribers.received2);
		
		EventSubscribers.cancel = true;
		EventSubscribers.received = false;
		EventSubscribers.received2 = false;
		
		bus.post(new TestEvent2(3)); //Post is synchronous, so all handlers will have run after this call
		assertFalse(EventSubscribers.received);
		assertTrue(EventSubscribers.received2);
		
		EventSubscribers.received = false;
		EventSubscribers.received2 = false;
		EventSubscribers.idx = 0;
		bus.post(new TestEvent1(""));
		assertFalse(EventSubscribers.received2);
	}

}

package lb.simplebase.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory{

	private final AtomicInteger threadId = new AtomicInteger(0);
	private final String namePrefix;
	
	public NamedThreadFactory(String prefix) {
		namePrefix = prefix;
	}
	
	@Override
	public Thread newThread(Runnable var1) {
		Thread t = new Thread(var1);
		t.setName(namePrefix + threadId.getAndIncrement());
		return t;
	}

}

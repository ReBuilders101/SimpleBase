package lb.simplebase.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	BasicTest.class,
	BufferTest.class,
	LocalNetworkTest.class,
	NetworkTest.class
})
public class AllTests {}

package lb.simplebase.net;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

//package vis
final class TargetIdentifierNameCache {

	private TargetIdentifierNameCache() {}
	
	//Ensures unique names
	private static final Set<String> usedNames = new HashSet<>();
	private static final Object setAccessLock = new Object();
	
	protected static TargetIdentifier createImpl(final Function<String, TargetIdentifier> constructor,
			final String name, final IntFunction<String> nameMapper) {
		synchronized (setAccessLock) {
			String tempName = name;
			int i = 0;
			while(usedNames.contains(tempName)) {
				tempName = nameMapper.apply(i);
				i++;
			}
			usedNames.add(tempName);
			return constructor.apply(tempName);
		}
	}
	
	protected static Set<String> getCache() {
		synchronized (setAccessLock) {
			return usedNames;
		}
	}
	
	protected static Object getLock() {
		return setAccessLock;
	}
}

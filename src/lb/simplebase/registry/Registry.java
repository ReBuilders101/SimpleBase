package lb.simplebase.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public abstract class Registry<T extends RegistryEntry> {
	
	private Map<ResourceLocation, T> entries;
	private Registry<T>.Access defaultAccess;
	
	private Registry(T defaultValue) {
		entries = new HashMap<>();
		defaultValue.setRegistryName(ResourceLocation.DEFAULT);
		entries.put(ResourceLocation.DEFAULT, defaultValue);
		defaultAccess = new Access() {
			@SuppressWarnings("unused")
			private void lock() {} //Unlockable
		};
	}
	
	public Stream<T> getElementStream() {
		return entries.values().stream();
	}
	
	public Map<ResourceLocation, T> getMapView() {
		return Collections.unmodifiableMap(entries);
	}
	
	public Set<T> getEntrySet() {
		return new HashSet<>(entries.values());
	}
	
	public T getEntry(ResourceLocation location) {
		return entries.get(location);
	}
	
	public T getEntryOrDefault(ResourceLocation location) {
		if(entries.containsKey(location)) { //Map's getOrDefault() is not used on purpose
			return entries.get(location);
		} else {
			return entries.get(ResourceLocation.DEFAULT);
		}
	}
	
	public boolean containsEntryFor(ResourceLocation location) {
		return entries.containsKey(location);
	}
	
	protected Access createAccess() {
		return new Access();
	}
	
	protected Access getDefaultAccess() {
		return defaultAccess;
	}
	
//	public static <T extends IRegistryEntry> Registry<T> createRegistry(T defaultValue) {
//		if(defaultValue.hasRegistryName()) return null;
//		return new Registry<T>(defaultValue);
//	}
	
	public class Access {
		
		private boolean locked;
		
		private Access() {
			locked = false;
		}
		
		@SuppressWarnings("unused")
		private void lock() {
			locked = true;
		}
		
		public boolean register(T entry) {
			if(locked) return false;
			if(entry.hasRegistryName()) {
				ResourceLocation key = entry.getRegistryName();
				if(entries.containsKey(key) || key.isEqual(ResourceLocation.DEFAULT)) { //Must not be already registered or default
					return false;
				} else {
					entries.put(key, entry);
					return true;
				}
			} else {
				return false;
			}
		}	
	}
}

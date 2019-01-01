package lb.simplebase.function;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;


public class EventHandlerMap<I,H> extends HashMap<I,Set<H>>{
	private static final long serialVersionUID = -46577653235910497L;

	public void addHandler(I id, H handler) {
		Set<H> handlers = get(id);
		if(handlers == null) {
			handlers = new HashSet<>();
			put(id,handlers);
		}
		handlers.add(handler);
	}
	
	public void removeHandler(I id, H handler) {
		if(hasHandlers(id)) {
			get(id).remove(handler);
		}
	}
	
	public void removeAllHandlers(I id) {
		if(hasHandlers(id)) {
			get(id).removeIf((h) -> true);
		}
	}
	
	public void forEachHandler(I id, Consumer<? super H> action) {
		if(hasHandlers(id)) {
			get(id).forEach(action);
		}
	}
	
	public boolean hasHandlers(I id) {
		return get(id) != null && !get(id).isEmpty();
	}
	
}

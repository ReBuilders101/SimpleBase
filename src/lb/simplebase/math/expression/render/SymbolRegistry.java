package lb.simplebase.math.expression.render;

import java.util.HashMap;

import lb.simplebase.math.expression.ExpressionElement;

public final class SymbolRegistry {
	
	private HashMap<Class<? extends ExpressionElement>, RenderSymbol<?>> mappings;
	private static final SymbolRegistry INSTANCE = new SymbolRegistry();
	
	private SymbolRegistry() {
		mappings = new HashMap<>();
		applyPatch(new DefaultPatch());
	}
	
	public static SymbolRegistry getRegistry() {
		return INSTANCE;
	}
	
	public <T extends ExpressionElement> boolean addMapping(Class<T> type, RenderSymbol<T> mapping) {
		if(mappings.containsKey(type)) {
			return false;
		} else {
			mappings.put(type, mapping);
			return true;
		}
	}
	
	public <T extends ExpressionElement> void overrrideMapping(Class<T> type, RenderSymbol<T> mapping) {
		mappings.put(type, mapping);
	}
	
	public static void applyPatch(SymbolRegistryPatch patch) {
		patch.applyPatch(INSTANCE);
	}

	@SuppressWarnings("unchecked")
	public <T extends ExpressionElement> RenderSymbol<T> getMapping(Class<T> type){
		return (RenderSymbol<T>) mappings.get(type);
	}
	
	private static class DefaultPatch implements SymbolRegistryPatch {

		@Override
		public void applyPatch(SymbolRegistry instance) {
			
		}
		
	}
	
}

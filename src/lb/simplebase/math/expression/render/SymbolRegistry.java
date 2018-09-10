package lb.simplebase.math.expression.render;

import java.util.HashMap;

import lb.simplebase.math.expression.ExpressionElement;
import lb.simplebase.math.expression.IntegerElement;

public final class SymbolRegistry {
	
	private HashMap<Class<? extends ExpressionElement>, RenderSymbol<?>> mappings;
	private static final SymbolRegistry INSTANCE = new SymbolRegistry();
	
	private SymbolRegistry() {
		mappings = new HashMap<>();
		//Add default mappings
		addMapping(IntegerElement.class, RenderNumber.INT_RENDER_NORMAL);
//		addMapping(NumberElement.class, RenderNumber.INT_RENDER_NORMAL);
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

	@SuppressWarnings("unchecked")
	public <T extends ExpressionElement> RenderSymbol<T> getMapping(Class<T> type){
		return (RenderSymbol<T>) mappings.get(type);
	}
	
}

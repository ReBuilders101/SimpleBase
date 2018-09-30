package lb.simplebase.math.expression;

import java.io.Serializable;
import java.util.Map;

import lb.simplebase.math.expression.render.RenderSymbol;
import lb.simplebase.math.expression.render.SymbolRegistry;

public interface ExpressionElement extends Serializable{
	
	public default RenderSymbol<?> getSymbol() {
		return SymbolRegistry.getRegistry().getMapping(this.getClass());
	};

	public ExpressionElement replaceVariable(Variable variable, ExpressionElement value);
	
	public ExpressionElement replaceVariables(Map<Variable, ExpressionElement> variable2value);
	
	public ExpressionElement simplify();
	
	public boolean isSimplified();
	
	public Variable[] getVariables();
	
}

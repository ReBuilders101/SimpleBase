package lb.simplebase.math.expression;

import lb.simplebase.math.expression.render.RenderSymbol;

public interface ExpressionElement {
	public RenderSymbol<?> getSymbol();
	public boolean hasValue();
}

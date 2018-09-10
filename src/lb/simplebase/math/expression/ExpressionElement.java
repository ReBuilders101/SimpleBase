package lb.simplebase.math.expression;

import java.io.Serializable;

import lb.simplebase.math.expression.render.RenderSymbol;

public interface ExpressionElement extends Serializable{
	public RenderSymbol<?> getSymbol();
	public boolean hasValue();
}

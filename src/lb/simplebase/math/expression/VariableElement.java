package lb.simplebase.math.expression;

public interface VariableElement extends ExpressionElement{

	public int getVariableCount();
	
	public default boolean hasVariables() {
		return getVariableCount() != 0;
	}

	@Override
	public default boolean hasValue() {
		return !hasVariables();
	}
	
}

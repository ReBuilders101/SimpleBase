package lb.simplebase.math.expression;

import java.math.BigDecimal;

public interface CalculatableElement extends ExpressionElement{
	public BigDecimal getValue();
	
	@Override
	public default boolean hasValue() {
		return true;
	}
}

package lb.simplebase.math.expression;

import java.math.BigDecimal;

public interface CalculatableElement extends ExpressionElement, Comparable<CalculatableElement>{
	public BigDecimal getValue();
	
	@Override
	public default boolean hasValue() {
		return true;
	}
	
	@Override
	default int compareTo(CalculatableElement o) {
		return getValue().compareTo(o.getValue());
	}
}

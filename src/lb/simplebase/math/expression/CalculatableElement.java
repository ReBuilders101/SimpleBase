package lb.simplebase.math.expression;

import java.math.BigDecimal;

import lb.simplebase.math.BigComplexNumber;

public interface CalculatableElement extends ExpressionElement, Comparable<CalculatableElement>{
	public BigDecimal getValue();
	
	@Override
	public default boolean hasValue() {
		return true;
	}
	
	@Override
	public default int compareTo(CalculatableElement o) {
		return getValue().compareTo(o.getValue());
	}

	@Override
	public default BigComplexNumber getValue(Object... variables) {
		return new BigComplexNumber(getValue());
	}
}

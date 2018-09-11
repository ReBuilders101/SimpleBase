package lb.simplebase.math.expression.complex;

import lb.simplebase.math.BigComplexNumber;
import lb.simplebase.math.expression.ExpressionElement;

public interface ComplexCalculatableElement extends ExpressionElement{
	public BigComplexNumber getValue();
	
	@Override
	public default boolean hasValue() {
		return true;
	}
}

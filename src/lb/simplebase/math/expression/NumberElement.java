package lb.simplebase.math.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import lb.simplebase.math.expression.render.RenderSymbol;
import lb.simplebase.math.expression.render.SymbolRegistry;

public class NumberElement implements CalculatableElement{

	private static final long serialVersionUID = -1909384316766936204L;

	private BigDecimal bigDec;
	
	//For serialization
	protected NumberElement() {}
	
	public NumberElement(double number) {
		bigDec = BigDecimal.valueOf(number);
	}
	
	public NumberElement(String number) {
		bigDec = new BigDecimal(number);
	}
	
	public NumberElement(BigDecimal big) {
		bigDec = big; //This is ok because BigInteger is immutable
	}
	
	public NumberElement(BigInteger bigInt) {
		bigDec = new BigDecimal(bigInt);
	}

	public static NumberElement valueOf(double number) {
		return new NumberElement(number);
	}
	
	public static NumberElement valueOf(BigDecimal bigDec) {
		return new NumberElement(bigDec);
	}
	
	//Implemented Methods
	
	@Override
	public RenderSymbol<NumberElement> getSymbol() {
		return SymbolRegistry.getRegistry().getMapping(NumberElement.class);
	}

	@Override
	public BigDecimal getValue() {
		return bigDec;
	}
	
}

package lb.simplebase.math.expression;

import java.math.BigDecimal;
import java.math.BigInteger;

import lb.simplebase.math.expression.render.RenderSymbol;
import lb.simplebase.math.expression.render.SymbolRegistry;

/**
 * @version 1.0
 * @author LB
 * This class represents a integer number in a mathematical expression. 
 */
@Deprecated
public class IntegerElement implements CalculatableElement {

	private static final long serialVersionUID = -999541132842381945L;
	
	private BigInteger bigInt;
	
	public IntegerElement(long number) {
		bigInt = BigInteger.valueOf(number);
	}
	
	public IntegerElement(String number) {
		bigInt = new BigInteger(number);
	}
	
	public IntegerElement(BigInteger big) {
		bigInt = big; //This is ok because BigInteger is immutable
	}
	
	protected IntegerElement(byte[] bytes) {
		bigInt = new BigInteger(bytes);
	}

	public static IntegerElement valueOf(long number) {
		return new IntegerElement(number);
	}
	
	public static IntegerElement valueOf(BigInteger bigInt) {
		return new IntegerElement(bigInt);
	}

	//Implemented Methods
	
	@Override
	public RenderSymbol<IntegerElement> getSymbol() {
		return SymbolRegistry.getRegistry().getMapping(IntegerElement.class);
	}

	@Override
	public BigDecimal getValue() {
		return new BigDecimal(bigInt);
	}

	
}

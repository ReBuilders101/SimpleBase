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
public class IntegerElement extends BigInteger implements CalculatableElement{

	private static final long serialVersionUID = -999541132842381945L;
	
	public IntegerElement(long number) {
		super(BigInteger.valueOf(number).toByteArray());
	}
	
	public IntegerElement(String number) {
		super(number);
	}
	
	public IntegerElement(BigInteger bigInt) {
		super(bigInt.toByteArray());
	}
	
	protected IntegerElement(byte[] bytes) {
		super(bytes);
	}

	public static IntegerElement valueOf(long number) {
		return new IntegerElement(number);
	}
	
	public static IntegerElement valueOf(BigInteger bigInt) {
		return new IntegerElement(bigInt.toByteArray());
	}

	//Implemented Methods
	
	@Override
	public RenderSymbol<IntegerElement> getSymbol() {
		return SymbolRegistry.getRegistry().getMapping(IntegerElement.class);
	}

	@Override
	public BigDecimal getValue() {
		return new BigDecimal(this);
	}

	
}

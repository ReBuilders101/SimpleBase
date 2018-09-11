package lb.simplebase.math.expression.complex;

import java.math.BigDecimal;

import lb.simplebase.math.BigComplexNumber;
import lb.simplebase.math.expression.render.RenderSymbol;
import lb.simplebase.math.expression.render.SymbolRegistry;

public class ComplexNumberElement implements ComplexCalculatableElement{
	private static final long serialVersionUID = -4649446552581723740L;

	private BigComplexNumber bigCom;
	
	public ComplexNumberElement(BigDecimal realPart) {
		bigCom = new BigComplexNumber(realPart);
	}
	
	public ComplexNumberElement(BigComplexNumber number) {
		bigCom = number;
	}

	@Override
	public RenderSymbol<ComplexNumberElement> getSymbol() {
		return SymbolRegistry.getRegistry().getMapping(ComplexNumberElement.class);
	}

	@Override
	public BigComplexNumber getValue() {
		return bigCom;
	}

}

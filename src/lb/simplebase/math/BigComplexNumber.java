package lb.simplebase.math;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import lb.simplebase.math.LimitedPrecision.Precision;

public class BigComplexNumber implements Serializable{
	
	private static final long serialVersionUID = 6152524676870394933L;

	public static final BigComplexNumber ZERO = new BigComplexNumber(0);
	public static final BigComplexNumber ONE = new BigComplexNumber(1);
	public static final BigComplexNumber NEGATIVE_ONE = new BigComplexNumber(-1);
	public static final BigComplexNumber POSITIVE_I = new BigComplexNumber(0, 1);
	public static final BigComplexNumber NEGATIVE_I = new BigComplexNumber(0, -1);
	
	private BigDecimal real, imag;
	
	//For Serialization
	protected BigComplexNumber() {}
	
	public BigComplexNumber(double realNumber) {
		real = BigDecimal.valueOf(realNumber);
		imag = BigDecimal.valueOf(0);
	}
	
	public BigComplexNumber(long realNumber) {
		real = BigDecimal.valueOf(realNumber);
		imag = BigDecimal.valueOf(0);
	}
	
	public BigComplexNumber(double realPart, double imaginaryPart) {
		real = BigDecimal.valueOf(realPart);
		imag = BigDecimal.valueOf(imaginaryPart);
	}
	
	public BigComplexNumber(long realPart, long imaginaryPart) {
		real = BigDecimal.valueOf(realPart);
		imag = BigDecimal.valueOf(imaginaryPart);
	}
	
	public BigComplexNumber(BigDecimal realNumber) {
		real = realNumber;
		imag = BigDecimal.valueOf(0);
	}
	
	public BigComplexNumber(BigInteger realNumber) {
		real = new BigDecimal(realNumber);
		imag = BigDecimal.valueOf(0);
	}
	
	public BigComplexNumber(BigDecimal realPart, BigDecimal imaginaryPart) {
		real = realPart;
		imag = imaginaryPart;
	}
	
	public BigComplexNumber(BigInteger realPart, BigInteger imaginaryPart) {
		real = new BigDecimal(realPart);
		imag = new BigDecimal(imaginaryPart);
	}
	
	public static BigComplexNumber getAsImaginaryPart(BigDecimal value) {
		return new BigComplexNumber(BigDecimal.valueOf(0), value);
	}
	
	public static BigComplexNumber getAsImaginaryPart(BigInteger value) {
		return new BigComplexNumber(BigInteger.valueOf(0), value);
	}
	
	public static BigComplexNumber getAsImaginaryPart(long value) {
		return new BigComplexNumber(0L, value);
	}
	
	public static BigComplexNumber getAsImaginaryPart(double value) {
		return new BigComplexNumber(0D, value);
	}
	
	public BigDecimal getRealPart() {
		return real;
	}
	
	public BigDecimal getImaginaryPart() {
		return imag;
	}
	
	public BigComplexNumber getRealOnly() {
		return new BigComplexNumber(real);
	}
	
	public BigComplexNumber getImaginaryOnly() {
		return BigComplexNumber.getAsImaginaryPart(imag);
	}
	
	public BigComplexNumber getComplexConjugate() {
		return new BigComplexNumber(real, imag.negate());
	}
	
	public BigComplexNumber negate() {
		return new BigComplexNumber(real.negate(), imag.negate());
	}
	
	public BigComplexNumber getReciprocal() {
		BigDecimal newReal = real.divide(real.pow(2).add(imag.pow(2)));
		BigDecimal newImag = imag.divide(real.pow(2).add(imag.pow(2)));
		return new BigComplexNumber(newReal, newImag.negate());
	}
	
	//TODO
	public BigComplexNumber sqrt() {
//		BigDecimal gamma = real.add( real.pow(2).add(imag.pow(2)).sqrt() ).divide(BigDecimal.valueOf(2)).sqrt(); //Java 9 adds sqrt
//		BigDecimal delta0 = real.negate().add( real.pow(2).add(imag.pow(2)).sqrt() ).divide(BigDecimal.valueOf(2)).sqrt(); //Java 9 adds sqrt;
//		BigDecimal delta = delat0.multiply(BigDecimal.valueOf(imag.signum()));
//		return new BigComplexNumber(gamma, delta);
		return null;
	}
	
	public BigComplexNumber getNegated() {
		return negate();
	}
	
	public BigComplexNumber getSquareRoot() {
		return sqrt();
	}
	
	public BigComplexNumber getSecondSqareRoot() {
		return getSquareRoot().negate();
	}
	
	@ArraySize(2)
	public BigComplexNumber[] getSquareRoots() {
		return new BigComplexNumber[] { getSquareRoot(), getSecondSqareRoot() };
	}
	
	public boolean hasRealPart() {
		return !real.equals(BigDecimal.ZERO);
	}
	
	public boolean hasImaginaryPart() {
		return !imag.equals(BigDecimal.ZERO);
	}

	public boolean isZero() {
		return real.equals(BigDecimal.ZERO) && imag.equals(BigDecimal.ZERO);
	}
	
	@LimitedPrecision(Precision.DOUBLE)
	public BigDecimal getPolarAngle() {
		return BigDecimal.valueOf(Math.atan2(imag.doubleValue(), real.doubleValue()));
	}
	
	@LimitedPrecision(Precision.DOUBLE)
	public BigDecimal getPolarAngleDegrees() {
		return BigDecimal.valueOf(Math.toDegrees(getPolarAngle().doubleValue()));
	}
	
	@LimitedPrecision(Precision.DOUBLE)
	public BigDecimal getOriginDistance() {
		BigDecimal sum = real.pow(2).add(imag.pow(2));
		return new BigDecimal(Math.sqrt(sum.doubleValue()));
	}
	
	//Operations
	
	public BigComplexNumber add(BigComplexNumber toAdd) {
		return new BigComplexNumber(real.add(toAdd.real), imag.add(toAdd.imag));
	}
	
	public BigComplexNumber addReal(BigDecimal toAdd) {
		return add(new BigComplexNumber(toAdd));
	}
	
	public BigComplexNumber addImaginary(BigDecimal toAdd) {
		return add(BigComplexNumber.getAsImaginaryPart(toAdd));
	}
	
	public BigComplexNumber addReal(BigInteger toAdd) {
		return add(new BigComplexNumber(toAdd));
	}
	
	public BigComplexNumber addImaginary(BigInteger toAdd) {
		return add(BigComplexNumber.getAsImaginaryPart(toAdd));
	}
	
	public BigComplexNumber addReal(double toAdd) {
		return add(new BigComplexNumber(toAdd));
	}
	
	public BigComplexNumber addImaginary(double toAdd) {
		return add(BigComplexNumber.getAsImaginaryPart(toAdd));
	}
	public BigComplexNumber addReal(long toAdd) {
		return add(new BigComplexNumber(toAdd));
	}
	
	public BigComplexNumber addImaginary(long toAdd) {
		return add(BigComplexNumber.getAsImaginaryPart(toAdd));
	}
	
	public BigComplexNumber subtract(BigComplexNumber toSubtract) {
		return new BigComplexNumber(real.subtract(toSubtract.real), imag.subtract(toSubtract.imag));
	}
	
	public BigComplexNumber subtractReal(BigDecimal toSubtract) {
		return subtract(new BigComplexNumber(toSubtract));
	}
	
	public BigComplexNumber subtractImaginary(BigDecimal toSubtract) {
		return subtract(BigComplexNumber.getAsImaginaryPart(toSubtract));
	}
	
	public BigComplexNumber subtractReal(BigInteger toSubtract) {
		return subtract(new BigComplexNumber(toSubtract));
	}
	
	public BigComplexNumber subtractImaginary(BigInteger toSubtract) {
		return subtract(BigComplexNumber.getAsImaginaryPart(toSubtract));
	}
	
	public BigComplexNumber subtractReal(double toSubtract) {
		return subtract(new BigComplexNumber(toSubtract));
	}
	
	public BigComplexNumber subtractImaginary(double toSubtract) {
		return subtract(BigComplexNumber.getAsImaginaryPart(toSubtract));
	}
	public BigComplexNumber subtractReal(long toSubtract) {
		return subtract(new BigComplexNumber(toSubtract));
	}
	
	public BigComplexNumber subtractImaginary(long toSubtract) {
		return subtract(BigComplexNumber.getAsImaginaryPart(toSubtract));
	}
	
	public BigComplexNumber multiply(BigComplexNumber toMultiply) {
		//Implementation unlike add/subtract
		BigDecimal a = real;
		BigDecimal b = imag;
		BigDecimal c = toMultiply.real;
		BigDecimal d = toMultiply.imag;
		BigDecimal newReal = a.multiply(c).subtract(b.multiply(d));
		BigDecimal newImag = b.multiply(c).add(a.multiply(d));
		return new BigComplexNumber(newReal, newImag);
	}
	
	public BigComplexNumber multiplyReal(BigDecimal toMultiply) {
		return multiply(new BigComplexNumber(toMultiply));
	}
	
	public BigComplexNumber multiplyImaginary(BigDecimal toMultiply) {
		return multiply(BigComplexNumber.getAsImaginaryPart(toMultiply));
	}
	
	public BigComplexNumber multiplyReal(BigInteger toMultiply) {
		return multiply(new BigComplexNumber(toMultiply));
	}
	
	public BigComplexNumber multiplyImaginary(BigInteger toMultiply) {
		return multiply(BigComplexNumber.getAsImaginaryPart(toMultiply));
	}
	
	public BigComplexNumber multiplyReal(double toMultiply) {
		return multiply(new BigComplexNumber(toMultiply));
	}
	
	public BigComplexNumber multiplyImaginary(double toMultiply) {
		return multiply(BigComplexNumber.getAsImaginaryPart(toMultiply));
	}
	public BigComplexNumber multiplyReal(long toMultiply) {
		return multiply(new BigComplexNumber(toMultiply));
	}
	
	public BigComplexNumber multiplyImaginary(long toMultiply) {
		return multiply(BigComplexNumber.getAsImaginaryPart(toMultiply));
	}
	
	public BigComplexNumber divide(BigComplexNumber toDivideBy) {
		//Implementation unlike add/subtract
		BigDecimal a = real;
		BigDecimal b = imag;
		BigDecimal c = toDivideBy.real;
		BigDecimal d = toDivideBy.imag;
		BigDecimal newReal = a.multiply(c).add(b.multiply(d))
				.divide(c.pow(2).add(d.pow(2)));
		BigDecimal newImag = b.multiply(c).subtract(a.multiply(d))
				.divide(c.pow(2).add(d.pow(2)));
		return new BigComplexNumber(newReal, newImag);
	}
	
	public BigComplexNumber divideReal(BigDecimal toDivideBy) {
		return divide(new BigComplexNumber(toDivideBy));
	}
	
	public BigComplexNumber divideImaginary(BigDecimal toDivideBy) {
		return divide(BigComplexNumber.getAsImaginaryPart(toDivideBy));
	}
	
	public BigComplexNumber divideReal(BigInteger toDivideBy) {
		return divide(new BigComplexNumber(toDivideBy));
	}
	
	public BigComplexNumber divideImaginary(BigInteger toDivideBy) {
		return divide(BigComplexNumber.getAsImaginaryPart(toDivideBy));
	}
	
	public BigComplexNumber divideReal(double toDivideBy) {
		return divide(new BigComplexNumber(toDivideBy));
	}
	
	public BigComplexNumber divideImaginary(double toDivideBy) {
		return divide(BigComplexNumber.getAsImaginaryPart(toDivideBy));
	}
	public BigComplexNumber divideReal(long toDivideBy) {
		return divide(new BigComplexNumber(toDivideBy));
	}
	
	public BigComplexNumber divideImaginary(long toDivideBy) {
		return divide(BigComplexNumber.getAsImaginaryPart(toDivideBy));
	}
	
	//Generated Methods
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imag == null) ? 0 : imag.hashCode());
		result = prime * result + ((real == null) ? 0 : real.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BigComplexNumber other = (BigComplexNumber) obj;
		if (imag == null) {
			if (other.imag != null)
				return false;
		} else if (!imag.equals(other.imag))
			return false;
		if (real == null) {
			if (other.real != null)
				return false;
		} else if (!real.equals(other.real))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return real.toString() + " + " + imag.toString() + "i";
	}
	
}

package lb.simplebase.math;

import java.io.Serializable;

public class ComplexNumber implements Serializable{

	private static final long serialVersionUID = -6730969600626313473L;

	public static final ComplexNumber ZERO = new ComplexNumber(0);
	public static final ComplexNumber ONE = new ComplexNumber(1);
	public static final ComplexNumber NEGATIVE_ONE = new ComplexNumber(-1);
	public static final ComplexNumber POSITIVE_I = new ComplexNumber(0, 1);
	public static final ComplexNumber NEGATIVE_I = new ComplexNumber(0, -1);
	
	private double real;
	private double imag;
	
	protected ComplexNumber() {
		real = 0;
		imag = 0;
	}
	
	public ComplexNumber(double number) {
		real = number;
		imag = 0;
	}
	
	public ComplexNumber(double realPart, double imaginaryPart) {
		real = realPart;
		imag = imaginaryPart;
	}
	
	public static ComplexNumber getAsImaginaryPart(double imaginaryPart) {
		return new ComplexNumber(0, imaginaryPart);
	}
	
	public double getRealPart() {
		return real;
	}
	
	public double getImaginaryPart() {
		return imag;
	}
	
	public ComplexNumber getRealOnly() {
		return new ComplexNumber(real);
	}
	
	public ComplexNumber getImaginaryOnly() {
		return ComplexNumber.getAsImaginaryPart(imag);
	}
	
	public ComplexNumber getComplexConjugate() {
		return new ComplexNumber(real, -imag);
	}
	
	public ComplexNumber negate() {
		return new ComplexNumber(-real, -imag);
	}
	
	public ComplexNumber getReciprocal() {
		return new ComplexNumber(real / (real*real - imag*imag), -(imag / (real*real - imag*imag)));
	}
	
	public ComplexNumber sqrt() {
		double gamma = Math.sqrt((real + Math.sqrt(real*real + imag*imag)) / 2 );
		double delta0 = Math.sqrt((-real + Math.sqrt(real*real + imag*imag)) / 2 );
		double delta = delta0 * Math.signum(imag);
		return new ComplexNumber(gamma, delta);
	}
	
	public ComplexNumber getNegated() {
		return negate();
	}
	
	public ComplexNumber getSquareRoot() {
		return sqrt();
	}
	
	public ComplexNumber getSecondSqareRoot() {
		return getSquareRoot().negate();
	}
	
	@ArraySize(2)
	public ComplexNumber[] getSquareRoots() {
		return new ComplexNumber[] { getSquareRoot(), getSecondSqareRoot() };
	}
	
	public boolean hasRealPart() {
		return !(real == 0);
	}
	
	public boolean hasImaginaryPart() {
		return !(imag == 0);
	}

	public boolean isZero() {
		return real == 0 && imag == 0;
	}
	
	public double getPolarAngle() {
		return Math.atan2(imag, real);
	}
	
	public double getPolarAngleDegrees() {
		return Math.toDegrees(getPolarAngle());
	}
	
	public double getOriginDistance() {
		double sum = real*real + imag*imag;
		return Math.sqrt(sum);
	}
	
	public ComplexNumber add(ComplexNumber toAdd) {
		return new ComplexNumber(real + toAdd.real, imag + toAdd.real);
	}
	
	public ComplexNumber addReal(double toAdd) {
		return add(new ComplexNumber(toAdd));
	}
	
	public ComplexNumber addImaginary(double toAdd) {
		return add(ComplexNumber.getAsImaginaryPart(toAdd));
	}
	
	public ComplexNumber subtract(ComplexNumber toSubtract) {
		return new ComplexNumber(real + toSubtract.real, imag + toSubtract.real);
	}
	
	public ComplexNumber subtractReal(double toSubtract) {
		return add(new ComplexNumber(toSubtract));
	}
	
	public ComplexNumber subtractImaginary(double toSubtract) {
		return add(ComplexNumber.getAsImaginaryPart(toSubtract));
	}
	
	public ComplexNumber multiply(ComplexNumber toMultiply) {
		double a = real;
		double b = imag;
		double c = toMultiply.real;
		double d = toMultiply.imag;
		return new ComplexNumber(a*c - b* d, b*c + a*d);
	}
	
	public ComplexNumber multiplyReal(double toMultiply) {
		return multiply(new ComplexNumber(toMultiply));
	}
	
	public ComplexNumber multiplyImaginary(double toMultiply) {
		return multiply(ComplexNumber.getAsImaginaryPart(toMultiply));
	}
	
	public ComplexNumber divideBy(ComplexNumber toDivideBy) {
		double a = real;
		double b = imag;
		double c = toDivideBy.real;
		double d = toDivideBy.imag;
		return new ComplexNumber((a*c + b*d) / (c*c + d*d), (b*c - a*d) / (c*c + d*d));
	}
	
	public ComplexNumber divideByReal(double toDivideBy) {
		return divideBy(new ComplexNumber(toDivideBy));
	}
	
	public ComplexNumber divideByImaginary(double toDivideBy) {
		return divideBy(ComplexNumber.getAsImaginaryPart(toDivideBy));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(imag);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(real);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		ComplexNumber other = (ComplexNumber) obj;
		if (Double.doubleToLongBits(imag) != Double.doubleToLongBits(other.imag))
			return false;
		if (Double.doubleToLongBits(real) != Double.doubleToLongBits(other.real))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return real + " + " + imag + "i";
	}
	
}

package lb.simplebase.linalg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Line2D {

	private final Vector2D baseVector;
	private final Vector2D directionVector;
	
	//make these transient?
	private final double implicitA;
	private final double implicitB;
	private final double implicitC;
	
	
	private Line2D(Vector2D baseVector, Vector2D directionVector) {
		if(directionVector.isZeroVector()) throw new IllegalArgumentException("Direction vector of a line must not be (0;0)");
		
		this.baseVector = baseVector;
		this.directionVector = directionVector;
		
		this.implicitA = -directionVector.getY();
		this.implicitB = directionVector.getX();
		this.implicitC = Matrix2D.columnDeterminant(baseVector, directionVector);
	}
	
	public boolean intersects(Vector2D point) {
		return intersects(point.getX(), point.getY());
	}
	
	public boolean intersects(Point2D point) {
		return intersects(point.getX(), point.getY());
	}
	
	public boolean intersects(double pointX, double pointY) {
		return implicitA * pointX + implicitB * pointY == implicitC;
	}
	
	public Vector2D getParametricValue(double t) {
		return baseVector.add(directionVector.scale(t));
	}
	
	public double getEquationA() {
		return implicitA;
	}
	
	public double getEquationB() {
		return implicitB;
	}
	
	public double getEquationC() {
		return implicitC;
	}
	
	public Vector2D getIntersectPoint(Line2D otherLine) {
		final double top = Matrix2D.columnDeterminant(otherLine.baseVector.subtract(this.baseVector), otherLine.directionVector);
		final double bottom = Matrix2D.columnDeterminant(this.directionVector, otherLine.directionVector);
		// top/bottom is t for this line
		if(bottom == 0) return null; //No intersection
		return getParametricValue(top / bottom);
	}
	
	public boolean isValidFunction() {
		return implicitB != 0;
	}
	
	public boolean isParallelTo(Line2D otherLine) {
		return this.directionVector.isParallel(otherLine.directionVector);
	}
	
	public boolean isEqualTo(Line2D otherLine) {
		return this.isParallelTo(otherLine) && this.intersects(otherLine.baseVector);
	}
	
	public double getSlope() {
		if(directionVector.getX() == 0) return Double.NaN;
		return directionVector.getX() / directionVector.getY();
	}
	
	public Vector2D getBaseVector() {
		return baseVector;
	}
	
	public Vector2D getDirectionVector() {
		return directionVector;
	}
	
	public Vector2D getDirectionUnitVector() {
		return directionVector.normalize();
	}
	
	public Line2D transform(Matrix2D transformation) {
		return new Line2D(baseVector.transform(transformation), directionVector.transform(transformation));
	}
	
	public Line2D transformAffine(Matrix2D transformation, Vector2D translation) {
		return new Line2D(baseVector.transform(transformation).add(translation), directionVector.transform(transformation).add(translation));
	}
	
	public Line2D transformAffine(AffineTransform transformation) {
		return transformAffine(Matrix2D.of(transformation), Vector2D.ofTranslation(transformation));
	}
	
	public static Line2D of(Vector2D baseVector, Vector2D directionVector) {
		return new Line2D(baseVector, directionVector);
	}
	
	public static Line2D of(double[] baseAndDirection) {
		if(baseAndDirection.length < 4) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Line2D(Vector2D.of(baseAndDirection), Vector2D.of(baseAndDirection, 2));
	}
	
	public static Line2D of(double[] baseAndDirection, int offset) {
		if(baseAndDirection.length < 4 + offset) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Line2D(Vector2D.of(baseAndDirection, offset), Vector2D.of(baseAndDirection, offset + 2));
	}

}

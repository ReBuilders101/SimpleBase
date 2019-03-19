package lb.simplebase.linalg;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

public class Vector2D {
	
	private final double x;
	private final double y;
	
	public static final Vector2D NULL = new Vector2D(0, 0);
	public static final Vector2D UNIT_X = new Vector2D(1, 0);
	public static final Vector2D UNIT_Y = new Vector2D(0, 1);
	
	private Vector2D(double x, double y) { //Maybe make this public
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return x;
	}
	
	public Vector2D add(Vector2D toAdd) {
		return new Vector2D(x + toAdd.x, y + toAdd.y);
	}
	
	public Vector2D subtract(Vector2D toSubtract) {
		return new Vector2D(x - toSubtract.x, y - toSubtract.y);
	}
	
	public Vector2D scale(double factor) {
		if(factor == 0) return NULL;
		return new Vector2D(x * factor, y * factor);
	}
	
	public Vector2D scaleInverse(double factor) {
		return new Vector2D(x / factor, y / factor);
	}
	
	public double dotProduct(Vector2D other) {
		return (x * other.x) + (y * other.y);
	}
	
	public double getLength() {
		return Math.sqrt(getLengthSq());
	}
	
	public double getLengthSq() {
		return (x * x) + (y * y);
	}
	
	public double getAngleCosine(Vector2D second) {
		return dotProduct(second) / (getLength() * second.getLength());
	}
	
	public double getAngleRadians(Vector2D second) {
		return Math.acos(getAngleCosine(second));
	}
	
	public double getAngleDegrees(Vector2D second) {
		return Math.toDegrees(getAngleRadians(second));
	}
	
	public Vector2D normalize() {
		if(isNullVector()) return this;
		return scaleInverse(getLength());
	}
	
	public Vector3D get3D(double z) {
		return Vector3D.of(x, y, z);
	}
	
	public Vector2D getWithIntegerPrecision() {
		return new Vector2D((int) x, (int) y); 
	}
	
	public Vector2D getOrthogonalVector() {
		return new Vector2D(y, -x);
	}
	
	public Point2D getAsPoint() {
		return new Point2D.Double(x, y);
	}
	
	public double[] getAsArray() {
		return new double[] {
				x, y
		};
	}
	
	public boolean isParallel(Vector2D other) {
		if(isNullVector() || other.isNullVector()) return false;
		if(x == 0) return other.y == 0;
		double a2b = other.x / x;
		return y * a2b == other.y;
	}
	
	public boolean isNullVector() {
		return x == 0 && y == 0;
	}
	
	//Static create
	
	public static Vector2D of(double x, double y) {
		return new Vector2D(x, y);
	}
	
	public static Vector2D of(Point2D point) {
		return new Vector2D(point.getX(), point.getY());
	}
	
	public static Vector2D of(Dimension2D dimension) {
		return new Vector2D(dimension.getWidth(), dimension.getHeight());
	}
	
	public static Vector2D of(double[] values) {
		if(values.length < 2) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Vector2D(values[0], values[1]);
	}
	
	public static Vector2D of(float[] values) {
		if(values.length < 2) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Vector2D(values[0], values[1]);
	}
	
	public static Vector2D of(int[] values) {
		if(values.length < 2) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Vector2D(values[0], values[1]);
	}
	
	//Static calculate
	
	public static Vector2D distance(Point2D first, Point2D second) {
		return new Vector2D(second.getX() - first.getX(), second.getY() - first.getY());
	}
	
	public static Vector2D distance(Vector2D first, Vector2D second) {
		return second.subtract(first);
	}
	
	public static Vector2D distance(double x1, double y1, double x2, double y2) {
		return new Vector2D(x2 - x1, y2 - y1);
	}
}

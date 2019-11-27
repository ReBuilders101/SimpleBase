package lb.simplebase.linalg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;


/**
 * This class represents an immutable vector in 2D space.
 */
public class Vector2D {
	
	private final double x;
	private final double y;
	
	//make transient?
	private final double length;
	private final double angle;
	
	/**
	 * A constant for the zero vector (which has an undefined direction). It has {@code x=0}, {@code y=0}, {@code length=0}, {@code angle=NaN}.
	 * @see Double#NaN
	 */
	public static final Vector2D ZERO = new Vector2D(0, 0);
	
	/**
	 * A constant for the unit vector in x direction. It has {@code x=1}, {@code y=0}, {@code length=1}, {@code angle=0}.
	 */
	public static final Vector2D UNIT_X = new Vector2D(1, 0);
	
	/**
	 * A constant for the unit vector in y direction. It has {@code x=0}, {@code y=1}, {@code length=1}, {@code angle=pi/2}.
	 */
	public static final Vector2D UNIT_Y = new Vector2D(0, 1);
	
	private Vector2D(double x, double y) { //Maybe make this public
		this.x = x;
		this.y = y;
		this.length = Math.sqrt((x * x) + (y * y));
		
		if(x == 0 && y == 0) {
			this.angle = Double.NaN;
		} else {
			this.angle = Math.atan2(y, x);
		}
	}
	
	/**
	 * The x-component of this vector in a 2D cartesian coordinate system.
	 * @return The x-component of the vector
	 * @see #getY()
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * The y-component of this vector in a 2D cartesian coordinate system.
	 * @return The y-component of the vector
	 * @see #getX()
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * The angle of this vector in a 2D polar coordinate system.
	 * The angle will be in a range from {@code -pi} to {@code pi} (and {@code NaN}). An angle of {@code 0} means that the vector is parallel to the x axis.
	 * A zero vector will have an angle of {@code NaN}.
	 * @return The polar angle of the vector
	 * @see #getLength()
	 * @see Math#PI
	 * @see Double#NaN
	 */
	public double getPolarAngle() {
		return angle;
	}
	
	/**
	 * Adds another vector to this vector and returns a new vector with the result of the addition.
	 * <br>Calculation: <i>result = this + toAdd</i>
	 * @param toAdd The vector that will be added to this vector
	 * @return The result of the vector addition
	 */
	public Vector2D add(Vector2D toAdd) {
		return new Vector2D(x + toAdd.x, y + toAdd.y);
	}
	
	/**
	 * Subtracts another vector <b>from</b> this one and returns a new vector with the result of the subtraction.
	 * <br>Calculation: <i>result = this - toSubtract</i>
	 * @param toAdd The vector that will be added to this vector
	 * @return The result of the vector addition
	 */
	public Vector2D subtract(Vector2D toSubtract) {
		return new Vector2D(x - toSubtract.x, y - toSubtract.y);
	}
	
	/**
	 * Scales the vector length by a factor. Negative values will scale and invert the vector. If the scale factor is not
	 * finite ({@link Double#POSITIVE_INFINITY}, {@link Double#NEGATIVE_INFINITY} or {@link Double#NaN}) or zero, a {@link #ZERO} vector will be returned.
	 * <br>Calculation: <i>result = this * factor</i>
	 * @param factor The factor by which the length of the vector should be multiplied
	 * @return The scaled vector
	 * @see Double#isFinite(double)
	 * @see #scaleDivide(double)
	 */
	public Vector2D scale(double factor) {
		if(factor == 0 || !Double.isFinite(factor)) return ZERO;
		return new Vector2D(x * factor, y * factor);
	}
	
	/**
	 * Scales the vector length <b>down</b> by a factor. Negative values will scale and invert the vector. If the scale factor is not
	 * finite ({@link Double#POSITIVE_INFINITY}, {@link Double#NEGATIVE_INFINITY} or {@link Double#NaN}) or zero, a {@link #ZERO} vector will be returned.
	 * <br>Calculation: <i>result = this * 1/factor</i>
	 * @param factor The factor by which the length of the vector should be multiplied
	 * @return The scaled vector
	 * @see Double#isFinite(double)
	 * @see #scale(double)
	 */
	public Vector2D scaleDivide(double factor) {
		if(factor == 0 || !Double.isFinite(factor)) return ZERO;
		return new Vector2D(x / factor, y / factor);
	}
	
	/**
	 * Returns the dot product of this vector and the other vector.
	 * <br>Calculation: <i>result = this.x * other.x + this.y * other.y</i>
	 * @param other The other vector
	 * @return The dot product of the vectors
	 */
	public double dotProduct(Vector2D other) {
		return (x * other.x) + (y * other.y);
	}
	
	/**
	 * The length of this vector. This value is also the radius of the vector's endpoint in a polar coordinate system.<br>
	 * The length is calculated and stored when the vector is created, and no square root calculations are made by this method.
	 * <br>Calculation: <i>result = sqrt(x*x + y*y) = |this|</i>
	 * @return The length of this vector
	 * @see #getPolarAngle()
	 */
	public double getLength() {
		return length;
	}
	
	/**
	 * Returns the angle between this vector and the other vector. The angle will be in radians in a range from {@code 0} to {@code pi}.
	 * If this vector or the other vector is has a length of zero, {@link Double#NaN} will be returned.
	 * <br>Calculation: <i>result = arccos((this * second) / (|this| * |second|))</i>
	 * @param second The second angle
	 * @return The angle between the vectors
	 * @see #getPolarAngle()
	 * @see Math#acos(double)
	 */
	public double getAngleRadians(Vector2D second) {
		if(this.isZeroVector() || second.isZeroVector()) return Double.NaN;
		return Math.acos(dotProduct(second) / (getLength() * second.getLength()));
	}
	
	/**
	 * Scales this vector to a length of 1. If this vector has a length of zero, this vector will be returned.
	 * <br>Calculation: <i>result = this * 1/|this|</i>
	 * @return The normalized vector
	 */
	public Vector2D normalize() {
		if(isZeroVector()) return this;
		return scaleDivide(getLength());
	}
	
	/**
	 * Creates a 3-dimensional vector with the same x and y coordinates and an extra z coordinate.
	 * @param z The value for the new z-coordinate
	 * @return A 3D vector
	 */
	public Vector3D create3D(double z) {
		return Vector3D.of(x, y, z);
	}
	
	/**
	 * Creates a new vector where the x- and y-coordinate are rounded to be integer values. The type of the x and y fields will still be {@code double}.
	 * @return The vector with rounded values for coordinates
	 */
	public Vector2D getWithIntegerPrecision() {
		return new Vector2D((int) x, (int) y); 
	}
	
	/**
	 * Transforms this vector using a linear transformation.
	 * <br>Calculation: <i>result = transformation * this</i>
	 * @param transformation The linear transformation to use
	 * @return The transformed vector
	 */
	public Vector2D transform(Matrix2D transformation) {
		return transformation.transform(this);
	}
	
	/**
	 * Calculates a vector that is orthogonal to this one and has the same length.
	 * There are two possible vectors that are orthogonal to this vector, the returned one will
	 * always be 90 degrees to the <b>left</b> of this vector, when viewed in this vectors direction.
	 * To get the other orthogonal vector, the result must be inverted.
	 * <br>Calculation: <i>result = (-this.y ; this.x)</i>
	 * @return A vector that is orthogonal to this one
	 */
	public Vector2D getOrthogonalVector() {
		return new Vector2D(-y, x);
	}
	
	/**
	 * Converts the vector to a {@link Point2D} object with the coordinates of the vectors endpoint when its base is at (0;0).
	 * The used implemetation will always be {@link Point2D.Double}.
	 * @return A point with the same coordinates as the vector
	 */
	public Point2D getAsPoint() {
		return new Point2D.Double(x, y);
	}
	
	/**
	 * Negates the vector. The returned vector will have the same length and point in the other direction.
	 * <br>Calculation: <i>result = this * -1</i>
	 * @return The negated vector
	 */
	public Vector2D negate() {
		return new Vector2D(-x, -y);
	}
	
	/**
	 * Converts this 2D vector to a 3D vector that represents this vector in a 2D projective coordinate system.
	 * The x and y components of the 3D vector are the same, and the z component will be 1
	 * @return A 3D projective coordinates vector representing this vector
	 */
	public Vector3D getProjectiveCoordinates() {
		return Vector3D.of(x, y, 1);
	}
	
	/**
	 * Calculates whether this vector and another one are parallel (or identical).
	 * If one or both vectors are <b>zero vectors</b>, the method will return <b>true</b>.
	 * @param other The other vector to test
	 * @return Whether the two vectors are parallel
	 */
	public boolean isParallel(Vector2D other) {
		return Matrix2D.columnDeterminant(this, other) == 0;
	}
	
	/**
	 * Checks whether this vector is a zero vector with {@code x=0} and {@code y=0}.
	 * @return Whether this is a zero vector
	 */
	public boolean isZeroVector() {
		return x == 0 && y == 0;
	}
	
	//Static create
	
	/**
	 * Creates a new Vector with the x- and y-coordinate.
	 * @param x The x-coordinate of the vector
	 * @param y The y-coordinate of the vector
	 * @return The new vector
	 */
	public static Vector2D of(double x, double y) {
		return new Vector2D(x, y);
	}
	
	/**
	 * Creates a new Vector with the same coordinates as the {@link Point2D}.
	 * @param point The point that contains the coordinates of the vector
	 * @return The new vector
	 */
	public static Vector2D of(Point2D point) {
		return new Vector2D(point.getX(), point.getY());
	}
	
	/**
	 * Creates a new Vector with the same coordinates as the {@link Dimension2D}'s x and y sizes.
	 * @param dimension The dimension that contains the coordinates of the vector
	 * @return The new vector
	 */
	public static Vector2D of(Dimension2D dimension) {
		return new Vector2D(dimension.getWidth(), dimension.getHeight());
	}
	
	/**
	 * Creates a new Vector with coordinates form the double array.
	 * The vectors values will be x=values[0] and y=values[1].
	 * If the array has less that 2 elements, an {@link ArrayIndexOutOfBoundsException} is thrown.
	 * @param values The array that contains the coordinates
	 * @return The new vector
	 * @throws ArrayIndexOutOfBoundsException when the array has a length less than 2
	 */
	public static Vector2D of(double[] values) {
		if(values.length < 2) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Vector2D(values[0], values[1]);
	}
	
	/**
	 * Creates a new Vector with coordinates form the float array. The vector's coordinates will be stored as {@code double}.
	 * The vectors values will be x=values[0] and y=values[1].
	 * If the array has less that 2 elements, an {@link ArrayIndexOutOfBoundsException} is thrown.
	 * @param values The array that contains the coordinates
	 * @return The new vector
	 * @throws ArrayIndexOutOfBoundsException when the array has a length less than 2
	 */
	public static Vector2D of(float[] values) {
		if(values.length < 2) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Vector2D(values[0], values[1]);
	}
	
	/**
	 * Creates a new Vector with coordinates form the integer array. The vector's coordinates will be stored as {@code double}.
	 * The vectors values will be x=values[0] and y=values[1].
	 * If the array has less that 2 elements, an {@link ArrayIndexOutOfBoundsException} is thrown.
	 * @param values The array that contains the coordinates
	 * @return The new vector
	 * @throws ArrayIndexOutOfBoundsException when the array has a length less than 2
	 */
	public static Vector2D of(int[] values) {
		if(values.length < 2) throw new ArrayIndexOutOfBoundsException("The array must have at least 2 elements");
		return new Vector2D(values[0], values[1]);
	}
	
	/**
	 * Creates a new Vector with coordinates form the double array.
	 * The vectors values will be x=values[offset] and y=values[offset+1].
	 * If the array has less that offset+2 elements, an {@link ArrayIndexOutOfBoundsException} is thrown.
	 * @param values The array that contains the coordinates
	 * @param offset The offset index where the vector values will start in the array
	 * @return The new vector
	 * @throws ArrayIndexOutOfBoundsException when the array has a length less than offset+2
	 */
	public static Vector2D of(double[] values, int offset) {
		if(values.length < 2 + offset) throw new ArrayIndexOutOfBoundsException("The array must have at least offset+2 elements");
		return new Vector2D(values[offset], values[offset + 1]);
	}
	
	/**
	 * Creates a new Vector with coordinates form the float array. The vector's coordinates will be stored as {@code double}.
	 * The vectors values will be x=values[offset] and y=values[offset+1].
	 * If the array has less that offset+2 elements, an {@link ArrayIndexOutOfBoundsException} is thrown.
	 * @param values The array that contains the coordinates
	 * @param offset The offset index where the vector values will start in the array
	 * @return The new vector
	 * @throws ArrayIndexOutOfBoundsException when the array has a length less than offset+2
	 */
	public static Vector2D of(float[] values, int offset) {
		if(values.length < 2 + offset) throw new ArrayIndexOutOfBoundsException("The array must have at least offset+2 elements");
		return new Vector2D(values[offset], values[offset + 1]);
	}
	
	/**
	 * Creates a new Vector with coordinates form the integer array. The vector's coordinates will be stored as {@code double}.
	 * The vectors values will be x=values[offset] and y=values[offset+1].
	 * If the array has less that offset+2 elements, an {@link ArrayIndexOutOfBoundsException} is thrown.
	 * @param values The array that contains the coordinates
	 * @param offset The offset index where the vector values will start in the array
	 * @return The new vector
	 * @throws ArrayIndexOutOfBoundsException when the array has a length less than offset+2
	 */
	public static Vector2D of(int[] values, int offset) {
		if(values.length < 2 + offset) throw new ArrayIndexOutOfBoundsException("The array must have at least offset+2 elements");
		return new Vector2D(values[offset], values[offset + 1]);
	}
	
	/**
	 * Creates a Vector from polar coordinates. 
	 * @param radius The radius of the vector's endpoint, and the length of the vector
	 * @param theta The angle between x-axis and the vector
	 * @return The new vector
	 */
	public static Vector2D ofPolar(double radius, double theta) {
		return new Vector2D(Math.cos(theta) * radius, Math.sin(theta) * radius);
	}
	
	/**
	 * Creates a Vector from projective coordinates. 
	 * @param coords the 3D vector representing a 2D projective vector
	 * @return The new vector
	 */
	public static Vector2D ofProjectiveCoordinates(Vector3D coords) {
		if(coords.getZ() == 0) throw new IllegalArgumentException("Projective Vector must have z != 0");
		return new Vector2D(coords.getX() / coords.getZ(), coords.getY() / coords.getZ());
	}
	
	/**
	 * Creates a Vector from the offset (translation) part of a 2D affine transformation.
	 * @param transform The {@link AffineTransform} that contains the translation
	 * @return The new vector
	 */
	public static Vector2D ofTranslation(AffineTransform transform) {
		return new Vector2D(transform.getTranslateX(), transform.getTranslateY());
	}
	
	/**
	 * Checks whether the 3D vector is a projective 2D vector that does not represent a point in infinity (z=0).
	 * @param vector The vector to test
	 * @return Whether {@code vector.x != 0}
	 */
	public static boolean isFiniteProjectiveVector(Vector3D vector) {
		return vector.getZ() != 0;
	}
	
	//Static calculate
	
	/**
	 * Calculates a vector that goes from the first to the second {@link Point2D}, so that <i>first + newVector = second</i>.
	 * @param first The first point
	 * @param second The second point
	 * @return The new vector
	 */
	public static Vector2D distance(Point2D first, Point2D second) {
		return new Vector2D(second.getX() - first.getX(), second.getY() - first.getY());
	}
	
	/**
	 * Calculates a vector that goes from the first to the second vector, so that <i>first + newVector = second</i>.
	 * Equivalent to calculating <i>newVector = second - first</i>.
	 * @param first The first point
	 * @param second The second point
	 * @return The new vector
	 */
	public static Vector2D distance(Vector2D first, Vector2D second) {
		return second.subtract(first);
	}
	
	/**
	 * Calculates a vector that goes from the first to the second point represented by a set of two doubles each,
	 * so that <i>first + vector = second</i>.
	 * @param x1 The x-coordinate of the first vector
	 * @param y1 The y-coordinate of the first vector
	 * @param x2 The x-coordinate of the second vector
	 * @param y2 The y-coordinate of the second vector
	 * @return The new vector
	 */
	public static Vector2D distance(double x1, double y1, double x2, double y2) {
		return new Vector2D(x2 - x1, y2 - y1);
	}

	/**
	 * Calculates a unique and immutable value for every {@link Vector2D} instance, as specified in {@link Object#hashCode()}.
	 * @return The hash code value for this vector
	 * @see #equals(Object)
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(angle);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(length);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Checks whether this vector and another one are equal by values of the fields, as specified in {@link Object#equals(Object)}.
	 * @return Whether this object is equal to the other one
	 * @see #hashCode()
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector2D other = (Vector2D) obj;
		if (Double.doubleToLongBits(angle) != Double.doubleToLongBits(other.angle))
			return false;
		if (Double.doubleToLongBits(length) != Double.doubleToLongBits(other.length))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	/**
	 * Returns a {@link String} representation of this vector, containing information
	 * about x-coordinate, y-coordinate, length and angle.
	 * @return A string representation of this vector
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Vector2D [x=" + x + ", y=" + y + ", length=" + length + ", angle=" + angle + "]";
	}


}

package lb.simplebase.linalg;

public class Vector3D {

	private final double x;
	private final double y;
	private final double z;
	
	public static final Vector3D NULL = new Vector3D(0, 0, 0);
	public static final Vector3D UNIT_X = new Vector3D(1, 0, 0);
	public static final Vector3D UNIT_Y = new Vector3D(0, 1, 0);
	public static final Vector3D UNIT_Z = new Vector3D(0, 0, 1);
	
	private Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public Vector3D add(Vector3D toAdd) {
		return new Vector3D(x + toAdd.x, y + toAdd.y, z + toAdd.z);
	}
	
	public Vector3D subtract(Vector3D toSubtract) {
		return new Vector3D(x - toSubtract.x, y - toSubtract.y, z - toSubtract.z);
	}
	
	public Vector3D scale(double factor) {
		if(factor == 0) return NULL;
		return new Vector3D(x * factor, y * factor, z * factor);
	}
	
	public Vector3D scaleInverse(double factor) {
		return new Vector3D(x / factor, y / factor, z / factor);
	}
	
	public double dotProduct(Vector3D other) {
		return (x * other.x) + (y * other.y) + (z * other.z);
	}

	public double getLength() {
		return Math.sqrt(getLengthSq());
	}
	
	public double getLengthSq() {
		return (x * x) + (y * y) + (z * z);
	}
	
	public double getAngleCosine(Vector3D second) {
		return dotProduct(second) / (getLength() * second.getLength());
	}
	
	public double getAngleRadians(Vector3D second) {
		return Math.acos(getAngleCosine(second));
	}
	
	public double getAngleDegrees(Vector3D second) {
		return Math.toDegrees(getAngleRadians(second));
	}
	
	public Vector3D normalize() {
		if(isNullVector()) return this;
		return scaleInverse(getLength());
	}
	
	public Vector2D getProjectedCoordinates() {
		return Vector2D.ofProjectiveCoordinates(this);
	}
	
	public Vector3D transform(Matrix3D transformation) {
		return transformation.transform(this);
	}
	
	public Vector3D getWithIntegerPrecision() {
		return new Vector3D((int) x, (int) y, (int) z);
	}
	
	public Vector3D crossProduct(Vector3D other) {
		return new Vector3D((y * other.z) - (z * other.y), (z * other.x) - (x * other.z), (x * other.y) - (y * other.x));
	}
	
	public boolean isParallel(Vector3D other) {
		if(isNullVector() || other.isNullVector()) return false;
		double a2b;
		if(x == 0) {
			if(other.x == 0) {
				if(y == 0) return other.y == 0;
				a2b = other.y / y;
			} else {
				return false;
			}
		} else {
			a2b = other.x / x;
		}
		return y * a2b == other.y && z * a2b == other.z;
	}
	
	public boolean isNullVector() {
		return x == 0 && y == 0 && z == 0;
	}
	
	public double[] getAsArray() {
		return new double[] {
				x, y, z
		};
	}
	
	public Vector3D negate() {
		return new Vector3D(-x, -y, -z);
	}
	
	
	public static Vector3D of(double x, double y, double z) {
		return new Vector3D(x, y, z);
	}
	
	public static Vector3D of(double[] values) {
		if(values.length < 3) throw new ArrayIndexOutOfBoundsException("The array must have at least 3 elements");
		return new Vector3D(values[0], values[1], values[2]);
	}
	
	public static Vector3D of(float[] values) {
		if(values.length < 3) throw new ArrayIndexOutOfBoundsException("The array must have at least 3 elements");
		return new Vector3D(values[0], values[1], values[2]);
	}
	
	public static Vector3D of(int[] values) {
		if(values.length < 3) throw new ArrayIndexOutOfBoundsException("The array must have at least 3 elements");
		return new Vector3D(values[0], values[1], values[2]);
	}
	
	public static Vector3D distance(Vector3D first, Vector3D second) {
		return second.subtract(first);
	}
	
	public static Vector3D distance(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new Vector3D(x2 - x1, y2 - y1, z2 - z1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
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
		Vector3D other = (Vector3D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vector3D [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}

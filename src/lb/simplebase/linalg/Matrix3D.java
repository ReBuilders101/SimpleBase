package lb.simplebase.linalg;

public class Matrix3D {

	public static final Matrix3D NULL = new Matrix3D(0, 0, 0, 0, 0, 0, 0, 0, 0);
	public static final Matrix3D IDENTITY = new Matrix3D(1, 0, 0, 0, 1, 0, 0, 0, 1);
	
	//First row
	private final double m00;
	private final double m01;
	private final double m02;
	//Second row
	private final double m10;
	private final double m11;
	private final double m12;
	//Third row
	private final double m20;
	private final double m21;
	private final double m22;
	public Matrix3D(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
	}
	public double getM00() {
		return m00;
	}
	public double getM01() {
		return m01;
	}
	public double getM02() {
		return m02;
	}
	public double getM10() {
		return m10;
	}
	public double getM11() {
		return m11;
	}
	public double getM12() {
		return m12;
	}
	public double getM20() {
		return m20;
	}
	public double getM21() {
		return m21;
	}
	public double getM22() {
		return m22;
	}
	
	public double getElement(int row, int col) {
		if(row > 2 || row < 0 || col > 2 || col < 0) throw new ArrayIndexOutOfBoundsException("row and col must be in range 0 to 2");
		if(row == 0) {
			if(col == 0) {
				return m00;
			} else if(col == 1) {
				return m01;
			} else { //2
				return m02;
			}
		} else if(row == 1) {
			if(col == 0) {
				return m10;
			} else if(col == 1) {
				return m11;
			} else { //2
				return m12;
			}
		} else { //2
			if(col == 0) {
				return m20;
			} else if(col == 1) {
				return m21;
			} else { //2
				return m22;
			}
		}
	}
	
	public double[] getAsArray() {
		return new double[] {
				m00, m01, m02, m10, m11, m12, m20, m21, m22
		};
	}
	
	public Matrix3D add(Matrix3D toAdd) {
		return new Matrix3D(m00 + toAdd.m00, m01 + toAdd.m01, m02 + toAdd.m02,
							m10 + toAdd.m10, m11 + toAdd.m11, m12 + toAdd.m12,
							m20 + toAdd.m20, m21 + toAdd.m21, m22 + toAdd.m22);
	}
	
	public Matrix3D subtract(Matrix3D toSubtract) {
		return new Matrix3D(m00 - toSubtract.m00, m01 - toSubtract.m01, m02 - toSubtract.m02,
							m10 - toSubtract.m10, m11 - toSubtract.m11, m12 - toSubtract.m12,
							m20 - toSubtract.m20, m21 - toSubtract.m21, m22 - toSubtract.m22);
	}
	
	public Matrix3D inverse() {
		final double det = determinant();
		if(det == 0) return NULL;
		return adjugateMatrix().scale(1D / det);
	}
	
	public Matrix3D minorMatrix() {
		final double[] v = new double[9];
		int currentIndex = 0;
		
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				v[currentIndex++] = subMatrix(row, col).determinant();
			}
		}
		return Matrix3D.of(v);
	}
	
	public Matrix3D cofactorMatrix() {
		return minorMatrix().pattern();
	}
	
	public Matrix3D adjugateMatrix() {
		return cofactorMatrix().transpose();
	}
	
	private Matrix3D pattern() {
		return new Matrix3D(m00, -m01, m02, -m10, m11, -m12, m20, -m21, m22);
	}
	
	public Matrix3D negate() {
		return new Matrix3D(-m00, -m01, -m02, -m10, -m11, -m12, -m20, -m21, -m22);
	}
	
	public Vector3D transform(Vector3D vec) {
		return Vector3D.of(vec.getX() * m00 + vec.getY() * m01 + vec.getZ() * m02,
						   vec.getX() * m10 + vec.getZ() * m11 + vec.getZ() * m12,
						   vec.getX() * m20 + vec.getY() * m21 + vec.getZ() * m22);
	}
	
	public Matrix3D multiply(Matrix3D left) {
		return new Matrix3D(
				dotP3D(left.m00, left.m01, left.m02, m00, m10, m20), dotP3D(left.m00, left.m01, left.m02, m01, m11, m21), dotP3D(left.m00, left.m01, left.m02, m02, m12, m22),
				dotP3D(left.m10, left.m11, left.m12, m00, m10, m20), dotP3D(left.m10, left.m11, left.m12, m01, m11, m21), dotP3D(left.m10, left.m11, left.m12, m02, m12, m22),
				dotP3D(left.m20, left.m21, left.m22, m00, m10, m20), dotP3D(left.m20, left.m21, left.m22, m01, m11, m21), dotP3D(left.m20, left.m21, left.m22, m02, m12, m22));
	}
	
	private static double dotP3D(double x1, double y1, double z1, double x2, double y2, double z2) {
		return (x1 * x2) + (y1 * y2) + (z1 * z2);
	}
	
	public Matrix3D scale(double factor) {
		return new Matrix3D(m00 * factor, m01 * factor, m02 * factor,
							m10 * factor, m11 * factor, m12 * factor,
							m20 * factor, m21 * factor, m22 * factor);
	}
	
	public Matrix3D transpose() {
		return new Matrix3D(m00, m10, m20, m01, m11, m21, m02, m12, m22);
	}
	
	public double determinant() {
		return 	  (m00 * (m11 * m22 - m12 * m21))
				- (m01 * (m10 * m22 - m12 * m20))
				+ (m02 * (m10 * m21 - m11 * m20));
	}
	
	public boolean isNull() {
		return m00 == 0 && m01 == 0 && m02 == 0 && m10 == 0 && m11 == 0 && m12 == 0 && m20 == 0 && m21 == 0 && m22 == 0;
	}
	
	public boolean isIdentity() {
		return m00 == 1 && m01 == 0 && m02 == 0 && m10 == 0 && m11 == 1 && m12 == 0 && m20 == 0 && m21 == 0 && m22 == 1;
	}
	
	public boolean isInverse(Matrix3D inverse) {
		return multiply(inverse).isIdentity();
	}
	
	public Matrix2D subMatrix(int rowExclude, int colExclude) {
		final double[] v = new double[4];
		int currentIndex = 0;
		for(int row = 0; row < 3; row++) {
			for(int col = 0; col < 3; col++) {
				if(row != rowExclude && col != colExclude) v[currentIndex++] = getElement(row, col);
			}
		}
		return Matrix2D.of(v);
	}
	
	public Vector3D getFirstColumn() {
		return Vector3D.of(m00, m10, m20);
	}
	
	public Vector3D getSecondColumn() {
		return Vector3D.of(m01, m11, m21);
	}

	public Vector3D getThirdColumn() {
		return Vector3D.of(m02, m12, m22);
	}

	public Vector3D getFirstRow() {
		return Vector3D.of(m00, m01, m02);
	}

	public Vector3D getSecondRow() {
		return Vector3D.of(m10, m11, m12);
	}

	public Vector3D getThirdRow() {
		return Vector3D.of(m20, m21, m22);
	}
	
	public static Matrix3D of(double topLeft, double topCenter, double topRight,
							  double centerLeft, double center, double centerRight,
							  double bottomLeft, double bottomCenter, double bottomRight) {
		return new Matrix3D(topLeft, topCenter, topRight, centerLeft, center, centerRight, bottomLeft, bottomCenter, bottomRight);
	}
	
	public static Matrix3D ofRows(Vector3D row1, Vector3D row2, Vector3D row3) {
		return new Matrix3D(row1.getX(), row1.getY(), row1.getZ(), row2.getX(), row2.getY(), row2.getZ(), row3.getX(), row3.getY(), row3.getZ());
	}
	
	public static Matrix3D ofCols(Vector3D col1, Vector3D col2, Vector3D col3) {
		return new Matrix3D(col1.getX(), col2.getX(), col3.getX(), col1.getY(), col2.getY(), col3.getY(), col1.getZ(), col2.getZ(), col3.getZ());
	}
	
	public static Matrix3D of(double[] values) {
		if(values.length < 9) throw new ArrayIndexOutOfBoundsException("The array must have at least 9 elements");
		return new Matrix3D(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8]);
	}
	
	public static Matrix3D of(float[] values) {
		if(values.length < 9) throw new ArrayIndexOutOfBoundsException("The array must have at least 9 elements");
		return new Matrix3D(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8]);
	}
	
	public static Matrix3D of(int[] values) {
		if(values.length < 9) throw new ArrayIndexOutOfBoundsException("The array must have at least 9 elements");
		return new Matrix3D(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8]);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(m00);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m01);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m02);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m10);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m11);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m12);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m20);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m21);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(m22);
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
		Matrix3D other = (Matrix3D) obj;
		if (Double.doubleToLongBits(m00) != Double.doubleToLongBits(other.m00))
			return false;
		if (Double.doubleToLongBits(m01) != Double.doubleToLongBits(other.m01))
			return false;
		if (Double.doubleToLongBits(m02) != Double.doubleToLongBits(other.m02))
			return false;
		if (Double.doubleToLongBits(m10) != Double.doubleToLongBits(other.m10))
			return false;
		if (Double.doubleToLongBits(m11) != Double.doubleToLongBits(other.m11))
			return false;
		if (Double.doubleToLongBits(m12) != Double.doubleToLongBits(other.m12))
			return false;
		if (Double.doubleToLongBits(m20) != Double.doubleToLongBits(other.m20))
			return false;
		if (Double.doubleToLongBits(m21) != Double.doubleToLongBits(other.m21))
			return false;
		if (Double.doubleToLongBits(m22) != Double.doubleToLongBits(other.m22))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "Matrix3D [m00=" + m00 + ", m01=" + m01 + ", m02=" + m02 + ", m10=" + m10 + ", m11=" + m11 + ", m12="
				+ m12 + ", m20=" + m20 + ", m21=" + m21 + ", m22=" + m22 + "]";
	}
}

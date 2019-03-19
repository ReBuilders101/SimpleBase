package lb.simplebase.linalg;

public class Matrix2D {

	public static final Matrix2D IDENTITY = new Matrix2D(1, 0, 0, 1);
	public static final Matrix2D NULL = new Matrix2D(0, 0, 0, 0);
	
	private final double m00;
	private final double m01;
	private final double m10;
	private final double m11;
	
	private Matrix2D(double topLeft, double topRight, double bottomLeft, double bottomRight) {
		m00 = topLeft;
		m01 = topRight;
		m10 = bottomLeft;
		m11 = bottomRight;
	}
	
	public double getElement00() {
		return m00;
	}
	
	public double getElement01() {
		return m01;
	}
	
	public double getElement10() {
		return m10;
	}
	
	public double getElement11() {
		return m11;
	}
	
	public double getElementTopLeft() {
		return m00;
	}
	
	public double getElementTopRight() {
		return m01;
	}
	
	public double getElementBottomLeft() {
		return m10;
	}
	
	public double getElementBottomRight() {
		return m11;
	}
	
	public double getElement(int row, int col) {
		if(row == 0 && col == 0) return m00;
		if(row == 0 && col == 1) return m01;
		if(row == 1 && col == 0) return m10;
		if(row == 1 && col == 1) return m11;
		return 0;
	}
	
	//Operations
	
	public Matrix2D add(Matrix2D toAdd) {
		return new Matrix2D(m00 + toAdd.m00, m01 + toAdd.m01, m10 + toAdd.m10, m11 + toAdd.m11);
	}
	
	public Matrix2D subtract(Matrix2D toSubtract) {
		return new Matrix2D(m00 + toSubtract.m00, m01 + toSubtract.m01, m10 + toSubtract.m10, m11 + toSubtract.m11);
	}
	
	public Matrix2D inverse() {
		final double det = determinant();
		if(det == 0) return NULL;
		return new Matrix2D(m11, -m01, -m10, m00).scale(1D / det);
	}
	
	public Matrix2D negate() {
		return new Matrix2D(-m00, -m01, -m10, -m11);
	}
	
	/**
	 * Param is left so it can be applied to this transform
	 */
	public Matrix2D multiply(Matrix2D left) {
		return new Matrix2D(dotP2D(left.m00, left.m01, this.m00, this.m10),
							dotP2D(left.m00, left.m01, this.m01, this.m11),
							dotP2D(left.m10, left.m11, this.m00, this.m10),
							dotP2D(left.m10, left.m11, this.m01, this.m11));
	}
	
	//Helper for fast dot product without creating a Vector2D object
	private static double dotP2D(double x1, double y1, double x2, double y2) {
		return (x1 * x2) + (y1 * y2);
	}
	
	public Matrix2D scale(double factor) {
		if(factor == 0) return NULL;
		return new Matrix2D(m00 * factor, m01 * factor, m10 * factor, m11 * factor);
	}
	
	public Matrix2D transpose() {
		return new Matrix2D(m00, m10, m01, m11);
	}
	
	public double determinant() {
		return (m00 * m11) - (m01 * m10);
	}
	
	public boolean isNull() {
		return m00 == 0 && m01 == 0 && m10 == 0 && m11 == 0;
	}
	
	public boolean isIdentity() {
		return m00 == 1 && m01 == 0 && m10 == 0 && m11 == 1;
	}
	
	public boolean isInverse(Matrix2D inverse) {
		return multiply(inverse).isIdentity();
	}
	
	public Vector2D getFirstColumn() {
		return Vector2D.of(m00, m10);
	}
	
	public Vector2D getSecondColumn() {
		return Vector2D.of(m01, m11);
	}
	
	public Vector2D getFirstRow() {
		return Vector2D.of(m00, m01);
	}
	
	public Vector2D getSecondRow() {
		return Vector2D.of(m10, m11);
	}
	
	
	public static Matrix2D of(double topLeft, double topRight, double bottomLeft, double bottomRight) {
		return new Matrix2D(topLeft, topRight, bottomLeft, bottomRight);
	}
	
	public static Matrix2D ofRows(Vector2D row1, Vector2D row2) {
		return new Matrix2D(row1.getX(), row1.getY(), row2.getX(), row2.getY());
	}
	
	public static Matrix2D ofCols(Vector2D col1, Vector2D col2) {
		return new Matrix2D(col1.getX(), col2.getX(), col1.getY(), col2.getY());
	}
	
}

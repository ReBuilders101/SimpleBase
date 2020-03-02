package lb.simplebase.glcore;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

public class MatrixUtils {
	
	public static final float PI = (float) Math.PI;
	public static final float PI2 = (float) (Math.PI * 2d);
	
	public static float sin(double angleRad) {
		return (float) Math.sin(angleRad);
	}
	
	public static float cos(double angleRad) {
		return (float) Math.cos(angleRad);
	}
	
	public static float tan(double angleRad) {
		return (float) Math.tan(angleRad);
	}
	
	public static Matrix4f scale(float x, float y, float z) {
		Matrix4f r = new Matrix4f();
		r.m00 = x;
		r.m11 = y;
		r.m22 = z;
		r.m33 = 1;
		return r;
	}
	
	public static Matrix4f scale(float scale) {
		return scale(scale, scale, scale);
	}
	
	public static Matrix4f scale(Vector3f xyz) {
		return scale(xyz.x, xyz.y, xyz.z);
	}
	
	public static Matrix4f translate(float x, float y, float z) {
		Matrix4f r = new Matrix4f();
		r.setIdentity();
		r.m03 = x;
		r.m13 = y;
		r.m23 = z;
		return r;
	}
	
	public static Matrix4f translate(Vector3f offset) {
		return translate(offset.x, offset.y, offset.z);
	}
	
	public static Matrix4f rotateAround(Vector3f axis, double theta) {
		final float x = axis.x;
		final float y = axis.y;
		final float z = axis.z;
		final float cos = cos(theta);
		final float sin = sin(theta);
		final float cn1 = 1 - cos;
		
		final Matrix4f r = new Matrix4f();
		
		r.m00 = cos + x*x*cn1;
		r.m01 = x*y*cn1 - z*sin;
		r.m02 = x*z*cn1 + y*sin;
		
		r.m10 = x*y*cn1 + z*sin;
		r.m11 = cos + y*y*cn1;
		r.m12 = y*z*cn1 - x*sin;
		
		r.m20 = x*z*cn1 - y*sin;
		r.m21 = y*z*cn1 + x*sin;
		r.m22 = cos + z*z*cn1;
		
		r.m33 = 1;
		
		return r;
	}
	
	public static Matrix4f rotateEuler(RotationOrder order, double rotXrad, double rotYrad, double rotZrad) {
		final Matrix4f rx = new Matrix4f();
		if(rotXrad == 0) {
			rx.setIdentity();
		} else {
			rx.m00 = 1;
			rx.m11 = cos(rotXrad);
			rx.m12 = -sin(rotXrad);
			rx.m21 = sin(rotXrad);
			rx.m22 = cos(rotXrad);
			rx.m33 = 1;
		}
		
		final Matrix4f ry = new Matrix4f();
		if(rotYrad == 0) {
			ry.setIdentity();
		} else {
			ry.m00 = cos(rotYrad);
			ry.m02 = sin(rotYrad);
			ry.m11 = 1;
			ry.m20 = -sin(rotYrad);
			ry.m22 = cos(rotYrad);
			ry.m33 = 1;
		}
		
		final Matrix4f rz = new Matrix4f();
		if(rotZrad == 0) {
			rz.setIdentity();
		} else {
			rz.m00 = cos(rotZrad);
			rz.m01 = -sin(rotZrad);
			rz.m10 = sin(rotZrad);
			rz.m11 = cos(rotZrad);
			rz.m22 = 1;
			rz.m33 = 1;
		}
		
		return order.produce(rx, ry, rz);
	}
	
	public static Matrix4f perspective(float fovYradians, float aspectRatio, float nearPlane, float farPlane) {
		final float yScale = 1f / MatrixUtils.tan(fovYradians / 2f);
		final float xScale = yScale / aspectRatio;
		final float frustrumLength = farPlane - nearPlane;
		final Matrix4f mat4f = new Matrix4f();
		mat4f.m00 = xScale; // 1/tan
		mat4f.m11 = yScale;
		mat4f.m22 = -((farPlane + nearPlane) / frustrumLength);
		mat4f.m23 = -1;
		mat4f.m32 = -((2f * nearPlane * farPlane) / frustrumLength);
		mat4f.m33 = 0f;
		return mat4f;
	}
	
	public static Matrix4f perspective2(float fovYradians, float aspectRatio, float nearPlane, float farPlane, boolean flag) {
		final Matrix4f mat4f = new Matrix4f();
		final float h = tan(fovYradians * 0.5f);
		mat4f.m00 = 1.0f / (h * aspectRatio);
		mat4f.m11 = 1.0f / h;
		
		mat4f.m22 = (flag ? farPlane : farPlane + nearPlane) / (nearPlane - farPlane);
		mat4f.m32 = (flag ? farPlane : farPlane + nearPlane) * nearPlane / (nearPlane - farPlane);
		
		mat4f.m23 = -1.0f;
		return mat4f;
	}

	public static void print(Matrix4f value) {
		System.out.println(value.m00 + " " + value.m01 + " " + value.m02 + " " + value.m03);
		System.out.println(value.m10 + " " + value.m11 + " " + value.m12 + " " + value.m13);
		System.out.println(value.m20 + " " + value.m21 + " " + value.m22 + " " + value.m23);
		System.out.println(value.m30 + " " + value.m31 + " " + value.m32 + " " + value.m33);
		System.out.println();
	}
	
	public static enum RotationOrder {
		XYZ(0, 1, 2), XZY(0, 2, 1),
		YXZ(1, 0, 2), YZX(1, 2, 0),
		ZXY(2, 0, 1), ZYX(2, 1, 0);
		
		private final int[] order;
		
		private RotationOrder(int...order) {
			assert order.length == 3;
			this.order = order;
		}
		
		protected Matrix4f produce(Matrix4f...xyz) {
			final Matrix4f mat1 = xyz[order[0]];
			final Matrix4f mat2 = xyz[order[1]];
			final Matrix4f mat3 = xyz[order[2]];
			
			final Matrix4f result = new Matrix4f();
			result.setIdentity();
			result.mul(mat3);
			result.mul(mat2);
			result.mul(mat1);
			return result;
//			result.mul(mat2, mat1);
//			result.mul(mat3, result);
//			return result; //TODO check order
		}
	}
	
}

package lb.simplebase.glcore.scene;

import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;

import lb.simplebase.glcore.GLFramework;
import lb.simplebase.glcore.MatrixUtils;
import lb.simplebase.glcore.MatrixUtils.RotationOrder;

public class Camera {

	private float px;
	private float py;
	private float pz;
	
	private float roll;
	private float pitch;
	private float yaw;
	
	private float fovRadians;
	
	private final Matrix4f cameraSpace;
	private final Matrix4f perspective;
	
	
	public Camera(float x, float y, float z, float pitch, float yaw, float roll, float fov) {
		this.cameraSpace = new Matrix4f();
		this.perspective = new Matrix4f();
		this.px = x;
		this.py = y;
		this.pz = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		this.fovRadians = fov;
		updateTransforms();
	}
	
	private void updateTransforms() {
		final Matrix4f rotate = MatrixUtils.rotateEuler(RotationOrder.YXZ, roll, yaw, pitch);
		final Matrix4f translate = MatrixUtils.translate(px, py, pz);
		cameraSpace.mul(translate, rotate);
		perspective.set(MatrixUtils.perspective2(fovRadians, GLFramework.gfAspectRatio(), 0.1F, 100F, false));
	}
	
	public float getPosX() {
		return px;
	}

	public float getPosY() {
		return py;
	}
	
	public float getPosZ() {
		return pz;
	}
	
	public void setPosX(float x) {
		this.px = x;
		updateTransforms();
	}

	public void setPosY(float y) {
		this.py = y;
		updateTransforms();
	}
	
	public void setPosZ(float z) {
		this.pz = z;
		updateTransforms();
	}
	
	public float[] getPos() {
		return new float[] {px, py, pz};
	}
	
	public void getPos(float[] fill) {
		if(fill.length < 3) throw new ArrayIndexOutOfBoundsException("Array to fill must have a length of 3 or more");
		fill[0] = px;
		fill[1] = py;
		fill[2] = pz;
	}	
	
	public void getPos(float[] fill, int offset) {
		if(fill.length < offset + 3) throw new ArrayIndexOutOfBoundsException("Array to fill must have a length of 3 or more ofter the offset index");
		fill[0 + offset] = px;
		fill[1 + offset] = py;
		fill[2 + offset] = pz;
	}
	
	public void getPos(FloatBuffer fill) {
		fill.put(px).put(py).put(pz);
	}	
	
	public void setPos(float[] pos) {
		if(pos.length < 3) throw new ArrayIndexOutOfBoundsException("Array to read must have a length of 3 or more");
		px = pos[0];
		py = pos[1];
		pz = pos[2];
		updateTransforms();
	}
	
	public void setPos(float x, float y, float z) {
		px = x;
		py = y;
		pz = z;
		updateTransforms();
	}
	
	public void setPos(float[] pos, int offset) {
		if(pos.length < 3) throw new ArrayIndexOutOfBoundsException("Array to read must have a length of 3 or more after the offset index");
		px = pos[0 + offset];
		py = pos[1 + offset];
		pz = pos[2 + offset];
		updateTransforms();
	}
	

	public float getRoll() {
		return roll;
	}

	public float getPitch() {
		return pitch;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public void setRoll(float roll) {
		this.roll = roll;
		updateTransforms();
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
		updateTransforms();
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
		updateTransforms();
	}
	
	
//	public float getTargetX() {
//		return tx;
//	}
//
//	public float getTargetY() {
//		return ty;
//	}
//	
//	public float getTargetZ() {
//		return tz;
//	}
//	
//	public void setTargetX(float x) {
//		this.tx = x;
//		updateTransforms();
//	}
//
//	public void setTargetY(float y) {
//		this.ty = y;
//		updateTransforms();
//	}
//	
//	public void setTargetZ(float z) {
//		this.tz = z;
//		updateTransforms();
//	}
//	
//	public float[] getTarget() {
//		return new float[] {tx, ty, tz};
//	}
//	
//	public void getTarget(float[] fill) {
//		if(fill.length < 3) throw new ArrayIndexOutOfBoundsException("Array to fill must have a length of 3 or more");
//		fill[0] = tx;
//		fill[1] = ty;
//		fill[2] = tz;
//	}	
//	
//	public void getTarget(float[] fill, int offset) {
//		if(fill.length < offset + 3) throw new ArrayIndexOutOfBoundsException("Array to fill must have a length of 3 or more after the offset index");
//		fill[0 + offset] = tx;
//		fill[1 + offset] = ty;
//		fill[2 + offset] = tz;
//	}
//	
//	public void getTarget(FloatBuffer fill) {
//		fill.put(tx).put(ty).put(tz);
//	}
//	
//	public void setTarget(float[] pos) {
//		if(pos.length < 3) throw new ArrayIndexOutOfBoundsException("Array to read must have a length of 3 or more");
//		tx = pos[0];
//		ty = pos[1];
//		tz = pos[2];
//		updateTransforms();
//	}
//	
//	public void setTarget(float[] pos, int offset) {
//		if(pos.length < 3) throw new ArrayIndexOutOfBoundsException("Array to read must have a length of 3 or more after the offset index");
//		tx = pos[0 + offset];
//		ty = pos[1 + offset];
//		tz = pos[2 + offset];
//		updateTransforms();
//	}
//	
//	public void setTarget(float x, float y, float z) {
//		tx = x;
//		ty = y;
//		tz = z;
//		updateTransforms();
//	}
	
	public float getFovRadians() {
		return fovRadians;
	}
	
	public void setFovRadians(float fov) {
		fovRadians = fov;
		updateTransforms();
	}
	
	public void setFovDegrees(double fov) {
		setFovRadians((float) Math.toRadians(fov));
	}
	
	public Matrix4f worldSpaceToCameraSpace() {
		return cameraSpace;
	}
	
	public Matrix4f cameraSpaceToPerspective() {
		return perspective;
	}
	
	public Matrix4f worldSpaceToPerspective() {
		final Matrix4f m4f = new Matrix4f();
		m4f.mul(cameraSpaceToPerspective(), worldSpaceToCameraSpace());
		return m4f;
	}	
}

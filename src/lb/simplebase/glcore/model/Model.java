package lb.simplebase.glcore.model;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.lwjgl.opengl.GL11;
import lb.simplebase.glcore.MatrixUtils;
import lb.simplebase.glcore.MatrixUtils.RotationOrder;
import lb.simplebase.glcore.oop.BufferObject;
import lb.simplebase.glcore.oop.GLDisposable;
import lb.simplebase.glcore.oop.VertexArray;
import lb.simplebase.glcore.oop.BufferObject.BufferLocation;
import lb.simplebase.glcore.oop.BufferObject.BufferUsage;
import lb.simplebase.glcore.scene.GLModel;

public class Model {
	private final ModelPrefab prefab;
	
	private float px;
	private float py;
	private float pz;
	
	private float roll;
	private float pitch;
	private float yaw;

	private final Matrix4f objectToWorldSpace;
	
	protected Model(ModelPrefab pre, float x, float y, float z, float pitch, float yaw, float roll) {
		this.prefab = pre;
		this.objectToWorldSpace = new Matrix4f();
		this.px = x;
		this.py = y;
		this.pz = z;
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
		updateTransforms();
	}
	
	private void updateTransforms() {
		//Everything is inverted, because we transform back to world space
		final Matrix4f rotate = MatrixUtils.rotateEuler(RotationOrder.YXZ, -roll, -yaw, -pitch);
		final Matrix4f translate = MatrixUtils.translate(-px, -py, -pz);
		objectToWorldSpace.mul(translate, rotate);
	}
	
	public ModelPrefab getPrefab() {
		return prefab;
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
	
	private GLModelVAO glModel; 
	
	public void initGlModel(boolean textures, boolean normals) {
		if(glModel != null) disposeGlModel();
		glModel = new GLModelVAO(textures, normals);
	}
	
	public void disposeGlModel() {
		glModel.getDisposeAction().run();
		glModel = null;
	}
	
	public GLModel gl() {
		if(glModel == null) throw new IllegalStateException("No GLModel present in model");
		return glModel;
	}
	
	private final class GLModelVAO implements GLModel, GLDisposable {

		private final boolean textures;
		private final boolean normals;
		
		private final List<GLMeshVBO> buffers;
		
		private final VertexArray vao;
		
		public GLModelVAO(boolean textures, boolean normals) {
			this.textures = textures;
			this.normals = normals;
			
			this.buffers = new ArrayList<>();
			
			this.vao = VertexArray.create();
			updateModelData();
		}
		

		@Override
		public Matrix4f toWorldSpace() {
			return objectToWorldSpace;
		}
		
		private void layoutVAO() {
			if(textures && normals) {
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 0, 3, 8, 0); //position
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 1, 2, 8, 3); //texture
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 2, 3, 8, 5); //normal
			} else if(normals) {
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 0, 3, 6, 0); //position
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 2, 3, 6, 3); //normal
			} else if(textures) {
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 0, 3, 5, 0); //position
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 1, 2, 5, 3); //texture
			} else {
				vao.layoutVertexBufferUnchecked(GL11.GL_FLOAT, Float.BYTES, 0, 3, 0, 0); //position
			}
		}

		@Override
		public void render() {
			vao.enable();
			for(GLMeshVBO buf : buffers) {
				buf.vbo.bindForRendering();
				vao.drawVertices(buf.count);
			}
			vao.disable();
		}

		@Override
		public void updateModelData() {
			vao.enable();
			buffers.forEach(GLMeshVBO::dispose);
			buffers.clear();
			
			for(Mesh mesh : prefab.meshes.values()) {
				GLMeshVBO glm = new GLMeshVBO(mesh);
				glm.vbo.putData(mesh.makeData(textures, normals), BufferUsage.STATIC_DRAW);
				layoutVAO();
				buffers.add(glm);
			}
			vao.disable();
		}

		@Override
		public Runnable getDisposeAction() {
			return vao.getDisposeAction();
		}
	
		private final class GLMeshVBO implements GLDisposable {
			public GLMeshVBO(Mesh mesh) {
				this.vbo = vao.createBuffer(BufferLocation.VERTEX_DATA);
				this.count = mesh.getFaceCount() * 3;
			}
			
			private final BufferObject vbo;
			private final int count;
			
			@Override
			public Runnable getDisposeAction() {
				return vbo.getDisposeAction();
			}
		}
	}
}

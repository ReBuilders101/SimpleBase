package lb.simplebase.glcore.oop;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import lb.simplebase.gl.GLHandle;
import lb.simplebase.glcore.RequireVAO;

public class BufferObject implements GLHandle, GLDisposable {

	public static final BufferObject NULL = null; //Used for varargs
	
	private final int handle;
	private final BufferLocation type;
	private int internalDataType;
	private int dataStrideFactor;
	private final VertexArray context;
	
	protected BufferLocation getLocation() {
		return type;
	}
	
	protected int getDataType() {
		return internalDataType;
	}
	
	protected int getDataFactor() {
		return dataStrideFactor;
	}
	
	protected VertexArray getContext() {
		return context;
	}
	
	private void setContext() {
		if(context != null) {
			context.enable();
		}
	}
	
	protected BufferObject(int id, BufferLocation nativePeer, VertexArray context) {
		this.handle = id;
		this.type = nativePeer;
		this.context = context;
		this.internalDataType = 0;
		this.dataStrideFactor = 0;
		GLDisposable.registerTask(this);
	}
	
	@RequireVAO
	public void putData(float[] data, BufferUsage dataUsage) {
		setContext();
		GL15.glBindBuffer(type.handle, handle);
		GL15.glBufferData(type.handle, data, dataUsage.handle);
		internalDataType = GL11.GL_FLOAT;
		dataStrideFactor = Float.BYTES;
	}
	
	@RequireVAO
	public void putData(FloatBuffer data, BufferUsage dataUsage) {
		setContext();
		GL15.glBindBuffer(type.handle, handle);
		GL15.glBufferData(type.handle, data, dataUsage.handle);
		internalDataType = GL11.GL_FLOAT;
		dataStrideFactor = Float.BYTES;
	}
	
	@RequireVAO
	public void updateSubData(long startOffset, FloatBuffer data) {
		setContext();
		if(internalDataType != GL11.GL_FLOAT) throw new IllegalStateException("Buffer type is not GL_FLOAT");
		GL15.glBindBuffer(type.handle, handle);
		GL15.glBufferSubData(handle, startOffset, data);
	}

	@RequireVAO
	public void updateSubData(long startOffset, float[] data) {
		setContext();
		if(internalDataType != GL11.GL_FLOAT) throw new IllegalStateException("Buffer type is not GL_FLOAT");
		GL15.glBindBuffer(type.handle, handle);
		GL15.glBufferSubData(handle, startOffset, data);
	}
	
	public boolean hasData() {
		return internalDataType != 0;
	}
	
	@RequireVAO
	public void bindForRendering() {
		GL15.glBindBuffer(type.handle, handle);
	}
	
	@Override
	public int getGLHandle() {
		return handle;
	}
	
	@Override
	public Runnable getDisposeAction() {
		return () -> GL15.glDeleteBuffers(handle);
	}
	
	public static BufferObject createUnbound(BufferLocation nativePeer) {
		return new BufferObject(GL15.glGenBuffers(), nativePeer, null);
	}
	
	public static enum BufferUsage implements GLHandle {
		STATIC_DRAW(GL15.GL_STATIC_DRAW);

		private final int handle;
		private BufferUsage(int handle) {
			this.handle = handle;
		}
		
		@Override
		public int getGLHandle() {
			return handle;
		}
	}
	
	public static enum BufferLocation implements GLHandle {
		VERTEX_DATA(GL30.GL_ARRAY_BUFFER),
		VERTEX_INDEX_DATA(GL15.GL_ELEMENT_ARRAY_BUFFER);

		private final int handle;
		private BufferLocation(int handle) {
			this.handle = handle;
		}
		
		@Override
		public int getGLHandle() {
			return handle;
		}
		
	}
}

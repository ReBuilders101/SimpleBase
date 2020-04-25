package lb.simplebase.gl.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import lb.simplebase.gl.GLHandle;

public class RenderContext implements GLHandle {

	private final boolean indexed;
	private final int vertexArray;
	private final int renderCount;
	private final int renderMode;
	private final int indexType;
	private final int[] buffers;
	
	private RenderContext(boolean indexed, int vertexArray, int renderCount, int renderMode, int indexType, int[] buffers) {
		this.indexed = indexed;
		this.vertexArray = vertexArray;
		this.renderCount = renderCount;
		this.renderMode = renderMode;
		this.indexType = indexType;
		this.buffers = buffers;
	}
	
	public static RenderContext forVAO(int vaoHandle, int renderCount) {
		return forVAOImpl(vaoHandle, renderCount, GL11.GL_TRIANGLES, null);
	}
	
	public static RenderContext forVAO(int vaoHandle, int renderCount, int renderMode) {
		return forVAOImpl(vaoHandle, renderCount, renderMode, null);
	}
	
	public static RenderContext forVAO(int vaoHandle, int renderCount, int[] buffers) {
		return forVAOImpl(vaoHandle, renderCount, GL11.GL_TRIANGLES, buffers);
	}
	
	public static RenderContext forVAO(int vaoHandle, int renderCount, int renderMode, int...buffers) {
		return forVAOImpl(vaoHandle, renderCount, renderMode, buffers);
	}
	
	private static RenderContext forVAOImpl(int vaoHandle, int renderCount, int renderMode, int[] buffers) {
		return new RenderContext(false, vaoHandle, renderCount, renderMode, 0, buffers);
	}
	
	
	
	public static RenderContext forVAOIndexed(int vaoHandle, int renderCount) {
		return forVAOIndexedImpl(vaoHandle, renderCount, GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_INT, null);
	}
	
	public static RenderContext forVAOIndexed(int vaoHandle, int renderCount, int renderMode, int indexType) {
		return forVAOIndexedImpl(vaoHandle, renderCount, renderMode, indexType, null);
	}
	
	public static RenderContext forVAOIndexed(int vaoHandle, int renderCount, int[] buffers) {
		return forVAOIndexedImpl(vaoHandle, renderCount, GL11.GL_TRIANGLES, GL11.GL_UNSIGNED_INT, buffers);
	}
	
	public static RenderContext forVAOIndexed(int vaoHandle, int renderCount, int renderMode, int indexType, int...buffers) {
		return forVAOIndexedImpl(vaoHandle, renderCount, renderMode, indexType, buffers);
	}
	
	private static RenderContext forVAOIndexedImpl(int vaoHandle, int renderCount, int renderMode, int indexType, int...buffers) {
		return new RenderContext(true, vaoHandle, renderCount, renderMode, indexType, buffers);
	}
	
	/**
	 * Will NOT bind the array
	 */
	public void draw() {
		if(indexed) {
			GL11.glDrawElements(renderCount, renderCount, indexType, 0);
		} else {
			GL11.glDrawArrays(renderMode, 0, renderCount);
		}
	}
	
	public final void bind() {
		GL30.glBindVertexArray(vertexArray);
	}
	
	public boolean isIndexedRendering() {
		return indexed;
	}
	
	public int getBuffer(int index) {
		if(buffers == null) throw new ArrayIndexOutOfBoundsException(0);
		return buffers[index];
	}
	
	public int getBufferCount() {
		return buffers == null ? 0 : buffers.length;
	}
	
	@Override
	public int getGLHandle() {
		return vertexArray;
	}
	
	public static  void disposeResources(RenderContext...context) {
		if(context != null) {
			for(RenderContext c : context) {
				if(c != null) c.disposeResources();
			}
		}
	}
	
	public void disposeResources() {
		if(buffers != null) {
			for(int buf : buffers) {
				GL33.glDeleteBuffers(buf);
			}
		}
		GL30.glDeleteVertexArrays(vertexArray);
	}
	
}

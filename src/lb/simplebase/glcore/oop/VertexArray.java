package lb.simplebase.glcore.oop;

import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import lb.simplebase.gl.GLHandleLong;
import lb.simplebase.glcore.RequireVAO;
import lb.simplebase.glcore.oop.BufferObject.BufferLocation;

public class VertexArray implements GLHandleLong, GLBindable, GLDisposable {
	
	private final int vaoHandle;
	private final Set<Integer> layoutIds;
	private int drawMode;
	
	public static VertexArray create() {
		return new VertexArray(GL30.glGenVertexArrays());
	}
	
	private VertexArray(int handle) {
		this.vaoHandle = handle;
		this.layoutIds = new LinkedHashSet<>();
		this.drawMode = GL11.GL_TRIANGLES;
		GLDisposable.registerTask(this);
	}
	
	@Override
	public int getGLHandle() {
		return vaoHandle;
	}

	@Override
	public void enable() {
		GL30.glBindVertexArray(vaoHandle);
	}

	@Override
	public void disable() {
		GL30.glBindVertexArray(0);
	}
	
	@RequireVAO
	public void layoutVertexBuffer(BufferObject data, int layoutId, int valuesPerVertex) {
		layoutVertexBuffer(data, layoutId, valuesPerVertex, 0, 0);
	}
	
	@RequireVAO
	public void layoutVertexBuffer(BufferObject data, int layoutId, int valuesPerVertex, int stride, int offset) {
		if(data.getLocation() != BufferLocation.VERTEX_DATA)
			throw new IllegalArgumentException("Buffer must be bound to the vertex array (GL_ARRAY_BUFFER)");
		data.bindForRendering(); //In this case, to bind it to the vertex array
		GL20.glVertexAttribPointer(layoutId, valuesPerVertex, data.getDataType(), false, stride * data.getDataFactor(), offset * data.getDataFactor());
		layoutIds.add(layoutId);
	}
	
	@RequireVAO
	public void layoutVertexBufferUnchecked(int type, int factor, int layoutId, int valuesPerVertex, int stride, int offset) {
		GL20.glVertexAttribPointer(layoutId, valuesPerVertex, type, false, stride * factor, offset * factor);
		layoutIds.add(layoutId);
	}
	
	public void disableLayoutId(int layoutId) {
		layoutIds.remove(layoutId);
	}
	
	public BufferObject createBuffer(BufferLocation location) {
		return new BufferObject(GL15.glGenBuffers(), location, this);
	}

	@Override
	public Runnable getDisposeAction() {
		return () -> GL30.glDeleteVertexArrays(vaoHandle);
	}
	
	public void drawVertices(int count) {
		drawVertices(count, 0);
	}
	
	public void drawIndexedVertices(int count, BufferObject indexBuffer) {
		layoutIds.forEach((i) -> GL20.glEnableVertexAttribArray(i));
		GL11.glDrawElements(drawMode, count, indexBuffer.getDataType(), 0);
		layoutIds.forEach((i) -> GL20.glDisableVertexAttribArray(i));
	}
	
	public void drawVertices(int count, int offset) {
		layoutIds.forEach((i) -> GL20.glEnableVertexAttribArray(i));
		GL11.glDrawArrays(drawMode, offset, count);
		layoutIds.forEach((i) -> GL20.glDisableVertexAttribArray(i));
	}
	
	public void setGLDrawMode(DrawMode mode) {
		this.drawMode = mode.handle;
	}
	
	public static enum DrawMode implements GLHandleLong {
		TRIANGLES(GL11.GL_TRIANGLES),
		LINES(GL11.GL_LINES);

		private final int handle;
		private DrawMode(int handle) {
			this.handle = handle;
		}
		@Override
		public int getGLHandle() {
			return handle;
		}
		
	}
	
	public static void disableAll() {
		GL30.glBindVertexArray(0);
	}
	
}

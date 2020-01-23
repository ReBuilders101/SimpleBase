package lb.simplebase.glcore.oop;

import java.util.LinkedHashSet;
import java.util.Set;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import lb.simplebase.glcore.oop.BufferObject.BufferLocation;

public class VertexArray implements GLHandle, GLBindable, GLDisposable {
	
	private final int vaoHandle;
	private final Set<Integer> layoutIds;
	
	public static VertexArray create() {
		return new VertexArray(GL30.glGenVertexArrays());
	}
	
	private VertexArray(int handle) {
		this.vaoHandle = handle;
		this.layoutIds = new LinkedHashSet<>();
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
	
	public void layoutVertexBuffer(BufferObject data, int layoutId, int valuesPerVertex) {
		layoutVertexBuffer(data, layoutId, valuesPerVertex, 0, 0);
	}
	
	public void layoutVertexBuffer(BufferObject data, int layoutId, int valuesPerVertex, int stride, int offset) {
		if(data.getLocation() != BufferLocation.ARRAY_BUFFER)
			throw new IllegalArgumentException("Buffer must be bound to the vertex array (GL_ARRAY_BUFFER)");
		GL20.glVertexAttribPointer(layoutId, valuesPerVertex, data.getDataType(), false, stride, offset);
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
		drawVertices(0, count);
	}
	
	public void drawVertices(int offset, int count) {
		layoutIds.forEach((i) -> GL20.glEnableVertexAttribArray(i));
		GL11.glDrawArrays(GL11.GL_TRIANGLES, offset, count);
		layoutIds.forEach((i) -> GL20.glDisableVertexAttribArray(i));
	}

}

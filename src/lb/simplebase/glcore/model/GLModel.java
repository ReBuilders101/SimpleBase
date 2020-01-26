package lb.simplebase.glcore.model;

import lb.simplebase.glcore.oop.VertexArray;

public interface GLModel {

	public int getTriCount();

	public int getVertexCount();
	
	public void render(VertexArray vao);

	public void fillAndLayout(VertexArray array, boolean textures, boolean normal);
}

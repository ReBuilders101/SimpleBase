package lb.simplebase.glcore.scene;

import javax.vecmath.Matrix4f;

public interface GLModel {
	
	public void render();

	public void updateModelData();
	
	public Matrix4f toWorldSpace();
	
}

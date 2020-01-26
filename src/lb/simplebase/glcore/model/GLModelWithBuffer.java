package lb.simplebase.glcore.model;

import lb.simplebase.glcore.oop.BufferObject;
import lb.simplebase.glcore.oop.VertexArray;
import lb.simplebase.glcore.oop.BufferObject.BufferLocation;
import lb.simplebase.glcore.oop.BufferObject.BufferUsage;

class GLModelWithBuffer implements GLModel {

	private final BufferObject buffer;
	private final Model model;
	private final BufferUsage usage;
	
	protected GLModelWithBuffer(Model model, BufferUsage modelUsage) {
		this.buffer = BufferObject.createUnbound(BufferLocation.VERTEX_DATA);
		this.model = model;
		this.usage = modelUsage;
	}
	
	@Override
	public int getTriCount() {
		return model.getFaceCount();
	}

	@Override
	public int getVertexCount() {
		return model.getVertexCount(); 
	}

	@Override
	public void fillAndLayout(VertexArray array, boolean textures, boolean normal) {
		array.enable();
		buffer.bindForRendering();
		buffer.putData(model.makeDataVTN(), usage);
		
		array.layoutVertexBuffer(buffer, 0, 3, 8, 0); //position
		array.layoutVertexBuffer(buffer, 1, 2, 8, 3); //texture
		array.layoutVertexBuffer(buffer, 2, 3, 8, 5); //normal
	}

	@Override
	public void render(VertexArray array) {
		array.enable();
		buffer.bindForRendering();
		array.drawVertices(getVertexCount());
	}

}

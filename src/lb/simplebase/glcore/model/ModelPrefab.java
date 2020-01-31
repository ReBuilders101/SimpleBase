package lb.simplebase.glcore.model;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3f;

public class ModelPrefab {

	private final MaterialLibrary materials;
	protected final Map<String, Mesh> meshes;
	
	public ModelPrefab(MaterialLibrary lib) {
		materials = lib;
		meshes = new HashMap<>();
	}
	
	public Mesh getMesh(String name) {
		return meshes.get(name);
	}
	
	public void removeMesh(String name) {
		meshes.remove(name);
	}
	
	public Mesh getOrCreateMesh(String name) {
		if(meshes.containsKey(name)) return meshes.get(name);
		Mesh mesh = new Mesh(name, this);
		meshes.put(name, mesh);
		return mesh;
	}
	
	protected void forcePutMesh(String name, Mesh mesh) {
		meshes.put(name, mesh);
	}
	
	public MaterialLibrary getMaterials() {
		return materials;
	}
	
	public Material getOrAdd(Material material) {
		materials.addMaterial(material);
		return material;
	}
	
	public Model createModelInstance(float x, float y, float z) {
		return new Model(this, x, y, z, 0, 0, 0);
	}
	
	public Model createModelInstance(Vector3f position) {
		return new Model(this, position.x, position.y, position.z, 0, 0, 0);
	}
	
	public Model createModelInstance(float x, float y, float z, float pitch, float yaw, float roll) {
		return new Model(this, x, y, z, pitch, yaw, roll);
	}
	
	public Model createModelInstance(Vector3f position, float pitch, float yaw, float roll) {
		return new Model(this, position.x, position.y, position.z, pitch, yaw, roll);
	}
	
	public Model createModelInstance(float[] positionAndRotation) {
		if(positionAndRotation.length < 6) throw new ArrayIndexOutOfBoundsException("Array length for model position and rotation must be at least 6");
		return new Model(this, positionAndRotation[0], positionAndRotation[1], positionAndRotation[2],
				positionAndRotation[3], positionAndRotation[4], positionAndRotation[5]);
	}
	
	public Model createModelInstance(float[] positionAndRotation, int offset) {
		if(positionAndRotation.length < 6 + offset) throw new ArrayIndexOutOfBoundsException(
				"Array length for model position and rotation must be at least 6 after the offset");
		return new Model(this, positionAndRotation[0 + offset], positionAndRotation[1 + offset], positionAndRotation[2 + offset],
				positionAndRotation[3 + offset], positionAndRotation[4 + offset], positionAndRotation[5 + offset]);
	}
	
}

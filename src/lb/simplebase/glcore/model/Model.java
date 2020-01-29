package lb.simplebase.glcore.model;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.lwjgl.BufferUtils;

import lb.simplebase.glcore.oop.BufferObject.BufferUsage;
import lb.simplebase.glcore.oop.VertexArray;

@SuppressWarnings("unused") //For the BufferUtils import, in case I get that working again
public class Model {
	
	private final String name;
	private final Set<Vertex> vertices;
	private final Set<Face> faces;
	private final MaterialLibrary mtllib;
	
	protected Model(String name) {
		this(name, new HashSet<>(), new HashSet<>(), MaterialLibrary.DISABLED);
	}
	
	protected Model(String name, Set<Vertex> vertices, Set<Face> faces, MaterialLibrary materials) {
		this.name = name;
		this.vertices = vertices;
		this.faces = faces;
		this.mtllib = materials;
	}
	
	public String getModelName() {
		return name;
	}
	
	public Vertex getOrAddVertex(float x, float y, float z) {
		for(Vertex v : vertices) {
			if(!v.hasValidNormals() && !v.hasValidTextures() && //Correct state
					v.getPositionX() == x && v.getPositionY() == y && v.getPositionZ() == z) { //Correct position
				return v;
			}
		}
		Vertex vtx = new Vertex(new float[] {x, y, z, 0, 0, 0, 0, 0}, false, false);
		vertices.add(vtx);
		return vtx;
	}
	
	public Vertex getOrAddVertex(float x, float y, float z, float u, float v) {
		for(Vertex vtx : vertices) {
			if(!vtx.hasValidNormals() && vtx.hasValidTextures() && //Correct state
					vtx.getPositionX() == x && vtx.getPositionY() == y && vtx.getPositionZ() == z &&  //Correct position
					vtx.getTextureU() == u && vtx.getTextureV() == v) { //Correct UVs
				return vtx;
			}
		}
		Vertex vtx = new Vertex(new float[] {x, y, z, u, v, 0, 0, 0}, false, false);
		vertices.add(vtx);
		return vtx;
	}
	
	public Vertex getOrAddVertex(float x, float y, float z, float nx, float ny, float nz) {
		for(Vertex vtx : vertices) {
			if(vtx.hasValidNormals() && !vtx.hasValidTextures() && //Correct state
					vtx.getPositionX() == x && vtx.getPositionY() == y && vtx.getPositionZ() == z && //Correct position
					vtx.getNormalX() == nx && vtx.getNormalY() == ny && vtx.getNormalZ() == nz) { //Correct normal
				return vtx;
			}
		}
		Vertex vtx = new Vertex(new float[] {x, y, z, 0, 0, nx, ny, nz}, false, false);
		vertices.add(vtx);
		return vtx;
	}
	
	public Vertex getOrAddVertex(float x, float y, float z, float u, float v, float nx, float ny, float nz) {
		for(Vertex vtx : vertices) {
			if(vtx.hasValidNormals() && vtx.hasValidTextures() && //Correct state
					vtx.getPositionX() == x && vtx.getPositionY() == y && vtx.getPositionZ() == z && //Correct position
					vtx.getTextureU() == u && vtx.getTextureV() == v &&	//correct UVs
					vtx.getNormalX() == nx && vtx.getNormalY() == ny && vtx.getNormalZ() == nz) { //Correct normal
				return vtx;
			}
		}
		Vertex vtx = new Vertex(new float[] {x, y, z, u, v, nx, ny, nz}, false, false);
		vertices.add(vtx);
		return vtx;
	}
	
	public Vertex getOrAddVertex(Vertex vtx) {
		Objects.requireNonNull(vtx, "Vertex to add or check cannot be null");
		for(Vertex v : vertices) {
			if(vtx.equals(v)) return v; //return the one from the set 
		}
		vertices.add(vtx);
		return vtx;
	}
	
	public Face addFace(Vertex v1, Vertex v2, Vertex v3, Material material) {
		return addFace(v1, v2, v3, material, -1, null);
	}
	
	public Face addFace(Vertex v1, Vertex v2, Vertex v3, Material material, int smoothingGroup) {
		return addFace(v1, v2, v3, material, smoothingGroup, null);
	}
	
	public Face addFace(Vertex v1, Vertex v2, Vertex v3, Material material, String groupName) {
		return addFace(v1, v2, v3, material, -1, groupName);
	}
	
	public Face addFace(Vertex v1, Vertex v2, Vertex v3, Material material, int smoothingGroup, String groupName) {
		synchronized (faces) {
			mtllib.addMaterial(material);
			Face face = new Face(v1, v2, v3, smoothingGroup, material, groupName);
			faces.add(face);
			return face;
		}
	}
	
	public Set<Vertex> getVertices() {
		return Collections.unmodifiableSet(vertices);
	}
	
	public Set<Face> getFaces() {
		return Collections.unmodifiableSet(faces);
	}
	
	public MaterialLibrary getMaterials() {
		return mtllib;
	}
	
	/**
	 * Makes model for rendering with OpenGL
	 * Call {@link GLModel#fillAndLayout(lb.simplebase.glcore.oop.VertexArray, boolean, boolean)} with a {@link VertexArray}
	 * to layout the loactions for the shader and put the data from the model into the GL Buffer.
	 * The vertex array should not be modified after calling that method. It will be the active vertex array when that method retruns.
	 * Afterwards, the {@link GLModel#render()} method can be called to send the model information to the shader.<p>
	 * The shader must accept information in this format:<ul>
	 * <li>location = 0: vec3 (position of vertex)</li>
	 * <li>location = 1: vec2 (texture uv)</li>
	 * <li>location = 2: vex3 (vertex normal vector)</li></ul>
	 */
	public GLModel makeRenderModel() {
		return new GLModelWithBuffer(this, BufferUsage.STATIC_DRAW);
	}
	
	public int getFaceCount() {
		return faces.size();
	}
	
	public int getVertexCount() {
		return vertices.size();
	}
	
	protected int effectiveVertexCount() {
		return faces.size() * 3;
	}
	
	protected float[] makeDataVTN() {
		synchronized (faces) {
//			final FloatBuffer data = BufferUtils.createFloatBuffer(faces.size() * 24); //24 -> 3 vertices per face, 8 values per vertex
			final FloatBuffer data = FloatBuffer.allocate(faces.size() * 24);
			for(Face face : faces) {
				data.put(face.getVertex1().data);
				data.put(face.getVertex2().data);
				data.put(face.getVertex3().data);
			}
			return data.array();
		}
	}
	
	protected float[] makeDataVN() {
		synchronized (faces) {
//			final FloatBuffer data = BufferUtils.createFloatBuffer(faces.size() * 18); //24 -> 3 vertices per face, 6 values per vertex
			final FloatBuffer data = FloatBuffer.allocate(faces.size() * 18);
			for(Face face : faces) {
				data.put(face.getVertex1().data, 0, 3);
				data.put(face.getVertex1().data, 5, 3);
				
				data.put(face.getVertex2().data, 0, 3);
				data.put(face.getVertex2().data, 5, 3);
				
				data.put(face.getVertex3().data, 0, 3);
				data.put(face.getVertex3().data, 5, 3);
			}
			return data.array();
		}
	}
	
	protected float[] makeDataVT() {
		synchronized (faces) {
//			final FloatBuffer data = BufferUtils.createFloatBuffer(faces.size() * 15); //24 -> 3 vertices per face, 5 values per vertex
			final FloatBuffer data = FloatBuffer.allocate(faces.size() * 15);
			for(Face face : faces) {
				data.put(face.getVertex1().data, 0, 5);
				data.put(face.getVertex2().data, 0, 5);
				data.put(face.getVertex3().data, 0, 5);
			}
			return data.array();
		}
	}
	
	protected float[] makeDataV() {
		synchronized (faces) {
//			final FloatBuffer data = BufferUtils.createFloatBuffer(faces.size() * 9);
			final FloatBuffer data = FloatBuffer.allocate(faces.size() * 9);//BufferUtils.createFloatBuffer(faces.size() * 9); //24 -> 3 vertices per face, 3 values per vertex
			for(Face face : faces) {
				data.put(face.getVertex1().data, 0, 3);
				data.put(face.getVertex2().data, 0, 3);
				data.put(face.getVertex3().data, 0, 3);
			}
			return data.array();
		}
	}
	
//	private float[] array(FloatBuffer buf) {
//		if(buf.hasArray()) return buf.array();
//		buf.flip();
//		float[] data = new float[buf.limit()];
//		buf.get(data);
//		return data;
//	}
	
}

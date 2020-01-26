package lb.simplebase.glcore.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lb.simplebase.glcore.model.ModelLoader.LoadPath;
import lb.simplebase.util.SupplierThrows;

public class ModelBuilder {

	private final List<float[]> vertexPosPrimers;
	private final List<float[]> vertexTexPrimers;
	private final List<float[]> vertexNrmPrimers;
	private final List<String> materialLibPaths;
	private final List<FacePrimer> facePrimers;
	
//	private final List<E>
	
	private int smoothingGroup;
	private String modelName;
	private String currentGroupName;
	private String currentMaterialName;
	
	protected ModelBuilder() {
		modelName = null;
		currentGroupName = null;
		currentMaterialName = null;
		smoothingGroup = -1;
		
		vertexPosPrimers = new ArrayList<>();
		vertexTexPrimers = new ArrayList<>();
		vertexNrmPrimers = new ArrayList<>();
		materialLibPaths = new ArrayList<>();
		facePrimers = new ArrayList<>();
	}

	protected void appendMaterialLibrary(String libName) {
		materialLibPaths.add(libName);
	}
	
	protected void appendModelName(String name) throws ModelFormatException {
		if(modelName == null) {
			modelName = name;
		} else {
			throw new ModelFormatException("Cannot set model name twice", this);
		}
	}
	
	protected void appendVertex3(float[] xyz) {
		vertexPosPrimers.add(xyz);
	}
	
	protected void appendVertex4(float[] xyzw) {
		vertexPosPrimers.add(xyzw);
	}
	
	protected void appendTextureCoordinate(float[] uv) {
		vertexTexPrimers.add(uv);
	}
	
	protected void appendNormal(float[] xyz) {
		vertexNrmPrimers.add(xyz);
	}
	
	protected void beginFaceGroup(String groupName) {
		currentGroupName = groupName;
	}
	
	protected void setFaceMaterial(String materialName) {
		currentMaterialName = materialName;
	}
	
	protected void beginSmoothFaceGroup(int groupId) {
		smoothingGroup = groupId;
	}
	
	protected void endSmoothFaceGroup() {
		smoothingGroup = -1;
	}
	
	protected void appendFaceV(int[] vertex1, int[] vertex2, int[] vertex3, String v1Text, String v2Text, String v3Text, int line) {
		facePrimers.add(new FacePrimer(vertex1, vertex2, vertex3, v1Text, v2Text, v2Text, smoothingGroup, currentMaterialName, currentGroupName, line));
	}
	
	public Model build(LoadPath materialLookupPath) throws ModelFormatException {
		//Preconditions
		if(modelName == null) throw new ModelFormatException("Model name cannot be null / undefined", this);

		final MaterialLibrary mtllib;
		try {
			mtllib = materialLookupPath == null ? MaterialLibrary.DISABLED 
					: new MaterialLibrary(materialLookupPath, true, materialLibPaths.toArray(new String[materialLibPaths.size()]));
		} catch (IOException e) {
			throw new ModelFormatException("Could not resolve external material names", e, this);
		}
		
		//Data
		final Map<String, Vertex> vertexCache = new HashMap<>();
		final Set<Face> faces = new HashSet<>();
		//Process all faces
		for(FacePrimer currentFace : facePrimers) {
			//Register all vertices
			final Vertex v1 = getOrPut(vertexCache, currentFace.v1Text, () -> makeVertex(currentFace.vertex1, currentFace.line));
			final Vertex v2 = getOrPut(vertexCache, currentFace.v2Text, () -> makeVertex(currentFace.vertex2, currentFace.line));
			final Vertex v3 = getOrPut(vertexCache, currentFace.v3Text, () -> makeVertex(currentFace.vertex3, currentFace.line));
			
			final Face face = new Face(v1, v2, v3, currentFace.smoothingGroup, mtllib.getMaterial(currentFace.materialName), currentFace.groupName);
			faces.add(face);
		}
		
		return new Model(modelName, new HashSet<>(vertexCache.values()), faces, mtllib);
	}
	
	private Vertex makeVertex(int[] vertices, int line) throws ModelFormatException {
		final int vtxPos = vertices[0];
		final int vtxTex = vertices[1];
		final int vtxNrm = vertices[2];

		final boolean text = vtxTex != -1;
		final boolean norm = vtxNrm != -1;
		
		if(vtxPos < 0 || vtxPos >= vertexPosPrimers.size()) throw new ModelFormatException("Face vertex position index out of range @l" + line,
				new ArrayIndexOutOfBoundsException(vtxPos), this);
		if(text && (vtxTex < 0 || vtxTex >= vertexTexPrimers.size())) throw new ModelFormatException("Face vertex texture index out of range @l" + line,
				new ArrayIndexOutOfBoundsException(vtxTex), this);
		if(norm && (vtxNrm < 0 || vtxNrm >= vertexNrmPrimers.size())) throw new ModelFormatException("Face vertex normal index out of range @l" + line,
				new ArrayIndexOutOfBoundsException(vtxNrm), this);

		final float[] vertexData = new float[8]; //xyz uv xyz
		System.arraycopy(vertexPosPrimers.get(vtxPos), 0, vertexData, 0, 3);
		if(text) System.arraycopy(vertexTexPrimers.get(vtxTex), 0, vertexData, 3, 2);
		if(norm) System.arraycopy(vertexNrmPrimers.get(vtxNrm), 0, vertexData, 5, 3);
		return new Vertex(vertexData, text, norm);
	}
	
	private <K, V, E extends Throwable> V getOrPut(Map<K, V> map, K key, SupplierThrows<V, E> value) throws E {
		if(map.containsKey(key)) {
			return map.get(key);
		} else {
			V val = value.get();
			map.put(key, val);
			return val;
		}
	}
	
	private static final class FacePrimer {
		private final int[] vertex1;
		private final int[] vertex2;
		private final int[] vertex3;
		
		private final String v1Text;
		private final String v2Text;
		private final String v3Text;
		
		private final int smoothingGroup;
		private final String materialName;
		private final String groupName;
		
		private final int line;
		
		private FacePrimer(int[] vertex1, int[] vertex2, int[] vertex3,
				String v1Text, String v2Text, String v3Text,
				int smoothingGroup, String materialName, String groupName, int line) {
			this.vertex1 = vertex1;
			this.vertex2 = vertex2;
			this.vertex3 = vertex3;
			
			this.v1Text = v1Text;
			this.v2Text = v2Text;
			this.v3Text = v3Text;
			
			this.smoothingGroup = smoothingGroup;
			this.materialName = materialName;
			this.groupName = groupName;
			
			this.line = line;
		}
		
	}
}

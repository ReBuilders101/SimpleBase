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

	private final List<MeshPrimer> meshes;
	private MeshPrimer currentMesh;
	private final List<String> materialLibPaths;
	
	private int smoothingGroup;
	private String currentGroupName;
	private String currentMaterialName;
	
	protected ModelBuilder() {
		materialLibPaths = new ArrayList<>();
		meshes = new ArrayList<>();
		currentMesh = null;
		
		smoothingGroup = -1;
		currentGroupName = null;
		currentMaterialName = null;
	}

	private void requireMesh() throws ModelFormatException {
		if(currentMesh == null) throw new ModelFormatException("No mesh set (missing 'o' command before vertex/face/normal/texture declaration", this);
	}
	
	protected void appendMaterialLibrary(String libName) {
		materialLibPaths.add(libName);
	}
	
	protected void beginMesh(String name) throws ModelFormatException {
		if(currentMesh != null) meshes.add(currentMesh);
		currentMesh = new MeshPrimer(name);
	}
	
	protected void appendVertex3(float[] xyz) throws ModelFormatException {
		requireMesh();
		currentMesh.vertexPosPrimers.add(xyz);
	}
	
	protected void appendVertex4(float[] xyzw) throws ModelFormatException {
		requireMesh();
		currentMesh.vertexPosPrimers.add(xyzw);
	}
	
	protected void appendTextureCoordinate(float[] uv) throws ModelFormatException {
		requireMesh();
		currentMesh.vertexTexPrimers.add(uv);
	}
	
	protected void appendNormal(float[] xyz) throws ModelFormatException {
		requireMesh();
		currentMesh.vertexNrmPrimers.add(xyz);
	}
	
	protected void beginFaceGroup(String groupName) throws ModelFormatException {
		currentGroupName = groupName;
	}
	
	protected void setFaceMaterial(String materialName) throws ModelFormatException {
		currentMaterialName = materialName;
	}
	
	protected void beginSmoothFaceGroup(int groupId) throws ModelFormatException {
		smoothingGroup = groupId;
	}
	
	protected void endSmoothFaceGroup() throws ModelFormatException {
		smoothingGroup = -1;
	}
	
	protected void appendFaceV(int[] vertex1, int[] vertex2, int[] vertex3, String v1Text, String v2Text, String v3Text, int line) throws ModelFormatException {
		requireMesh();
		currentMesh.facePrimers.add(new FacePrimer(vertex1, vertex2, vertex3, v1Text, v2Text, v2Text,
				smoothingGroup, currentMaterialName, currentGroupName, line));
	}
	
	public ModelPrefab build(LoadPath materialLookupPath) throws ModelFormatException {

		final MaterialLibrary mtllib;
		try {
			mtllib = materialLookupPath == null ? MaterialLibrary.DISABLED 
					: new MaterialLibrary(materialLookupPath, true, materialLibPaths.toArray(new String[materialLibPaths.size()]));
		} catch (IOException e) {
			throw new ModelFormatException("Could not resolve external material names", e, this);
		}
		ModelPrefab pre = new ModelPrefab(mtllib);
		//Data
		for(MeshPrimer mp : meshes) {
			Mesh mesh = makeMesh(mp, pre);
			pre.forcePutMesh(mesh.getMeshName(), mesh);
		}
		
		return pre;
	}
	
	private Mesh makeMesh(MeshPrimer mesh, ModelPrefab incompleteModel) throws ModelFormatException {
		final Map<String, Vertex> vertexCache = new HashMap<>();
		final Set<Face> faces = new HashSet<>();
		//Process all faces
		for(FacePrimer currentFace : mesh.facePrimers) {
			//Register all vertices
			final Vertex v1 = getOrPut(vertexCache, currentFace.v1Text, () -> makeVertex(mesh, currentFace.vertex1, currentFace.line));
			final Vertex v2 = getOrPut(vertexCache, currentFace.v2Text, () -> makeVertex(mesh, currentFace.vertex2, currentFace.line));
			final Vertex v3 = getOrPut(vertexCache, currentFace.v3Text, () -> makeVertex(mesh, currentFace.vertex3, currentFace.line));
			
			final Face face = new Face(v1, v2, v3, currentFace.smoothingGroup,
					incompleteModel.getMaterials().getMaterial(currentFace.materialName), currentFace.groupName);
			faces.add(face);
		}
		
		return new Mesh(mesh.meshName, incompleteModel, new HashSet<>(vertexCache.values()), faces);
	}
	
	private Vertex makeVertex(MeshPrimer mesh, int[] vertices, int line) throws ModelFormatException {
		//Apparently the indices are 1-based
		final int vtxPos = vertices[0] - 1;
		final int vtxTex = vertices[1] - 1;
		final int vtxNrm = vertices[2] - 1;

		final boolean text = vertices[1] != -1;
		final boolean norm = vertices[2] != -1;
		
		if(vtxPos < 0 || vtxPos >= mesh.vertexPosPrimers.size()) throw new ModelFormatException("Face vertex position index out of range @l" + line,
				new ArrayIndexOutOfBoundsException(vtxPos), this);
		if(text && (vtxTex < 0 || vtxTex >= mesh.vertexTexPrimers.size())) throw new ModelFormatException("Face vertex texture index out of range @l" + line,
				new ArrayIndexOutOfBoundsException(vtxTex), this);
		if(norm && (vtxNrm < 0 || vtxNrm >= mesh.vertexNrmPrimers.size())) throw new ModelFormatException("Face vertex normal index out of range @l" + line,
				new ArrayIndexOutOfBoundsException(vtxNrm), this);

		final float[] vertexData = new float[8]; //xyz uv xyz
		System.arraycopy(mesh.vertexPosPrimers.get(vtxPos), 0, vertexData, 0, 3);
		if(text) System.arraycopy(mesh.vertexTexPrimers.get(vtxTex), 0, vertexData, 3, 2);
		if(norm) System.arraycopy(mesh.vertexNrmPrimers.get(vtxNrm), 0, vertexData, 5, 3);
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
	
	private static final class MeshPrimer {
		public MeshPrimer(String name) {
			super();
			this.vertexPosPrimers = new ArrayList<>();
			this.vertexTexPrimers = new ArrayList<>();
			this.vertexNrmPrimers = new ArrayList<>();
			this.facePrimers = new ArrayList<>();
			this.meshName = name;
		}
		
		private final List<float[]> vertexPosPrimers;
		private final List<float[]> vertexTexPrimers;
		private final List<float[]> vertexNrmPrimers;
		private final List<FacePrimer> facePrimers;
		private final String meshName;
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

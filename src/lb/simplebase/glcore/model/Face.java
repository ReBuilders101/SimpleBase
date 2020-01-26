package lb.simplebase.glcore.model;

public class Face {

	private final Vertex v1;
	private final Vertex v2;
	private final Vertex v3;
	
	private final String groupName;
	private final int smoothingGroup;
	private final Material material;
	
	protected Face(Vertex v1, Vertex v2, Vertex v3, int smoothingGroup, Material material, String groupName) {
		this.v1 = v1;
		this.v2 = v2;
		this.v3 = v3;
		this.smoothingGroup = smoothingGroup;
		this.material = material;
		this.groupName = groupName;
	}

	public Vertex getVertex1() {
		return v1;
	}

	public Vertex getVertex2() {
		return v2;
	}

	public Vertex getVertex3() {
		return v3;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getSmoothingGroupId() {
		return smoothingGroup;
	}
	
	public boolean hasSmoothingGroup() {
		return smoothingGroup != -1;
	}

	public Material getMaterial() {
		return material;
	}
}

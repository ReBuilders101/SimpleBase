package lb.simplebase.glcore.model;

public class Material {

	protected static final Material DISABLED = new Material("<loading disabled>");
	
	private final String materialName;
	
	protected Material(String materialName) {
		this.materialName = materialName;
	}
	
	public String getName() {
		return materialName;
	}
	
	public boolean isDisabledMaterial() {
		return this == DISABLED;
	}
	
	//TODO expand this class
	
}

package lb.simplebase.glcore.model;

public class Material {

	public static final Material DISABLED = new Material("DisabledMaterial");
	
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

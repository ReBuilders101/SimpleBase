package lb.simplebase.registry;

public interface ResourceLocation extends Comparable<ResourceLocation> {
	
	public String asString();
	public boolean isEqual(ResourceLocation location);
	
	public static final ResourceLocation DEFAULT = new StringLocation("DEFAULT"); 
	
	public static class StringLocation implements ResourceLocation {

		private String location;
		
		public StringLocation(String location) {
			this.location = location;
		}
		
		@Override
		public int compareTo(ResourceLocation paramT) {
			return this.asString().compareTo(paramT.asString());
		}

		@Override
		public String asString() {
			return this.location;
		}

		@Override
		public boolean isEqual(ResourceLocation location) {
			return location.asString().equals(asString());
		}
	}
}

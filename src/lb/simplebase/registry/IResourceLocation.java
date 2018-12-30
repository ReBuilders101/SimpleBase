package lb.simplebase.registry;

public interface IResourceLocation extends Comparable<IResourceLocation> {
	
	public String asString();
	public boolean isEqual(IResourceLocation location);
	
	public static final IResourceLocation DEFAULT = new StringLocation("DEFAULT"); 
	
	public static class StringLocation implements IResourceLocation {

		private String location;
		
		public StringLocation(String location) {
			this.location = location;
		}
		
		@Override
		public int compareTo(IResourceLocation paramT) {
			return this.asString().compareTo(paramT.asString());
		}

		@Override
		public String asString() {
			return this.location;
		}

		@Override
		public boolean isEqual(IResourceLocation location) {
			return location.asString().equals(asString());
		}
	}
}

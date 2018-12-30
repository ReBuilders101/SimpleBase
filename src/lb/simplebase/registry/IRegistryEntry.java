package lb.simplebase.registry;

public interface IRegistryEntry {

	public boolean setRegistryName(IResourceLocation location);
	public IResourceLocation getRegistryName();
	public boolean hasRegistryName();
	
	
	public static abstract class RegistryEntry implements IRegistryEntry{
		
		private IResourceLocation location;
		private boolean hasName;
		
		protected RegistryEntry() {
			this.hasName = false;
		}
		
		protected RegistryEntry(IResourceLocation location) {
			this.location = location;
			this.hasName = true;
		}
		
		@Override
		public boolean setRegistryName(IResourceLocation location) {
			if(hasName) {
				return false;
			} else {
				this.location = location;
				this.hasName = true;
				return true;
			}
		}

		@Override
		public IResourceLocation getRegistryName() {
			return location;
		}

		@Override
		public boolean hasRegistryName() {
			return hasName;
		}
		
	}
	
}

package lb.simplebase.registry;

public interface RegistryEntry {

	public boolean setRegistryName(ResourceLocation location);
	public ResourceLocation getRegistryName();
	public boolean hasRegistryName();
	
	
	public static abstract class RegistryEntryImpl implements RegistryEntry{
		
		private ResourceLocation location;
		private boolean hasName;
		
		protected RegistryEntryImpl() {
			this.hasName = false;
		}
		
		protected RegistryEntryImpl(ResourceLocation location) {
			this.location = location;
			this.hasName = true;
		}
		
		@Override
		public boolean setRegistryName(ResourceLocation location) {
			if(hasName) {
				return false;
			} else {
				this.location = location;
				this.hasName = true;
				return true;
			}
		}

		@Override
		public ResourceLocation getRegistryName() {
			return location;
		}

		@Override
		public boolean hasRegistryName() {
			return hasName;
		}
		
	}
	
}

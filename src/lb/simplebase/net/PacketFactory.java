package lb.simplebase.net;

import java.util.EnumSet;

public class PacketFactory {
	
	public PacketFactory(NetworkManager manager, NetworkConnection connection) {
		addMappings(Mappings.class);
	}
	
	public void feed(byte data) {
		
	}
	
	public <T extends Enum<T> & PacketIdMapping> void addMappings(Class<T> e) {
		EnumSet<T> es = EnumSet.allOf(e);
		for(T t : es) {
			addMapping(t);
		}
	}
	
	public void addMapping(PacketIdMapping mapping) {
		
	}
	
	protected void notifyConnectionClosed() {
		
	}
	
	private static enum Mappings implements PacketIdMapping {
		;

		@Override
		public Packet getNewInstance() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getPacketId() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Class<? extends Packet> getPacketClass() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
}

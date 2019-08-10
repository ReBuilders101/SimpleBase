package lb.simplebase.net.simple;

import lb.simplebase.net.ObjectPacket;
import lb.simplebase.net.Packet;
import lb.simplebase.net.PacketIdMapping;

//Package visiblility, used internally
class StringMessagePacket extends ObjectPacket{

	protected StringMessagePacket() {
		super();
	}

	//Only strings allowed
	protected StringMessagePacket(String data) {
		super(data);
	}
	
	
	@Override
	public String getObject() {
		return (String) super.getObject();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(Class<T> type) {
		if(type == String.class) {
			return (T) getObject();
		} else {
			throw new IllegalArgumentException("The class must be equal to String.class");
		}
	}
	
	public static PacketIdMapping getMapping(final int id) {
		return new PacketIdMapping() {
			
			@Override
			public int getPacketId() {
				return id;
			}
			
			@Override
			public Class<? extends Packet> getPacketClass() {
				return StringMessagePacket.class;
			}
			
			@Override
			public Packet getNewInstance() {
				return new StringMessagePacket();
			}
		};
	}
}

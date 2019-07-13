package lb.simplebase.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import lb.simplebase.io.ReadableByteData;
import lb.simplebase.io.WritableByteData;

public class ObjectPacket implements Packet{
	
	private Object object;
	
	public ObjectPacket() {
		object = null;
	}
	
	public ObjectPacket(Serializable data) {
		object = data;
	}
	
	@Override
	public void writeData(WritableByteData data) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(data.getOutStream());
			out.writeObject(object);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void readData(ReadableByteData data) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(data.getInStream());
			object = in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Object getObject() {
		return object;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObject(Class<T> type) {
		return (T) object;
	}
	
	public static PacketIdMapping getMapping(final int id) {
		return new PacketIdMapping() {
			
			@Override
			public int getPacketId() {
				return id;
			}
			
			@Override
			public Class<? extends Packet> getPacketClass() {
				return ObjectPacket.class;
			}
			
			@Override
			public Packet getNewInstance() {
				return new ObjectPacket();
			}
		};
	}
	
}

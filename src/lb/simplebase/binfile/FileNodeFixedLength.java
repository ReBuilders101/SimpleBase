package lb.simplebase.binfile;

import java.util.Collection;

import lb.simplebase.net.Packet;

public class FileNodeFixedLength<T extends Packet> extends FileNode<T>{

	public FileNodeFixedLength(String name, Class<T> type, int itemlength) {
		super(name, type);
	}

	@Override
	protected void parseFill(byte[] data, Collection<T> toFill) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] createData() {
		// TODO Auto-generated method stub
		return null;
	}

}

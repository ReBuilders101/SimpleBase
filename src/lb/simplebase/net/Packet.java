package lb.simplebase.net;

public interface Packet {
	
	public void writeData(WriteableByteData data);
	public void readData(ReadableByteData data);
	
}

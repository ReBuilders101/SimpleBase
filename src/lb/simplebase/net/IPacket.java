package lb.simplebase.net;

public interface IPacket {
	
	public void writeData(IByteData data);
	public void readData(IByteData data);
	
}

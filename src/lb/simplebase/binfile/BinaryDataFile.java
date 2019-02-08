package lb.simplebase.binfile;

import java.util.Map;

public class BinaryDataFile {

	private Map<String, FileNode<?>> nodes;
	private FileNode.Factory factory;
	
	public <T> FileNode<T> registerFileNode(String name, Class<T> type) {
		FileNode<T> node = factory.create(name, type);
		nodes.put(name, node);
		return node;
	}
	
	/**
	 * Fills the FileNodes with data from a file
	 * @param data The file data
	 */
	public void parse(final byte[] data) {
		final ByteArrayReader reader = new ByteArrayReader(data);
		
		//Header - Node start positions
		final int headerItemLength = reader.readInt();		//Number of nodes
		final int[] nodeOffsets = new int[headerItemLength];//Node start offsets
		for(int i = 0; i < headerItemLength; i++) {
			nodeOffsets[i] = reader.readInt(); //Parse the node offsets
		}
		
		//Data - All nodes
		for(final int offset : nodeOffsets) {
			reader.jump(offset);
			final String nodeName = reader.readShortStringWithLength(); //Read name
			final int nodeDataLen = reader.readInt();	//Data length
			final byte[] nodeData = reader.read(nodeDataLen); //Read data
			
			final FileNode<?> toParse = nodes.get(nodeName);
			if(toParse != null) {
				toParse.parseAll(nodeData); //Parse everyting
			}
			
			//Async parse file nodes 
		}
	}
}

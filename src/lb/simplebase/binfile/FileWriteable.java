package lb.simplebase.binfile;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileWriteable implements  Iterable<FileNodeWriteable<?>>{
	
	private Map<String, FileNodeWriteable<?>> nodeElements;
	private FileTemplate template;
	private byte[] header;
	
	public FileWriteable(FileTemplate template) {
		this.template = template;
		this.header = new byte[0];
		this.nodeElements = new HashMap<>();
		//Add empty nodes
		for(FileNodeTemplate<?> node : template) {
			FileNodeWriteable<?> newNode = new FileNodeWriteable<>(node);
			this.nodeElements.put(node.getName(), newNode);
		}
	}
	
	public FileTemplate getTemplate() {
		return template;
	}
	
	public Map<String, FileNodeWriteable<?>> getNodeMap() {
		return Collections.unmodifiableMap(nodeElements);
	}
	
	public void setHeader(byte[] header) {
		this.header = header;
	}
	
	public boolean containsFileNode(String name) {
		if(name == null || name.isEmpty()) return false;
		return nodeElements.containsKey(name);
	}
	
	public FileNodeWriteable<?> getFileNode(String name) {
		if(!containsFileNode(name)) return null;
		return nodeElements.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> FileNodeWriteable<T> getFileNode(String name, Class<T> elementType) {
		return (FileNodeWriteable<T>) getFileNode(name);
	}
	
	public byte[] writeData() {
		ByteArrayWriter baw = new ByteArrayWriter();
		//Header first
		baw.writeInt(header.length);
		baw.write(header);
		//Node count
		baw.writeInt(nodeElements.size());
		//Then nodes
		for(FileNodeWriteable<?> node : this) {
			baw.writeShortStringWithLength(node.getName()); //Node name
			baw.writeInt(node.getElements().size()); //Element count
			//Need a sub-writer to get the size of the node
			ByteArrayWriter sbaw = new ByteArrayWriter();
			for(Object nodeData : node) {
				node.getTemplate().writeElementUnchecked(sbaw, nodeData);
			}
			byte[] sData = sbaw.getAsArray(); //Node data
			baw.writeInt(sData.length); //Byte size
			baw.write(sData); //Node data
		}
		return baw.getAsArray();
	}
	
	public void writeData(File file) {
		
	}

	@Override
	public Iterator<FileNodeWriteable<?>> iterator() {
		return nodeElements.values().iterator();
	}
	
}

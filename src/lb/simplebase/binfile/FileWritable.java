package lb.simplebase.binfile;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileWritable implements  Iterable<FileNodeWritable<?>>{
	
	private Map<String, FileNodeWritable<?>> nodeElements;
	private FileTemplate template;
	private byte[] header;
	
	public FileWritable(FileTemplate template) {
		this.template = template;
		this.header = new byte[0];
		this.nodeElements = new HashMap<>();
		//Add empty nodes
		for(FileNodeTemplate<?> node : template) {
			FileNodeWritable<?> newNode = new FileNodeWritable<>(node);
			this.nodeElements.put(node.getName(), newNode);
		}
	}
	
	public FileTemplate getTemplate() {
		return template;
	}
	
	public Map<String, FileNodeWritable<?>> getNodeMap() {
		return Collections.unmodifiableMap(nodeElements);
	}
	
	public void setHeader(byte[] header) {
		this.header = header;
	}
	
	public boolean containsFileNode(String name) {
		if(name == null || name.isEmpty()) return false;
		return nodeElements.containsKey(name);
	}
	
	public FileNodeWritable<?> getFileNode(String name) {
		if(!containsFileNode(name)) return null;
		return nodeElements.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> FileNodeWritable<T> getFileNode(String name, Class<T> elementType) {
		return (FileNodeWritable<T>) getFileNode(name);
	}
	
	public byte[] writeData() {
		ByteArrayWriter baw = new ByteArrayWriter();
		//Header first
		baw.writeInt(header.length);
		baw.write(header);
		//Node count
		baw.writeInt(nodeElements.size());
		//Then nodes
		for(FileNodeWritable<?> node : this) {
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
	public Iterator<FileNodeWritable<?>> iterator() {
		return nodeElements.values().iterator();
	}
	
}

package lb.simplebase.binfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lb.simplebase.net.ReadableByteData;

/**
 * A template which contains rules for reading a file
 */
public class FileTemplate implements Iterable<FileNodeTemplate<?>>{

	private Map<String, FileNodeTemplate<?>> nodes;
	
	public FileTemplate() {
		nodes = new HashMap<>();
	}
	
	public boolean addNode(FileNodeTemplate<?> node) {
		String name = node.getName();
		if(name == null || name.isEmpty()) return false;
		if(nodes.containsKey(name)) return false;
		nodes.put(name, node);
		return true;
	}
	
	public boolean forceAddNode(FileNodeTemplate<?> node) {
		String name = node.getName();
		if(name == null || name.isEmpty()) return false;
		nodes.put(name, node);
		return true;
	}
	
	public FileData parseData(File dataSource) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(dataSource);
			int length = (int) dataSource.length();
			byte[] data = new byte[length];
			fis.read(data);
			return parseData(data);
		}finally {
			if(fis != null) fis.close();
		}
	}
	
	public FileData parseData(byte[] data) {
		ReadableByteData readable = new ByteArrayReader(data);
		//Header
		int headerLen = readable.readInt();
		byte[] headerData = readable.read(headerLen);
		//Nodes
		Map<String, FileNode<?>> nodeDat = new HashMap<>();
		int nodeCount = readable.readInt(); //Number of nodes
		for(int i = 0; i < nodeCount; i++) {
			String currentNodeName = readable.readShortStringWithLength();
			int currentEntryCount = readable.readInt();
			int currentByteCount = readable.readInt();
			FileNodeTemplate<?> currentTemplate = nodes.get(currentNodeName);
			if(currentTemplate == null) { //Skip this node
				readable.skip(currentByteCount);
			} else { //read the node data
				List<Object> currentNodeData = new ArrayList<>();
				//for each entry
				for(int j = 0; j < currentEntryCount; j++) {
					currentNodeData.add(currentTemplate.parseElement(readable));
				}
				//After node was fully read
				FileNode<?> currentNode = new FileNode<>(currentNodeData, currentTemplate);
				nodeDat.put(currentNodeName, currentNode);
			}
		}
		//After all nodes
		return new FileData(headerData, nodeDat, this);
	}
	
	public FileWritable createWritable() {
		return new FileWritable(this); //TODO
	}

	@Override
	public Iterator<FileNodeTemplate<?>> iterator() {
		return nodes.values().iterator();
	}
}

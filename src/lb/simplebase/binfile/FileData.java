package lb.simplebase.binfile;

import java.util.Iterator;
import java.util.Map;

/**
 * Contains data read from a file through a template
 */
public class FileData implements Iterable<FileNode<?>>{

	private FileTemplate template;
	private Map<String, FileNode<?>> nodeElements;
	
	protected FileData(byte[] headerData, Map<String, FileNode<?>> nodeElements, FileTemplate template) {
		this.nodeElements = nodeElements;
		this.template = template;
	}
	
	@Override
	public Iterator<FileNode<?>> iterator() {
		return nodeElements.values().iterator();
	}
	
	public FileTemplate getTemplate() {
		return template;
	}
	
	public FileNode<?> getFileNode(String name) {
		if(!containsFileNode(name)) return null;
		return nodeElements.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> FileNode<T> getFileNode(String name, Class<T> elementType) {
		return (FileNode<T>) getFileNode(name);
	}
	
	public boolean containsFileNode(String name) {
		if(name == null || name.isEmpty()) return false;
		return nodeElements.containsKey(name);
	}
	
}

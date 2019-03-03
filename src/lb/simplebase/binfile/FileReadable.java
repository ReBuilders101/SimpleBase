package lb.simplebase.binfile;

import java.util.Iterator;
import java.util.Map;

/**
 * Contains data read from a file through a template
 */
public class FileReadable implements Iterable<FileNodeReadable<?>>{

	private FileTemplate template;
	private Map<String, FileNodeReadable<?>> nodeElements;
	
	protected FileReadable(byte[] headerData, Map<String, FileNodeReadable<?>> nodeElements, FileTemplate template) {
		this.nodeElements = nodeElements;
		this.template = template;
	}
	
	@Override
	public Iterator<FileNodeReadable<?>> iterator() {
		return nodeElements.values().iterator();
	}
	
	public FileTemplate getTemplate() {
		return template;
	}
	
	public FileNodeReadable<?> getFileNode(String name) {
		if(!containsFileNode(name)) return null;
		return nodeElements.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> FileNodeReadable<T> getFileNode(String name, Class<T> elementType) {
		return (FileNodeReadable<T>) getFileNode(name);
	}
	
	public boolean containsFileNode(String name) {
		if(name == null || name.isEmpty()) return false;
		return nodeElements.containsKey(name);
	}
	
	public FileWritable getAsWriteable() {
		FileWritable fw = template.createWritable();
		for(FileNodeReadable<?> node : this) {
			FileNodeWritable<?> fnw = fw.getFileNode(node.getName());
			if(fnw != null) {
				fnw.addAllUnchecked(node);
			}
		}
		return fw;
	}
	
}

package lb.simplebase.binfile;

import java.util.Iterator;
import java.util.List;

/**
 * 
 * A collection of elements of type T
 *
 * @param <T>
 */
public class FileNode<T> implements Iterable<T>{
	
	private List<T> data;
	private FileNodeTemplate<T> template;
	private String name;
	
	@SuppressWarnings("unchecked")
	protected FileNode(List<T> data, FileNodeTemplate<?> template) {
		this.name = template.getName();
		this.data = data;
		this.template = (FileNodeTemplate<T>) template;
	}
	
	public String getName() {
		return name;
	}
	
	public T getElement(int index) {
		return data.get(index);
	}

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
	
	public FileNodeTemplate<T> getTemplate() {
		return template;
	}
}

package lb.simplebase.binfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FileNodeWriteable<T> implements Iterable<T>{
	
	private List<T> elements;
	private FileNodeTemplate<T> template;
	private String name;
	
	@SuppressWarnings("unchecked")
	public FileNodeWriteable(FileNodeTemplate<?> template) {
		this.name = template.getName();
		this.elements = new ArrayList<>();
		this.template = (FileNodeTemplate<T>) template;
	}
	
	public String getName() {
		return name;
	}
	
	public void add(T element) {
		elements.add(element);
	}
	
	public void add(int index, T element) {
		elements.set(index, element);
	}
	
	public List<T> getElements() {
		return Collections.unmodifiableList(elements);
	}
	
	public void addAll(Iterable<T> elements) {
		for(T element : elements) this.elements.add(element);
	}
	
	@SuppressWarnings("unchecked")
	protected void addAllUnchecked(Iterable<?> elements) {
		addAll((Iterable<T>) elements);
	}
	
	public void clear() {
		elements.clear();
	}
	
	public FileNodeTemplate<T> getTemplate() {
		return template;
	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}
}

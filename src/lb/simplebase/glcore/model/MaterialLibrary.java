package lb.simplebase.glcore.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import lb.simplebase.glcore.model.ModelLoader.LoadPath;

public class MaterialLibrary implements Iterable<Material>{
	
	protected static final MaterialLibrary DISABLED = new MaterialLibrary() {

		@Override
		public Iterator<Material> iterator() {
			return new Iterator<Material>() {
				boolean taken = false;
				@Override
				public boolean hasNext() {
					return !taken;
				}

				@Override
				public Material next() {
					if(taken) throw new NoSuchElementException("No more materials");
					taken = true;
					return Material.DISABLED;
				}
			};
		}

		@Override
		public Material getMaterial(String name) {
			return Material.DISABLED;
		}

		@Override
		public boolean isDisabledLibrary() {
			return true;
		}
		
	};
	
	private final Set<Material> materials;
	
	private MaterialLibrary() {
		this.materials = new HashSet<>();
	}
	
	protected MaterialLibrary(LoadPath lookup, boolean load, String...names) throws IOException {
		this.materials = new HashSet<>();
		if(load) this.load(lookup, names);
	}
	
	@Override
	public Iterator<Material> iterator() {
		return materials.iterator();
	}
	
	public Material getMaterial(String name) {
		for(Material mat : materials) {
			if(mat.getName().equals(name)) return mat;
		}
		return null;
	}
	
	private void load(LoadPath lookup, String[] names) throws IOException {
		for(String name : names) {
			try(InputStream is = lookup.openResource(name)) {
				//TODO read material file
			}
		}
	}
	
	protected void addMaterial(Material m) {
		Objects.requireNonNull(m, "Material cannot be null");
		materials.add(m);
	}
	
	public boolean isDisabledLibrary() {
		return false;
	}
}

package test.simplebase.binfile;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.binfile.FileData;
import lb.simplebase.binfile.FileNode;
import lb.simplebase.binfile.FileNodeTemplate;
import lb.simplebase.binfile.FileNodeWriteable;
import lb.simplebase.binfile.FileTemplate;
import lb.simplebase.binfile.FileWriteable;
import lb.simplebase.net.ReadableByteData;
import lb.simplebase.net.WriteableByteData;

class FileTest {

	FileTemplate template;
	
	@BeforeEach
	void setUp() throws Exception {
		template = new FileTemplate();
		template.addNode(new VertexFileNode());
		template.addNode(new EntityFileNode());
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	void test() {
		FileWriteable fw = template.createWritable();
		FileNodeWriteable<Vertex> vfn = (FileNodeWriteable<Vertex>) fw.getFileNode(VertexFileNode.NAME);
		vfn.add(new Vertex(23, 5, -678));
		vfn.add(new Vertex(-3, 785, 12));
		FileNodeWriteable<Entity> efn = (FileNodeWriteable<Entity>) fw.getFileNode(EntityFileNode.NAME);
		efn.add(new Entity("test", 23));
		efn.add(new Entity("abcd", 44));
		byte[] fileData = fw.writeData();
		
		FileData fd = template.parseData(fileData);
		FileNode<Vertex> node = (FileNode<Vertex>) fd.getFileNode(VertexFileNode.NAME);
		Vertex v1 = node.getElement(0);
		assertEquals(23  , v1.x);
		assertEquals(5   , v1.y);
		assertEquals(-678, v1.z);
	}
	
	static class VertexFileNode extends FileNodeTemplate<Vertex> {
		public static final String NAME = "Vertex";
		public VertexFileNode() {
			super(Vertex.class, NAME);
		}
		@Override
		public Vertex parseElement(ReadableByteData data) {
			Vertex v = new Vertex();
			v.x = data.readInt();
			v.y = data.readInt();
			v.z = data.readInt();
			return v;
		}
		@Override
		public void writeElement(WriteableByteData data, Vertex element) {
			data.writeInt(element.x);
			data.writeInt(element.y);
			data.writeInt(element.z);
		}
	}

	static class EntityFileNode extends FileNodeTemplate<Entity> {
		public static final String NAME = "Entity";
		public EntityFileNode() {
			super(Entity.class, NAME);
		}
		@Override
		public Entity parseElement(ReadableByteData data) {
			Entity e = new Entity();
			e.name = data.readStringWithLength();
			e.health = data.readInt();
			return e;
		}
		@Override
		public void writeElement(WriteableByteData data, Entity element) {
			data.writeStringWithLength(element.name);
			data.writeInt(element.health);
		}
	}
	
	static class Vertex {
		int x, y, z;
		
		Vertex() {}
		Vertex(int x, int y, int z) {
			this.x = x;
			this.y= y;
			this.z = z;
		}
	}
	
	static class Entity {
		String name;
		int health;
		Entity() {}
		Entity(String name, int health) {
			this.name = name;
			this.health = health;
		}
	}
	
}

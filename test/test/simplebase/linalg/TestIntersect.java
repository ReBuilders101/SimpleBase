package test.simplebase.linalg;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lb.simplebase.linalg.Line2D;
import lb.simplebase.linalg.Vector2D;

class TestIntersect {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	public static void main(String[] args) throws Throwable { new TestIntersect().test(); }
	
	@Test
	void test() throws IOException, ClassNotFoundException {
		Line2D line1 = Line2D.of(Vector2D.ZERO, Vector2D.of(3, 1));
		Line2D line2 = Line2D.of(Vector2D.of(2, 2), Vector2D.of(1, -1));
		
		assertEquals(line1.getIntersectPoint(line2), Vector2D.of(3, 1));
		
		System.out.println(Vector2D.UNIT_Y.getPolarAngle() / Math.PI);
		System.out.println(Math.atan2(0, 0) / Math.PI);
//		fail("Not yet implemented");
		
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(baos);
		Vector2D vec1 = Vector2D.of(5, -3);
		System.out.println(vec1);
		objOut.writeObject(vec1);
		byte[] data = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream objIn = new ObjectInputStream(bais);
		Vector2D vec2 = (Vector2D) objIn.readObject();
		System.out.println(vec2);
	}

}

package test.simplebase.linalg;

import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	void test() {
		Line2D line1 = Line2D.of(Vector2D.ZERO, Vector2D.of(3, 1));
		Line2D line2 = Line2D.of(Vector2D.of(2, 2), Vector2D.of(1, -1));
		
		assertEquals(line1.getIntersectPoint(line2), Vector2D.of(3, 1));
		
		System.out.println(Vector2D.UNIT_Y.getPolarAngle() / Math.PI);
		System.out.println(Math.atan2(0, 0) / Math.PI);
//		fail("Not yet implemented");
	}

}

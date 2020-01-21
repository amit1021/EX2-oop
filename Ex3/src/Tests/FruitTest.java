package Tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import gameClient.Fruit;

import utils.Point3D;

class FruitTest {
	static Fruit f1, f2, f3, f4;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		// {"Fruit":{"value":5.0,"type":-1,"pos":"35.197656770719604,32.10191878639921,0.0"}}
		double f1Value, f2Value, f3Value, f4Value;
		f1Value = 1;
		f2Value = 2;
		f3Value = 3;
		f4Value = 4;
		int f1Type, f2Type, f3Type, f4Type;
		f1Type = 1;
		f2Type = -1;
		f3Type = 1;
		f4Type = -1;
		Point3D f1Loc = new Point3D("35.197656770719601,32.10191878639921,0.0");
		Point3D f2Loc = new Point3D("35.197656770719602,32.10191878639922,0.0");
		Point3D f3Loc = new Point3D("35.197656770719603,32.10191878639923,0.0");
		Point3D f4Loc = new Point3D("35.197656770719604,32.10191878639924,0.0");
		String f1Init = fruitToJSON(f1Value, f1Type, f1Loc);
		String f2Init = fruitToJSON(f2Value, f2Type, f2Loc);
		String f3Init = fruitToJSON(f3Value, f3Type, f3Loc);
		String f4Init = fruitToJSON(f4Value, f4Type, f4Loc);
		f1 = new Fruit(f1Init);
		f2 = new Fruit(f2Init);
		f3 = new Fruit(f3Init);
		f4 = new Fruit(f4Init);

	}

	static String fruitToJSON(double value, int type, Point3D location) {
		String s = "{\"Fruit\":{\"value\":" + value + "," + "\"type\":" + type + "," + "\"pos\":\""
				+ location.toString() + "\"" + "}" + "}";
		return s;
	}

	@Test
	void testGetType() {
		int[] expected = { 1, -1, 1, -1 };
		int[] realType = { f1.getType(), f2.getType(), f3.getType(), f4.getType() };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], realType[i]);
		}
	}

	@Test
	void testGetvValue() {
		double[] expected = { 1, 2, 3, 4 };
		double[] realValue = { f1.getvValue(), f2.getvValue(), f3.getvValue(), f4.getvValue() };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], realValue[i]);
		}
	}

	@Test
	void testGetLocation() {
		Point3D Point1Exp = new Point3D("35.1976567707196091,32.10191878639929,0.0");
		Point3D Point2Exp = new Point3D("35.19765677071969,32.10191878639928,0.0");
		Point3D Point3Exp = new Point3D("35.197656770719603,32.10191878639923,0.0");
		Point3D Point4Exp = new Point3D("35.197656770719604,32.10191878639924,0.0");

		Point3D[] expected = { Point1Exp, Point2Exp, Point3Exp, Point4Exp };
		Point3D[] realLoc = { f1.getLocation(), f2.getLocation(), f3.getLocation(), f4.getLocation() };
		for (int i = 0; i < expected.length / 2; i++) {
			assertNotEquals(expected[i].x(), realLoc[i].x());
			assertNotEquals(expected[i].y(), realLoc[i].y());
		}
		for (int i = 2; i < expected.length; i++) {
			assertEquals(expected[i].x(), realLoc[i].x());
			assertEquals(expected[i].y(), realLoc[i].y());
		}
	}
}

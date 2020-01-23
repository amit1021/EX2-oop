package Tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Point;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gameClient.Robot;
import utils.Point3D;

class RobotTest {
	static Robot r1, r2, r3, r4;
	// this.value = value;
	// this.src = src;
	// this.dest = dest;
	// this.speed = speed;
	// this.location = location;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		int r1Id, r2Id, r3Id, r4Id;
		double r1Value, r2Value, r3Value, r4Value;
		int r1Src, r2Src, r3Src, r4Src;
		int r1Dest, r2Dest, r3Dest, r4Dest;
		double r1Speed, r2Speed, r3Speed, r4Speed;
		Point3D r1Loc, r2Loc, r3Loc, r4Loc;

		r1Id = 1;
		r2Id = 2;
		r3Id = 3;
		r4Id = 4;

		r1Value = 9;
		r2Value = 17;
		r3Value = 4;
		r4Value = 5;

		r1Src = 12;
		r2Src = 4;
		r3Src = 5;
		r4Src = 7;

		r1Dest = 4;
		r2Dest = 5;
		r3Dest = 5;
		r4Dest = 12;

		r1Speed = 11.0;
		r2Speed = 9.3;
		r3Speed = 17.1;
		r4Speed = 6.5;

		r1Loc = new Point3D("35.197656770719601,32.10191878639921,0.0");
		r2Loc = new Point3D("35.197656770719602,32.10191878639922,0.0");
		r3Loc = new Point3D("35.197656770719603,32.10191878639923,0.0");
		r4Loc = new Point3D("35.197656770719604,32.10191878639924,0.0");

		String r1Init = robotToJSON(r1Id, r1Value, r1Src, r1Src, r1Speed, r1Loc);
		String r2Init = robotToJSON(r2Id, r2Value, r2Src, r2Src, r2Speed, r2Loc);
		String r3Init = robotToJSON(r3Id, r3Value, r3Src, r3Src, r3Speed, r3Loc);
		String r4Init = robotToJSON(r4Id, r4Value, r4Src, r4Src, r4Speed, r4Loc);

		r1 = new Robot(r1Init);
		r2 = new Robot(r2Init);
		r3 = new Robot(r3Init);
		r4 = new Robot(r4Init);

	}

	private static String robotToJSON(int id, double value, int src, int dest, double speed, Point3D location) {
		String s = "{\"Robot\":{\"id\":" + id + ",\"value\":" + value + ",\"src\":" + src + ",\"dest\":" + dest
				+ ",\"speed\":" + speed + ",\"pos\":\"" + location.toString() + "\"}}";
		return s;
	}

	@Test
	void testGetId() {
		int[] expected = { 1, 2, 3, 4 };
		int[] realId = { r1.getId(), r2.getId(), r3.getId(), r4.getId() };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], realId[i]);
		}
	}

	@Test
	void testGetValue() {
		double[] expected = { 9, 17, 4, 5 };
		double[] realValue = { r1.getValue(), r2.getValue(), r3.getValue(), r4.getValue() };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], realValue[i]);
		}
	}

	@Test
	void testGetSpeed() {
		double[] expected = { 11.0, 9.3, 17.1, 6.5 };
		double[] realSpeed = { r1.getSpeed(), r2.getSpeed(), r3.getSpeed(), r4.getSpeed() };
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], realSpeed[i]);
		}
	}

	@Test
	void testGetLocation() {
		Point3D Point1Exp = new Point3D("35.1976567707196091,32.10191878639929,0.0");
		Point3D Point2Exp = new Point3D("35.19765677071969,32.10191878639928,0.0");
		Point3D Point3Exp = new Point3D("35.197656770719603,32.10191878639923,0.0");
		Point3D Point4Exp = new Point3D("35.197656770719604,32.10191878639924,0.0");

		Point3D[] expected = { Point1Exp, Point2Exp, Point3Exp, Point4Exp };
		Point3D[] realLoc = { r1.getLocation(), r2.getLocation(), r3.getLocation(), r4.getLocation() };
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

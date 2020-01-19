package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gameClient.KML_Logger;
import utils.Point3D;

class KML_LoggerTest {
	private KML_Logger kml = new KML_Logger(1);
	

	@Test
	void testAddPlaceMarkRobot() {
		double longitude = 35.197974119451;
		double latitude = 32.1027645647058;
		Point3D p = new Point3D(longitude,latitude,0.0);
		for (int i = 1; i <= 20; i++) {
			kml.addPlaceMark("robot", p.toString());
		}
	}
	
	
	
	@Test
	void testAddPlaceMarkFruit() {
		double longitude = 35.197974119451;
		double latitude = 32.1027645647058;	
		Point3D p = new Point3D(longitude,latitude,0.0);
		for (int i = 1; i <= 30; i++) {
			kml.addPlaceMark("apple" ,p.toString());
		}
		for (int i = 1; i <= 30; i++) {
			kml.addPlaceMark("banana" ,p.toString());
		}
	}

}

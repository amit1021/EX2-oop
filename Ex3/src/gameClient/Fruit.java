package gameClient;

import java.util.Collection;

import sun.security.provider.certpath.Vertex;
import utils.Point3D;

public class Fruit {
	private int type;
	private double value;
	private Point3D location;

	public Fruit(int type, double value, Point3D location) {
		this.type = type;
		this.value = value;
		this.location = location;
	}

	public int getType() {
		return this.type;
	}

	public double getvValue() {
		return this.value;
	}

	public Point3D getLocation() {
		return this.location;
	}
}

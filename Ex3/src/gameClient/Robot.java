package gameClient;

import utils.Point3D;

public class Robot {
	private int id;
	private double value;
	private int src;
	private int dest;
	private double speed;
	private Point3D location;

	public Robot(int id, double value, int src, int dest, double speed, Point3D location) {
		this.id = id;
		this.value = value;
		this.src = src;
		this.dest = dest;
		this.speed = speed;
		this.location = location;
	}

	public int getId() {
		return this.id;
	}

	public double getValue() {
		return this.value;
	}

	public int getSrc() {
		return this.src;
	}

	public int getDest() {
		return this.dest;
	}

	public double getSpeed() {
		return this.speed;
	}

	public Point3D getLocation() {
		return this.location;
	}
}

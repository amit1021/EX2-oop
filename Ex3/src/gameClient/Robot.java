package gameClient;

import org.json.JSONObject;

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

	public Robot(Robot r) {
		this.id = r.id;
		this.value = r.value;
		this.src = r.src;
		this.dest = r.dest;
		this.speed = r.speed;
		this.location = r.location;
	}

	public Robot() {
		;
	}

	public Robot(String s) {
		this();
		try {
			JSONObject obj_JsonObject = new JSONObject(s);
			JSONObject JSON_Robot = obj_JsonObject.getJSONObject("Robot");
			int id = JSON_Robot.getInt("id");
			this.id = id;
			double value = JSON_Robot.getDouble("value"); // Extract the value of the robot
			this.value = value;
			int src = JSON_Robot.getInt("src"); // Extract the source of the robot
			this.src = src;
			int dest = JSON_Robot.getInt("dest"); // Extract the destination of the robot
			this.dest = dest;
			double speed = JSON_Robot.getDouble("speed"); // Extract the speed of the robot
			this.speed = speed;
			String pos = JSON_Robot.getString("pos");// Extract the coordinates to String
			this.location = new Point3D(pos);
			// Point3D p = getXYZ(pos); // get p coordinates from getXYZ function
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void setLocation(Point3D p) {
		this.location = p;
	}
}
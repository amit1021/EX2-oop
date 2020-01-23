package gameClient;

import org.json.JSONException;
import org.json.JSONObject;
import utils.Point3D;

public class Robot {
	private int id;
	private double value;
	private int src;
	private int dest;
	private double speed;
	private Point3D location;
	private Fruit fruitTarget;

	public Robot(int id, double value, int src, int dest, double speed, Point3D location) {
		this.id = id;
		this.value = value;
		this.src = src;
		this.dest = dest;
		this.speed = speed;
		this.location = location;
		this.fruitTarget = null;
	}

	public Robot(Robot r) {
		this.id = r.id;
		this.value = r.value;
		this.src = r.src;
		this.dest = r.dest;
		this.speed = r.speed;
		this.location = r.location;
		this.fruitTarget = r.fruitTarget;
	}

	public Robot() {
		;
	}

	public Robot(int id) {
		this.id = id;
	}

	public Robot(String s) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getInfoFromJson(JSONObject r) {
		try {
			setSrc(r.getInt("src"));
			setDest(r.getInt("dest"));
			setValue(r.getDouble("value"));
			setLocation(r.getString("pos"));
			setSpeed(r.getDouble("speed"));

		} catch (JSONException e) {
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

	public void setLocation(String p) {
		this.location = new Point3D(p);
	}

	public void setLocation(Point3D p) {
		this.location = p;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void setSrc(int src) {
		this.src = src;
	}

	public void setDest(int dest) {
		this.dest = dest;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public Fruit getFruitTarget() {
		return this.fruitTarget;
	}

	public void setFruitTarget(Fruit f) {
		this.fruitTarget = f;
	}

}
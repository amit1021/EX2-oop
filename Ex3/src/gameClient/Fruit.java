package gameClient;

import org.json.JSONObject;

import dataStructure.edge_data;
import utils.Point3D;

public class Fruit {
	private int type;
	private double value;
	private edge_data edge;
	private Point3D location;
	private boolean targeted;

	public Fruit(int type, double value, Point3D location) {
		this.type = type;
		this.value = value;
		this.location = location;
		this.edge = null;
		this.targeted = false;
	}

	public Fruit() {
		;
	}

	public Fruit(String s) {
		try {
			JSONObject obj_JSONObject = new JSONObject(s);
			JSONObject JSON_Fruit = obj_JSONObject.getJSONObject("Fruit");
			String pos = JSON_Fruit.getString("pos");// Extract the coordinates to String
			this.location = new Point3D(pos);
			// Point3D p = getXYZ(pos); // get p coordinates from getXYZ function
			double value = JSON_Fruit.getDouble("value"); // Extract the value of the fruit
			this.value = value;
			int type = JSON_Fruit.getInt("type"); // Extract the type of the fruit
			this.type = type;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update(String Json) {
		try {
			JSONObject obj_JSONObject = new JSONObject(Json);
			JSONObject JSON_Fruit = obj_JSONObject.getJSONObject("Fruit");
			String pos = JSON_Fruit.getString("pos");// Extract the coordinates to String
			this.location = new Point3D(pos);
			double value = JSON_Fruit.getDouble("value"); // Extract the value of the fruit
			this.value = value;

		} catch (Exception e) {
			e.printStackTrace();
		}

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

	public edge_data getEdge() {
		return this.edge;
	}

	public void setEdge(edge_data e) {
		this.edge = e;
	}

	public boolean getTarget() {
		return this.targeted;
	}

	public void setTarget(boolean b) {
		this.targeted = b;
	}

}
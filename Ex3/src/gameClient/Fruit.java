package gameClient;

import org.json.JSONObject;

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
	
	
}
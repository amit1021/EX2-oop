package gameClient;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI {
	private DGraph g;
	final int INFINITE = Integer.MAX_VALUE;
	final int MINUS_INFINITE = Integer.MIN_VALUE;
	final double EPSILON = 0.01;
	private ArrayList<Fruit> Fruits = new ArrayList<Fruit>();
	private ArrayList<Robot> Robots = new ArrayList<Robot>();

	public MyGameGUI(int scenario_num) {
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String gg = game.getGraph();
		g = new DGraph();
		g.initFromJSON(gg);

		try {
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject gameOver = line.getJSONObject("GameServer");
			int rs = gameOver.getInt("robots");
			// JSONObject robot = line.getJSONObject("Robot");
			System.out.println(info);
			System.out.println(g);
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) {
				initFruitFromJSON(f_iter.next());
			}
			int src_node = 0; // arbitrary node, you should start at one of the fruits
			for (int a = 0; a < rs; a++) {
				game.addRobot(src_node + a);
				String robot = game.getRobots().get(a);
				initRobotFromJSON(robot);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		DrawGraph();
//		game.startGame();
//		// should be a Thread!!!
//		while (game.isRunning()) {
//			moveRobots(game, gg);
//		}
//		String results = game.toString();
//		System.out.println("Game Over: " + results);
//	}
	}

	public void initFruitFromJSON(String s) {
		try {
			JSONObject obj_JSONObject = new JSONObject(s);
			JSONObject JSON_Fruit = obj_JSONObject.getJSONObject("Fruit");
			String pos = JSON_Fruit.getString("pos");// Extract the coordinates to String
			Point3D p = getXYZ(pos); // get p coordinates from getXYZ function
			double value = JSON_Fruit.getDouble("value"); // Extract the value of the fruit
			int type = JSON_Fruit.getInt("type"); // Extract the type of the fruit
			Fruit f = new Fruit(type, value, p); // Add new fruit
			Fruits.add(f); // Add the new fruit to the list

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initRobotFromJSON(String s) {
		try {
			JSONObject obj_JsonObject = new JSONObject(s);
			JSONObject JSON_Robot = obj_JsonObject.getJSONObject("Robot");
			int id = JSON_Robot.getInt("id");
			double value = JSON_Robot.getDouble("value"); // Extract the value of the robot
			int src = JSON_Robot.getInt("src"); // Extract the source of the robot
			int dest = JSON_Robot.getInt("dest"); // Extract the destination of the robot
			double speed = JSON_Robot.getDouble("speed"); // Extract the speed of the robot
			String pos = JSON_Robot.getString("pos");// Extract the coordinates to String
			Point3D p = getXYZ(pos); // get p coordinates from getXYZ function
			Robot r = new Robot(id, value, src, dest, speed, p);
			Robots.add(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// function to get coordinates
	public Point3D getXYZ(String pos) {
		double x = Double.parseDouble(pos.substring(0, pos.indexOf(","))); // get x coordinate
		pos = pos.substring(pos.indexOf(",") + 1);
		double y = Double.parseDouble(pos.substring(0, pos.indexOf(","))); // get y coordinate
		pos = pos.substring(pos.indexOf(",") + 1); // get z coordinate
		double z = Double.parseDouble(pos.substring(0));
		Point3D p = new Point3D(x, y, z);
		return p;
	}

	public void initGraph() {
		StdDraw.setCanvasSize(1000, 600);
		Range x = rangeX();
		Range y = rangeY();
		StdDraw.setXscale(x.get_min() - 5, x.get_max() + 5);
		StdDraw.setYscale(y.get_min() - 5, y.get_max() + 5);
	}

	public Range rangeX() {
		Collection<node_data> V = g.getV();
		double minX = INFINITE;
		double maxX = MINUS_INFINITE;
		for (node_data vertex : V) {
			if (vertex.getLocation().x() * 2000 > maxX) {
				maxX = vertex.getLocation().x() * 2000;
			}
			if (vertex.getLocation().x() * 2000 < minX) {
				minX = vertex.getLocation().x() * 2000;
			}
		}
		Range x = new Range(minX, maxX);
		return x;
	}

	public Range rangeY() {
		Collection<node_data> V = g.getV();
		double minY = INFINITE;
		double MaxY = MINUS_INFINITE;
		for (node_data vertex : V) {
			if (vertex.getLocation().y() * 2000 > MaxY) {
				MaxY = vertex.getLocation().y() * 2000;
			}
			if (vertex.getLocation().y() * 2000 < minY) {
				minY = vertex.getLocation().y() * 2000;
			}
		}
		Range y = new Range(minY, MaxY);
		return y;
	}

	public void DrawGraph() {
		initGraph();
		// edge
		for (node_data vertex : g.getV()) {
			Collection<edge_data> edge = g.getE(vertex.getKey());
			if (edge != null) {
				for (edge_data e : edge) {
					double x0 = g.getNode(e.getSrc()).getLocation().x() * 2000;
					double y0 = g.getNode(e.getSrc()).getLocation().y() * 2000;
					double x1 = g.getNode(e.getDest()).getLocation().x() * 2000;
					double y1 = g.getNode(e.getDest()).getLocation().y() * 2000;

					// edge
					StdDraw.setPenRadius(0.005);
					StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
					StdDraw.line(x0, y0, x1, y1);

					// Direction
					StdDraw.setPenRadius(0.015);
					StdDraw.setPenColor(StdDraw.YELLOW);
					double dirX = ((9 * x1) + x0) / 10;
					double dirY = ((9 * y1) + y0) / 10;
					StdDraw.point(dirX, dirY);

					// weigh
					double w = twoDigitsAfterP(e.getWeight());
					StdDraw.setPenColor(Color.cyan);
					double Wx = (x1 + x0) / 2;
					double Wy = (y1 + y0) / 2;
					String w1 = "" + w;
					StdDraw.text(Wx, Wy + 0.5, w1);

					// vertex
					StdDraw.setPenColor(Color.BLACK);
					StdDraw.point(x0, y0);
					String point = "";
					point += vertex.getKey();
					StdDraw.setPenRadius(0.02);
					StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
					StdDraw.text(x0, y0 + 0.5, point); // the number of the vertex.
				}
			}
		}
		DrawFruits();
	}

	// Display the number 2 digits after the decimal point
	public double twoDigitsAfterP(double e) {
		double e1 = e * 100;
		e1 = (int) e1;
		e1 = e1 / 100;
		return e1;
	}

	public void DrawFruits() {
		for (int i = 0; i < this.Fruits.size(); i++) {
			double x = this.Fruits.get(i).getLocation().x() * 2000;
			double y = this.Fruits.get(i).getLocation().y() * 2000;
			String fruit = matchFruitToEdge(Fruits.get(i)); // check which fruit is match
			StdDraw.picture(x, y, fruit, 1.3, 1.3); // draw the fruit on the graph
		}
	}

	public String matchFruitToEdge(Fruit f) {
		Collection<node_data> V = g.getV();
		for (node_data vertex : V) {
			Collection<edge_data> edge = g.getE(vertex.getKey());
			if (edge != null) {
				for (edge_data e : edge) {
					// check if the fruit on the edge and return the type of the fruit
					double srcX = vertex.getLocation().x();
					double srcY = vertex.getLocation().y();
					double FruitX = f.getLocation().x();
					double FruitY = f.getLocation().y();
					double destX = g.getNode(e.getDest()).getLocation().x();
					double destY = g.getNode(e.getDest()).getLocation().y();
					double disSrc = calculateDistanceBetweenPoints(srcX, srcY, FruitX, FruitY);
					double disDest = calculateDistanceBetweenPoints(destX, destY, FruitX, FruitY);
					double disSrcDest = calculateDistanceBetweenPoints(srcX, srcY, destX, destY);
					if (Math.abs(disSrcDest - (disSrc + disDest)) <= EPSILON) {
						if (srcY < destY) {
							return "apple.png";
						} else {
							return "banana.png";
						}
					}
				}
			}

		}
		return null;
	}

	// calculate distance between two points
	public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
		return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
	}

	public static void main(String[] args) {
		MyGameGUI myg1 = new MyGameGUI(1);
//		MyGameGUI myg2 = new MyGameGUI(2);
//		MyGameGUI myg3 = new MyGameGUI(3);
//		MyGameGUI myg4 = new MyGameGUI(4);
//		MyGameGUI myg5 = new MyGameGUI(5);
//		MyGameGUI myg6 = new MyGameGUI(6);
//		MyGameGUI myg7 = new MyGameGUI(7);
//		MyGameGUI myg8 = new MyGameGUI(8);
//		MyGameGUI myg9 = new MyGameGUI(9);
//		MyGameGUI myg10 = new MyGameGUI(10);
//		MyGameGUI myg11 = new MyGameGUI(11);
//		MyGameGUI myg12 = new MyGameGUI(12);
//		MyGameGUI myg13 = new MyGameGUI(13);
//		MyGameGUI myg14 = new MyGameGUI(14);
//		MyGameGUI myg15 = new MyGameGUI(15);
//		MyGameGUI myg16 = new MyGameGUI(16);
//		MyGameGUI myg17 = new MyGameGUI(17);
//		MyGameGUI myg18 = new MyGameGUI(18);

		// myg1.drawGraph();
//	myg2.drawGraph();
//		myg3.drawGraph();
		// myg4.drawGraph();
//		myg5.drawGraph();
//		myg6.drawGraph();
//		myg7.drawGraph();
		// myg8.drawGraph();
//		myg9.drawGraph();
//		myg10.drawGraph();
//		myg11.drawGraph();
		// myg12.drawGraph();
		// myg13.drawGraph();
//	myg14.drawGraph();
//		myg15.drawGraph();
		// myg16.drawGraph();
		// myg17.drawGraph();
		// myg18.drawGraph();

	}
}
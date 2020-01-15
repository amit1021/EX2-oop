package gameClient;

import java.awt.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI {

	final double EPSILON = 0.0001;
	final int INFINITE = Integer.MAX_VALUE;
	final int MINUS_INFINITE = Integer.MIN_VALUE;
	private game_service game;
	private InitiateGame gameGraph;

	public MyGameGUI(int scenario_num) {
		game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String graph = game.getGraph();
		gameGraph = new InitiateGame(graph);
		try {
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject gameOver = line.getJSONObject("GameServer");
			int rs = gameOver.getInt("robots");
			// JSONObject robot = line.getJSONObject("Robot");
			System.out.println(info);
			// System.out.println(g);
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) {
				gameGraph.initFruitFromJSON(f_iter.next());
			}
			int src_node = 0; // arbitrary node, you should start at one of the fruits
			for (int a = 0; a < rs; a++) {
				game.addRobot(src_node + a);
				String robot = game.getRobots().get(a);
				gameGraph.initRobotFromJSON(robot);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		DrawGraph();
//		game.startGame();
//		// should be a Thread!!!
		while (game.isRunning()) {
//			moveRobots(game, gg);
		}
//		String results = game.toString();
//		System.out.println("Game Over: " + results);
//	}
	}

//	public void placeRobot() {
//		edge_data e;
//		for (int i = 0; i < Robots.size(); i++) {
//			e = 
//		}
//		
//	}

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
		StdDraw.setCanvasSize(800, 400);
		Range x = rangeX();
		Range y = rangeY();
		StdDraw.setXscale(x.get_min() - 5, x.get_max() + 5);
		StdDraw.setYscale(y.get_min() - 5, y.get_max() + 5);
	}

	public void DrawGraph() {
		initGraph();

		// edge
		for (node_data vertex : gameGraph.graph().getV()) {
			Collection<edge_data> edge = gameGraph.graph().getE(vertex.getKey());
			if (edge != null) {
				for (edge_data e : edge) {
					double x0 = gameGraph.graph().getNode(e.getSrc()).getLocation().x() * 2000;
					double y0 = gameGraph.graph().getNode(e.getSrc()).getLocation().y() * 2000;
					double x1 = gameGraph.graph().getNode(e.getDest()).getLocation().x() * 2000;
					double y1 = gameGraph.graph().getNode(e.getDest()).getLocation().y() * 2000;

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
					double w = twoDigitsAfterPoint(e.getWeight());
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
		drawRobot();
		// drawTime();
		mouseClick();

	}

	public void mouseClick() {
		StdDraw.enableDoubleBuffering();
		while (true) {
//		 mouse click
			if (StdDraw.isMousePressed()) {
				double x = StdDraw.mouseX(); // the X coordinate of the click position
				double y = StdDraw.mouseY(); // the Y coordinate of the click position
				Point3D dest = new Point3D(x / 2000, y / 2000, 0); // initiate new point with this coordinates
				int robotId = getRobotId(dest); // checks whether there is a robot coming out to the clicked vertex
				if (robotId != -1) { // if robotId is not equals -1 then such a robot exists
					System.out.println("RES: " + robotId);

				}
			}
			StdDraw.show();
			StdDraw.pause(10);
		}
	}

	public int getRobotId(Point3D pressed) {
		Collection<node_data> V = gameGraph.graph().getV();
		for (node_data vertex : V) {
			int robotId = gameGraph.getRobotIdHelp(vertex);// Looking for the vertex that has a robot
			if (robotId != -1) { // if robotId is not equals -1 then such a robot exists on a vertex
				Collection<edge_data> edge = gameGraph.graph().getE(vertex.getKey());
				if (edge != null) {
					for (edge_data e : edge) {
						// checks whether there is a robot on a vertex that connects to the clicked
						// vertex
						double neibX = gameGraph.graph().getNode(e.getDest()).getLocation().x();
						double neibY = gameGraph.graph().getNode(e.getDest()).getLocation().y();
						if (Math.abs(neibX - pressed.x()) <= EPSILON && Math.abs(neibY - pressed.y()) <= EPSILON) {
//							while (true) {
//								moveRobot(robotId, e.getDest());
//								drawRobot();
//								// StdDraw.show();
//								try {
//									Thread.sleep(10);
//								} catch (Exception e1) {
//								}
							return robotId;
						}
					}
				}
			}
		}
		return -1;

	}

	public Range rangeX() {
		Collection<node_data> V = gameGraph.graph().getV();
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
		Collection<node_data> V = gameGraph.graph().getV();
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

//	public void moveRobot(int robotId, int dest) {
//
//		if (Robots.get(robotId).getLocation().x() < g.getNode(dest).getLocation().x()
//				&& Robots.get(robotId).getLocation().y() < g.getNode(dest).getLocation().y()) {
//			double x = Robots.get(robotId).getLocation().x();
//			double y = Robots.get(robotId).getLocation().y();
//			Point3D p = new Point3D(x + 0.001, y + 0.001, 0);
//			Robots.get(robotId).setLocation(p);
//		}
//
//	}

	public void drawTime() {
		// while (true) {
		// synchronized (this) {
		double x = 35 * 2000;
		double y = 32 * 2000;
		StdDraw.setPenRadius(0.015);
		StdDraw.setPenColor(Color.black);
		StdDraw.text(x, y, "Time: " + game.timeToEnd());
//			}
//		}

	}

	public ArrayList<Robot> cloneRobotsArray() {
		ArrayList<Robot> clone = new ArrayList<Robot>();
		for (int i = 0; i < gameGraph.Robots().size(); i++) {
			Robot r = new Robot(gameGraph.Robots().get(i));
			clone.add(r);
		}
		return clone;
	}

	// Display the number 2 digits after the decimal point
	public double twoDigitsAfterPoint(double e) {
		double e1 = e * 100;
		e1 = (int) e1;
		e1 = e1 / 100;
		return e1;
	}

	public void DrawFruits() {
		for (int i = 0; i < gameGraph.Fruits().size(); i++) {
			double x = gameGraph.Fruits().get(i).getLocation().x() * 2000;
			double y = gameGraph.Fruits().get(i).getLocation().y() * 2000;
			String fruit = gameGraph.typeOfFruit(gameGraph.Fruits().get(i)); // check which fruit is match
			StdDraw.picture(x, y, fruit, 1.3, 1.3); // draw the fruit on the graph
		}
	}

	public void drawRobot() {
		for (int i = 0; i < gameGraph.Robots().size(); i++) {
			double x = gameGraph.Robots().get(i).getLocation().x() * 2000;
			double y = gameGraph.Robots().get(i).getLocation().y() * 2000;
			StdDraw.setPenRadius(0.005);
			StdDraw.setPenColor(Color.DARK_GRAY);
			StdDraw.circle(x, y, 0.5);
		}
	}
	
	public void uptadeRobot() {
		
		
	}
	
	public void uptadeFruit() {
		
		
	}
	
	public void run() {
		while (game.isRunning()) {
			
			
		}
		
	}

	public static void main(String[] args) {

		// MyGameGUI myg1 = new MyGameGUI(1);
		MyGameGUI myg2 = new MyGameGUI(2);
		// MyGameGUI myg3 = new MyGameGUI(3);
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

	}
}

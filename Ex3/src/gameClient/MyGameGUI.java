package gameClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static java.lang.Thread.sleep;
import org.json.JSONException;
import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import oop_dataStructure.oop_graph;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI extends JFrame implements ActionListener, MouseListener {

	final double EPSILON = 0.0001;
	final int INFINITE = Integer.MAX_VALUE;
	final int MINUS_INFINITE = Integer.MIN_VALUE;
	private game_service game;
	private InitiateGame gameGraph;
	private AutomaticGame auto;
	private int numOfRobots = 0;
	private int robotsOnGraph = 0;

	public MyGameGUI() {
		optionGame();
	}

	public void startGameAuto(int scenario_num) {
		game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String graph = game.getGraph();
		gameGraph = new InitiateGame(graph);
		try {
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject gameOver = line.getJSONObject("GameServer");
			System.out.println(info);
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) { // init fruit
				gameGraph.initFruitFromJSON(f_iter.next());
			}
			numOfRobots = getNumOfRobots(gameOver);
			initRobot();

		} catch (Exception e) {
			e.printStackTrace();
		}

		placeRobot();
		initGraph();
		DrawGraph();
		DrawFruits();
		DrawRobot();
		game.startGame();
		runAuto();

	}

	public void startGameManual(int scenario_num) {
		game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String graph = game.getGraph();
		gameGraph = new InitiateGame(graph);
		try {
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject gameOver = line.getJSONObject("GameServer");
			System.out.println(info);
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) { // init fruit
				gameGraph.initFruitFromJSON(f_iter.next());
			}
			numOfRobots = getNumOfRobots(gameOver);
			initRobot();

		} catch (Exception e) {
			e.printStackTrace();
		}
		initGraph();
		DrawGraph();
		DrawFruits();
		StdDraw.show();
		int numberRobots = numOfRobots;
		while (numberRobots > 0) {
			String place = JOptionPane.showInputDialog("Please select a vertex that does not have a robot ");
			try {
				int ver = Integer.parseInt(place);
				if (!existsRobot(ver)) {
					placeRobotManual(ver);
					node_data n = gameGraph.graph().getNode(ver);
					if (n != null) {
						numberRobots--;
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Please enter an number and existing vertex");
			}
		}
		game.startGame();
		runManual();
	}

	public void initGraph() {
		StdDraw.setCanvasSize(1100, 600);
		Range x = rangeX();
		Range y = rangeY();
		StdDraw.setXscale(x.get_min() - 5, x.get_max() + 5);
		StdDraw.setYscale(y.get_min() - 5, y.get_max() + 5);
		DrawGraph();
		DrawFruits();
		// DrawRobot();
		StdDraw.show();
	}

	private void initRobot() {
		for (int a = 0; a < numOfRobots; a++) {
			robots().add(new Robot(a));
		}
	}

	private void optionGame() {
		int scenario_num = -1;
		String scenario_str = JOptionPane.showInputDialog("Please select a scenario from 0 to 23"); // window to select
																									// a scenario
		try {
			scenario_num = Integer.parseInt(scenario_str);

		} catch (Exception e) {
			throw new RuntimeException("the scenario need to be from 0 to 23");

		}
		String[] Game = { "Manually Game", "Automatic Game" };
		Object typeOfGame = JOptionPane.showInputDialog(null, "Please select a game type", "Game",
				JOptionPane.INFORMATION_MESSAGE, null, Game, Game[0]);
		if (typeOfGame == "Automatic Game") {
			StdDraw.clear();
			StdDraw.enableDoubleBuffering();
			startGameAuto(scenario_num);
		} else {
			StdDraw.clear();
			StdDraw.enableDoubleBuffering();
			startGameManual(scenario_num);
		}

	}

	// return the number of the robots in the game
	public int getNumOfRobots(JSONObject Json) {
		try {
			int numOfRobots = Json.getInt("robots");
			return numOfRobots;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// Position the robot in the optimal place
	public void placeRobot() {
		for (int i = 0; i < gameGraph.Robots().size(); i++) {
			edge_data e = maxFruitValue(); // the edge on which the fruit is found with the greatest value
			// initiate the location where the robot will starts
			Point3D robotStart = gameGraph.graph().getNode(e.getSrc()).getLocation();
			gameGraph.Robots().get(i).setSrc(e.getSrc());
			gameGraph.Robots().get(i).setDest(-1);
			gameGraph.Robots().get(i).setLocation(robotStart);
			this.game.addRobot(e.getSrc());
			System.out.println(game.getRobots());
		}
	}

	// Finding the fruit with the greatest value
	public edge_data maxFruitValue() {
		double maxValue = MINUS_INFINITE;
		edge_data e = new edgeData();
		for (int i = 0; i < gameGraph.Fruits().size(); i++) {
			// checks if the current value is greater than the maxValue
			if (gameGraph.Fruits().get(i).getvValue() > maxValue) {
				// the edge of the fruit with the greatest value
				edge_data e1 = gameGraph.matchFruitToEdge(gameGraph.Fruits().get(i));
				if (!existsRobot(e1.getSrc())) { // Check if a robot already exists there
					maxValue = gameGraph.Fruits().get(i).getvValue();
					Fruit fruit = gameGraph.Fruits().get(i);
					e = gameGraph.matchFruitToEdge(fruit);
				}
			}
		}
		return e;
	}

	// Checks if there is a robot on the vertex
	public boolean existsRobot(int src) {
		for (Robot r : robots()) {
			if (r.getSrc() == src) {
				return true;
			}
		}
		return false;
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
							return robotId;
						}
					}
				}
			}
		}
		return -1;

	}

	/** set scales **/
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

	public void updateRobots(List<String> r) {
		Robot robot;
		for (int i = 0; i < r.size(); i++) {
			String robot_json = r.get(i);
			try {
				JSONObject line = new JSONObject(robot_json);
				JSONObject ttt = line.getJSONObject("Robot");
				robot = new Robot(ttt.getInt("id"));
				robot = this.getRobot(ttt.getInt("id"));
				robot.getInfoFromJson(ttt);
				robot.setDest(-1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Robot getRobot(int id) {
		for (int i = 0; i < gameGraph.Robots().size(); i++) {
			if (gameGraph.Robots().get(i).getId() == id)
				return gameGraph.Robots().get(i);
		}
		return null;
	}

	public void updateFruits() {
		List<String> updateFruits = this.game.getFruits();
		if (updateFruits != null) {
			for (int i = 0; i < fruit().size(); i++) {
				fruit().get(i).update(updateFruits.get(i));
			}
		}
	}

	public int getNodeId(Point3D pressed) {
		Collection<node_data> V = gameGraph.graph().getV();
		for (node_data vertex : V) {
			double X = vertex.getLocation().x();
			double Y = vertex.getLocation().y();
			if (Math.abs(X - pressed.x()) <= EPSILON && Math.abs(Y - pressed.y()) <= EPSILON) {
				return vertex.getKey();
			}
		}
		return -1;
	}

	/** Automatic Game **/
//	private void moveRobot(game_service game, graph graph) {
//		List<String> log = game.move();
//		if (log != null) {
//			long t = game.timeToEnd();
//			for (int i = 0; i < log.size(); i++) {
//				String robot_json = log.get(i);
//				try {
//					JSONObject line = new JSONObject(robot_json);
//					JSONObject ttt = line.getJSONObject("Robot");
//					int rid = ttt.getInt("id");
//					int src = ttt.getInt("src");
//					int dest = ttt.getInt("dest");
//					Graph_Algo ga = new Graph_Algo();
//					ga.init(graph);
//					
//					if (dest == -1) { // the robot need a new fruit to eat
//						edge_data fruitEdge = cloestFruit(ga, src); 
//						getRobot(rid).setEdge(fruitEdge);
//						List<node_data> shortPathCur;
//						shortPathCur = ga.shortestPath(src, fruitEdge.getSrc()); // take the path between the robot and the fruit
//						getRobot(rid).setShortPath(shortPathCur); //update the robot short path list.			
//					
//						if (getRobot(rid).getShortPath().isEmpty()) {
//							if (src == fruitEdge.getSrc()) {//if the robot location is the same to the source of the fruit Edge 
//								game.chooseNextEdge(rid, fruitEdge.getDest());
//								System.out.println(
//										"Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
//								System.out.println("empty list one step");
//						}else {
//							game.chooseNextEdge(rid,shortPathCur.get(0).getKey());
//							System.out.println(
//									"Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
//							getRobot(rid).setDest(shortPathCur.get(0).getKey());
//							System.out.println("first steps");
//						}
//						
//						
//					}
//					}
//						if (!getRobot(rid).getShortPath().isEmpty()) { // if the list not empty the robot on the way to fruit.
//							edge_data fruitEdge =  getRobot(rid).getEdge();
//							if (src == fruitEdge.getSrc()) {//if the robot location is the same to the source of the fruit Edge 
//								game.chooseNextEdge(rid, fruitEdge.getDest()); 
//								System.out.println("Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
//								System.out.println("final stap");
//							} else {
//								System.out.println("not final ");
//								game.chooseNextEdge(rid, getRobot(rid).getShortPath().get(0).getKey());
//								getRobot(rid).setDest(getRobot(rid).getShortPath().get(0).getKey());
//								//System.out.println(shortPathCur.get(0).getKey());
//								getRobot(rid).getShortPath().remove(0);
////								if (src == fruitEdge.getDest() && getRobot(rid).getShortPath() != null) {
////									getRobot(rid).getShortPath().remove(0);
////								}
//								System.out.println("Turn to node: " + fruitEdge.getDest() + " time to end:" + (t / 1000));
//							}
//
//						}
//					}
////					}else {
////							//if (src == fruitEdge.getSrc()) {//if the robot location is the same to the source of the fruit Edge 
////								game.chooseNextEdge(rid, fruitEdge.getDest());
////								System.out.println(
////										"Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
////								System.out.println("empty list one step");
//////							} else {
//////								game.chooseNextEdge(rid, getRobot(rid).getShortPath().get(0).getKey());
//////								System.out.println("Turn to node: " + getRobot(rid).getShortPath().get(0).getKey() + "  time to end:" + (t / 1000));
//////								System.out.println("bbb");
//////								if (src == fruitEdge.getDest()) {
//////									getRobot(rid).setDest(-1);
//////								}
////////							getRobot(rid).setDest(getRobot(rid).getShortPath().get(0).getKey());
//////							if (src == fruitEdge.getDest()) {
//////								getRobot(rid).getShortPath().remove(0);
//////							}
////							}
//
//			 catch (JSONException e) {
//					e.printStackTrace();
//				}
//		}}
//	}

	private void moveRobot(game_service game, graph graph) {
		List<String> log = game.move();
		if (log != null) {
			long t = game.timeToEnd();
			for (int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					Graph_Algo ga = new Graph_Algo();
					ga.init(graph);
					if (dest == -1) {
						List<node_data> shortList = getRobot(rid).getShortPath();
						edge_data fruitEdge = cloestFruit(ga, src);
						if (src == fruitEdge.getSrc()) { // if the robot position and the source of the fruit edge are
															// the same
							game.chooseNextEdge(rid, fruitEdge.getDest());
							System.out.println("1");
							System.out.println("Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
						} else {
							if (!getRobot(rid).getShortPath().isEmpty()) { // the robot not get yet to the fruit
								dest = getRobot(rid).getShortPath().get(0).getKey();
								game.chooseNextEdge(rid, dest);
								System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
								System.out.println("2");
								getRobot(rid).getShortPath().remove(0);
							} else { // the robot reach to the fruit or the fruit edge dest equals to src of the
										// robot
								if (src == fruitEdge.getDest()) { // the robot need to get to his src
									game.chooseNextEdge(rid, fruitEdge.getSrc());
									// getRobot(rid).getShortPath().add(gameGraph().graph().getNode(fruitEdge.getDest()));
									System.out.println("3");
									System.out.println(
											"Turn to node: " + fruitEdge.getSrc() + "  time to end:" + (t / 1000));
								} else { // the robot need new fruit to get
									shortList = ga.shortestPath(src, fruitEdge.getDest());
									getRobot(rid).setShortPath(shortList);
									dest = getRobot(rid).getShortPath().get(0).getKey();
									game.chooseNextEdge(rid, dest);
									System.out.println("4");
									System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
									getRobot(rid).getShortPath().remove(0);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private edge_data cloestFruit(Graph_Algo ga, int robotSrc) {
		edge_data fruitEdge = new edgeData();
		double shortestPath = INFINITE;
		for (Fruit f : fruit()) {
			edge_data currFruitEdge = gameGraph().matchFruitToEdge(f);
			int fruitSrc = currFruitEdge.getSrc();
			double currentPath = ga.shortestPathDist(robotSrc, fruitSrc);
			if (currentPath < shortestPath) {
				shortestPath = currentPath;
				fruitEdge = currFruitEdge;
				doFalseToFruit(f.getLocation());
			}
		}
		return fruitEdge;
	}

	private void doFalseToFruit(Point3D p) {
		for (Fruit f : fruit()) {
			double currX = f.getLocation().x();
			double currY = f.getLocation().y();
			if (currX - p.x() == 0 && currY - p.y() == 0) {
				f.setExists(false);
			}
		}
	}

	public void sortFruits() {
		Comperator c = new Comperator();
		gameGraph.Fruits().sort(c);
	}

	public void runAuto() {
		while (this.game.isRunning()) {
			updateRobots(game.move());
			moveRobot(this.game, gameGraph.graph());
			updateFruits();
			this.DrawGraph();
			this.DrawRobot();
			this.DrawFruits();
			this.drawGradeTime();
			StdDraw.show();
			try {
				sleep(80);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Game over " + this.game.toString());
	}

	/** Draw function **/
	// Draw graph
	public void DrawGraph() {
		StdDraw.clear();
		StdDraw.enableDoubleBuffering();
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

					Font font = new Font("BN anna", Font.BOLD, 15);
					StdDraw.setFont(font);
					// weigh
					double w = twoDigitsAfterPoint(e.getWeight());
					StdDraw.setPenColor(Color.cyan);
					double Wx = (x0 + 2 * x1) / 3;
					double Wy = (y0 + 2 * y1) / 3;
					String w1 = "" + w;
					StdDraw.text(Wx, Wy + 0.5, w1);

					Font font1 = new Font("BN anna", Font.BOLD, 15);
					StdDraw.setFont(font1);
					// vertex
					StdDraw.setPenColor(Color.BLACK);
					StdDraw.point(x0, y0);
					String point = "";
					point += vertex.getKey();
					StdDraw.setPenRadius(0.02);
					StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
					StdDraw.text(x0, y0 + 0.5, point); // the number of the vertex.

					Font font2 = new Font("BN anna", Font.BOLD, 20);
					StdDraw.setFont(font2);
				}
			}
		}
	}

	// Draw fruits
	private void DrawFruits() {
		// StdDraw.clear();
		StdDraw.enableDoubleBuffering();
		for (Fruit fruit : fruit()) {
			// set the position of the friut
			double x = fruit.getLocation().x() * 2000;
			double y = fruit.getLocation().y() * 2000;
			// set the type of the friut
			String fruitS = gameGraph.typeOfFruit(fruit);
			// draw robot
			StdDraw.picture(x, y, fruitS, 1.3, 1.3);
		}
	}

	// Draw robots
	private void DrawRobot() {
		StdDraw.enableDoubleBuffering();
		for (int a = 0; a < game.getRobots().size(); a++) {
			// set the position of the robot
			double x = robots().get(a).getLocation().x() * 2000;
			double y = robots().get(a).getLocation().y() * 2000;
			// set pen
			StdDraw.setPenRadius(0.005);
			StdDraw.setPenColor(Color.DARK_GRAY);
			// draw robot
			StdDraw.circle(x, y, 0.5);

		}
	}

	// Display the number 2 digits after the decimal point
	public double twoDigitsAfterPoint(double e) {
		double e1 = e * 100;
		e1 = (int) e1;
		e1 = e1 / 100;
		return e1;
	}

	// Draw the grade and the time
	public void drawGradeTime() {
		int grade = 0;
		try {
			JSONObject gameServer = new JSONObject(game.toString()).getJSONObject("GameServer"); // the grade from the
																									// game
			grade = gameServer.getInt("grade");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int timeToEnd = (int) game.timeToEnd() / 1000; // time to end
		// set the position of the text
		double x = rangeX().get_min() + rangeX().get_length() / 2;
		double y = rangeY().get_max() + rangeY().get_length() / 10;
		// set pen
		Font font = new Font("BN anna", Font.BOLD, 20);
		StdDraw.setFont(font);
		StdDraw.setPenColor(Color.PINK);
		// draw text
		StdDraw.text(x, +y, "{ Grade: " + grade + ",  Time left :" + timeToEnd + "  }");
	}

	/** Get/Set function **/

	public InitiateGame gameGraph() {
		return gameGraph;
	}

	public game_service game() {
		return game;
	}

	public ArrayList<Robot> robots() {
		return gameGraph.Robots();
	}

	public ArrayList<Fruit> fruit() {
		return gameGraph.Fruits();
	}

	/** manual game */

	public int mouseClick() {
		double x = StdDraw.mouseX(); // the X coordinate of the click position
		double y = StdDraw.mouseY(); // the Y coordinate of the click position
		Point3D dest = new Point3D(x / 2000, y / 2000, 0); // initiate new point with this coordinates
		int robotId = getRobotId(dest); // checks whether there is a robot coming out to the clicked vertex
		if (robotId != -1) { // if robotId is not equals -1 then such a robot exists
			return getNodeId(dest);
		}
		return -1;
	}

	public void placeRobotManual(int node) {
		node_data n = gameGraph.graph().getNode(node);
		if (n != null) {
			// add all the robot to the game.
			game.addRobot(n.getKey());
			updateRobots(game.getRobots());
			robotsOnGraph++;
			DrawRobot();
			StdDraw.show();
		}
	}

	public node_data getVertexByLocation(double x, double y) {
		for (node_data ver : gameGraph.graph().getV()) {
			if (Math.abs(ver.getLocation().x() - x) <= EPSILON
					&& Math.abs(Math.abs(ver.getLocation().y() - y)) <= EPSILON) { // check if the location is the same
				return ver;
			}
		}
		return null;
	}

	private void moveRobotsManual(game_service game, graph gg) {
		List<String> log = game.move();
		if (log != null) {
			long t = game.timeToEnd();
			for (int i = 0; i < log.size(); i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");

					if (StdDraw.isMousePressed()) {
						int dest1 = mouseClick();
						if (dest1 != -1) {
							game.chooseNextEdge(rid, dest1);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void runManual() {
		while (this.game.isRunning()) {
			updateRobots(game.move());
			moveRobotsManual(this.game, gameGraph.graph());
			updateFruits();
			this.DrawGraph();
			this.DrawRobot();
			this.DrawFruits();
			this.drawGradeTime();
			StdDraw.show();
			try {
				sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Game over " + this.game.toString());
	}

	public static void main(String[] args) {
		MyGameGUI myg2 = new MyGameGUI();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		;
	}

	public void mouseClicked() {
		;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		;

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		;

	}

	@Override
	public void mouseExited(MouseEvent e) {
		;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		;

	}

}
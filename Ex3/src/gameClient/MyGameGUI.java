package gameClient;

import java.awt.Color;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI implements ActionListener, MouseListener, Runnable {

	final double EPSILON = 0.0001;
	final int INFINITE = Integer.MAX_VALUE;
	final int MINUS_INFINITE = Integer.MIN_VALUE;
	private game_service game;
	private InitiateGame gameGraph;
	private AutomaticGame auto;

	public MyGameGUI() {
		;
	}

	public void startGame(int scenario_num) {

		game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String graph = game.getGraph();
		gameGraph = new InitiateGame(graph);
		try {
			String info = game.toString();
			JSONObject line = new JSONObject(info);
			JSONObject gameOver = line.getJSONObject("GameServer");
			System.out.println(info);
			Iterator<String> f_iter = game.getFruits().iterator();
			while (f_iter.hasNext()) {
				gameGraph.initFruitFromJSON(f_iter.next());
			}
			initRobot(gameOver);

		} catch (Exception e) {
			e.printStackTrace();
		}
		placeRobot();
		initGraph();
		DrawGraph();
		DrawFruits();
		DrawRobot();
		game.startGame();
		run();
	}

	private void initRobot(JSONObject Json) {
		try {
			int rs = Json.getInt("robots");
			for (int a = 0; a < rs; a++) {
				robots().add(new Robot(a));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private void initGame() {
//		StdDraw.setCanvasSize(1100, 900);
//		StdDraw.setJMenuBar(MenueBar());
//		StdDraw.addMouseListener(this);
//		StdDraw.enableDoubleBuffering();
//	}

//	private JMenuBar MenueBar() {
//		JMenuBar menu = new JMenuBar();
//		JMenu game = new JMenu("Game");
//		menu.add(game);
//		JMenuItem scenario = new JMenuItem("Choose scenario");
//		scenario.addActionListener(this);
//		game.add(scenario);
//		return menu;
//	}

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
				// Point3D checkExistsRobot = getSrcLocationByEdge(e1);
				if (!existsRobot(e1.getSrc())) { // Check if a robot already exists there
					maxValue = gameGraph.Fruits().get(i).getvValue();
					Fruit fruit = gameGraph.Fruits().get(i);
					e = gameGraph.matchFruitToEdge(fruit);
				}
			}
		}
		return e;
	}

	public boolean existsRobot(int src) {
		for (Robot r : robots()) {
			if (r.getSrc() == src) {
				return true;
			}
		}
		return false;
	}

	// Returns the source of the edge to position the robot
	public Point3D getSrcLocationByEdge(edge_data e) {
		double x = gameGraph.graph().getNode(e.getSrc()).getLocation().x();
		double y = gameGraph.graph().getNode(e.getSrc()).getLocation().y();
		Point3D src = new Point3D(x, y);
		return src;
	}

//	// Checks whether a robot exists at the point
//	public boolean existsRobot(Point3D p) {
//		for (int i = 0; i < gameGraph.Robots().size(); i++) {
//			double currentRobotX = gameGraph.Robots().get(i).getLocation().x();
//			double currentRobotY = gameGraph.Robots().get(i).getLocation().y();
//			if (currentRobotX == p.x() && currentRobotY == p.y()) {
//				return true;
//			}
//		}
//		return false;
//	}

	public void initGraph() {
		StdDraw.setCanvasSize(1100, 900);
		// StdDraw.enableDoubleBuffering();
		Range x = rangeX();
		Range y = rangeY();
		// StdDraw.clear();
		StdDraw.setXscale(x.get_min() - 5, x.get_max() + 5);
		StdDraw.setYscale(y.get_min() - 5, y.get_max() + 5);
		DrawGraph();
		DrawFruits();
		DrawRobot();
		StdDraw.show();
	}

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

					// weigh
					double w = twoDigitsAfterPoint(e.getWeight());
					StdDraw.setPenColor(Color.cyan);
					double Wx = (x0 + 2 * x1) / 3;
					double Wy =  (y0 + 2 * y1) / 3 ;
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
		//StdDraw.show();

	}

	public int mouseClick() {
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
					return getNodeId(dest);

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

//	public void drawTime() {
//		// while (true) {
//		// synchronized (this) {
//		double x = 35 * 2000;
//		double y = 32 * 2000;
//		StdDraw.setPenRadius(0.015);
//		StdDraw.setPenColor(Color.black);
//		StdDraw.text(x, y, "Time: " + game.timeToEnd());
////			}
////		}
//
//	}

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

	/*
	 * public void DrawFruits() { for (int i = 0; i < gameGraph.Fruits().size();
	 * i++) { double x = gameGraph.Fruits().get(i).getLocation().x() * 2000; double
	 * y = gameGraph.Fruits().get(i).getLocation().y() * 2000; String fruit =
	 * gameGraph.typeOfFruit(gameGraph.Fruits().get(i)); // check which fruit is
	 * match StdDraw.picture(x, y, fruit, 1.3, 1.3); // draw the fruit on the graph
	 * } }
	 */

	private void DrawFruits() { // in scenario 17 there is apple on banana and we cannot see the banana!
		//StdDraw.clear();
		StdDraw.enableDoubleBuffering();
		for (Fruit fruit : fruit()) {
			double x = fruit.getLocation().x() * 2000;
			double y = fruit.getLocation().y() * 2000;
			String fruitS = gameGraph.typeOfFruit(fruit);
			StdDraw.point(x, y);
			StdDraw.picture(x, y, fruitS, 1.3, 1.3);
			//StdDraw.show();

		}
	}

	private void DrawRobot() {
		StdDraw.enableDoubleBuffering();
		for (int a = 0; a < game.getRobots().size(); a++) {
			double x = robots().get(a).getLocation().x() * 2000;
			double y = robots().get(a).getLocation().y() * 2000;
			StdDraw.setPenRadius(0.005);
			StdDraw.setPenColor(Color.DARK_GRAY);
			StdDraw.circle(x, y, 0.5);
			//StdDraw.show();

		}
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

	private int nextNode(DGraph g, int src) {
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		Iterator<edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = mouseClick();
		ans = r;
		return ans;
	}

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

	public int shortestPathDistBetweenFruits(Fruit fruit) {
		Graph_Algo ga = new Graph_Algo();
		ga.init(gameGraph.graph());
		Robot rob = new Robot();
		ArrayList<node_data> minPath = new ArrayList();
		edge_data e = gameGraph().matchFruitToEdge(fruit);
		double minPathDist = INFINITE;
		for (Robot robot : robots()) {
			double currPathDist = ga.shortestPathDist(robot.getSrc(), e.getSrc());
			double FruitEdgeWeight = e.getWeight();
			double ratio = (currPathDist + FruitEdgeWeight) / robot.getSpeed();
			if (ratio < minPathDist) {
				minPathDist = ratio;
				minPath = (ArrayList<node_data>) ga.shortestPath(robot.getSrc(), e.getSrc());
				rob = robot;
			}
		}
		if (rob.getSrc() == e.getSrc()) {
			return e.getDest();
//			this.game().chooseNextEdge(rob.getId(), e.getDest());
		} else {
			fruit.setExists(false);
//			this.game().chooseNextEdge(rob.getId(), minPath.get(0).getKey());
			return minPath.get(0).getKey();
			// rob.setDest(minPath.get(0).getKey());
			// game.addRobot(rob.getId());

		}
	}

//	private void moveRobot(game_service gameM, DGraph graph) {
//		List<String> log = gameM.move();
//		if (log != null) {
//			updateFruits();
//			updateRobots(log);
//			sortFruits();
//			for (Fruit fruit : fruit()) {
//				if (fruit.getExists()) {
//					shortestPathDistBetweenFruits(fruit);
//				}
//			}
//		}
//	}
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
					edge_data fruitEdge = cloestFruit(ga, src);
					if (dest == -1) {
						List<node_data> shortPathCur;
						shortPathCur = ga.shortestPath(src, fruitEdge.getSrc());
						getRobot(rid).setShortPath(shortPathCur);
						if (!getRobot(rid).getShortPath().isEmpty()) {
							if (src == fruitEdge.getSrc()) {
								game.chooseNextEdge(rid, fruitEdge.getDest());
								System.out.println(
										"Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
								if (src == fruitEdge.getDest()) {
									getRobot(rid).getShortPath().remove(0);
								}
								System.out.println("asaadadf");
							} else {
								System.out.println("qqqq");
								game.chooseNextEdge(rid, getRobot(rid).getShortPath().get(0).getKey());
								getRobot(rid).setDest(getRobot(rid).getShortPath().get(0).getKey());
//								if (src == fruitEdge.getDest() && getRobot(rid).getShortPath() != null) {
//									getRobot(rid).getShortPath().remove(0);
//								}
								System.out
										.println("Turn to node: " + fruitEdge.getDest() + " time to end:" + (t / 1000));
							}

						} else {
							if (src == fruitEdge.getSrc()) {
								game.chooseNextEdge(rid, fruitEdge.getDest());
								System.out.println("Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
								System.out.println("aaa");
							} else {
								game.chooseNextEdge(rid, fruitEdge.getDest());
								System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
								System.out.println("bbb");
								if (src == fruitEdge.getDest()) {
									getRobot(rid).setDest(-1);
								}
//							getRobot(rid).setDest(getRobot(rid).getShortPath().get(0).getKey());
//							if (src == fruitEdge.getDest()) {
//								getRobot(rid).getShortPath().remove(0);
//							}
							}

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sortFruits() {
		Comperator c = new Comperator();
		gameGraph.Fruits().sort(c);
	}

	
	public void run() {
		while (this.game.isRunning()) {
			updateFruits();
			updateRobots(game.move());
			moveRobot(this.game, gameGraph.graph());
			this.DrawGraph();
			this.DrawRobot();
			
			this.DrawFruits();
		
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

		// MyGameGUI myg1 = new MyGameGUI(1);
		MyGameGUI myg2 = new MyGameGUI();
		myg2.startGame(1);
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
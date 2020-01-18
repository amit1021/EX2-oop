package gameClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import static java.lang.Thread.sleep;
import org.json.JSONException;
import org.json.JSONObject;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI implements MouseListener {

	private game_service game;
	private InitiateGame gameGraph;
	private int numOfRobots = 0;
	final static int INFINITE = Integer.MAX_VALUE;
	final static int MINUS_INFINITE = Integer.MIN_VALUE;
	final static double EPSILON = 0.0001;

	public MyGameGUI() {
		optionGame();
	}

	public void startGame(int scenario_num, int typeOfGame) {

		game = Game_Server.getServer(scenario_num); // you have [0,23] games

		// extract the game data (edges, nodes) as a string
		String graph = game.getGraph();

		// convert game data s
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
			numOfRobots = Utils.getNumOfRobots(gameOver);
			gameGraph.initRobot(robot(), numOfRobots);
		} catch (Exception e) {
			e.printStackTrace();
		}
		gameGraph.placeRobot(gameGraph.Robots(), gameGraph.Fruits(), game);
		initGraph();
		DrawGraph();
		DrawFruits();
		DrawRobot();
		game.startGame();
		if (typeOfGame == 1) {
			runAuto();

		} else {
			runManual();
		}
	}

	public void initGraph() {
		StdDraw.setCanvasSize(1100, 600);
		Range x = Utils.rangeX(gameGraph.graph().getV());
		Range y = Utils.rangeY(gameGraph.graph().getV());
		StdDraw.setXscale(x.get_min() - 5, x.get_max() + 5);
		StdDraw.setYscale(y.get_min() - 5, y.get_max() + 5);
		DrawGraph();
		DrawFruits();
		DrawRobot();
		StdDraw.show();
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
			startGame(scenario_num, 1);
		} else {
			StdDraw.clear();
			StdDraw.enableDoubleBuffering();
			startGame(scenario_num, 0);
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
				robot = Utils.getRobot(robot(), ttt.getInt("id"));
				robot.getInfoFromJson(ttt);
				robot.setDest(-1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateFruits() {
		List<String> updateFruits = this.game.getFruits();
		if (updateFruits != null) {
			for (int i = 0; i < fruit().size(); i++) {
				fruit().get(i).update(updateFruits.get(i));
			}
		}
	}

	/* Automatic Game */
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
					edge_data fruitEdge = Utils.closestFruit(gameGraph.Fruits(), gameGraph.graph(), ga, src);
					if (dest == -1) {
						List<node_data> shortPathCur;
						shortPathCur = ga.shortestPath(src, fruitEdge.getSrc());
						Utils.getRobot(robot(), rid).setShortPath(shortPathCur);
						if (!Utils.getRobot(robot(), rid).getShortPath().isEmpty()) {
							if (src == fruitEdge.getSrc()) {
								game.chooseNextEdge(rid, fruitEdge.getDest());
								System.out.println(
										"Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
								if (src == fruitEdge.getDest()) {
									Utils.getRobot(robot(), rid).getShortPath().remove(0);
								}
								System.out.println("asaadadf");
							} else {
								System.out.println("qqqq");
								game.chooseNextEdge(rid, Utils.getRobot(robot(), rid).getShortPath().get(0).getKey());
								Utils.getRobot(robot(), rid)
										.setDest(Utils.getRobot(robot(), rid).getShortPath().get(0).getKey());
//								if (src == fruitEdge.getDest() && getRobot(rid).getShortPath() != null) {
//									getRobot(rid).getShortPath().remove(0);
//								}
								System.out
										.println("Turn to node: " + fruitEdge.getDest() + " time to end:" + (t / 1000));
							}

						} else {
							if (src == fruitEdge.getSrc()) {
								game.chooseNextEdge(rid, fruitEdge.getDest());
								System.out.println(
										"Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
								System.out.println("aaa");
							} else {
								game.chooseNextEdge(rid, fruitEdge.getDest());
								System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
								System.out.println("bbb");
								if (src == fruitEdge.getDest()) {
									Utils.getRobot(robot(), rid).setDest(-1);
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

	/* Draw function */
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

					// weigh
					double w = Utils.twoDigitsAfterPoint(e.getWeight());
					StdDraw.setPenColor(Color.cyan);
					double Wx = (x0 + 2 * x1) / 3;
					double Wy = (y0 + 2 * y1) / 3;
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
			double x = robot().get(a).getLocation().x() * 2000;
			double y = robot().get(a).getLocation().y() * 2000;
			// set pen
			StdDraw.setPenRadius(0.005);
			StdDraw.setPenColor(Color.DARK_GRAY);
			// draw robot
			StdDraw.circle(x, y, 0.5);

		}
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
		double x = Utils.rangeX(gameGraph.graph().getV()).get_min()
				+ Utils.rangeX(gameGraph.graph().getV()).get_length() / 2;
		double y = Utils.rangeY(gameGraph.graph().getV()).get_max();
//				+ Utils.rangeX(gameGraph.graph().getV()).get_length() / 10;
		// set pen
		Font font = new Font("BN anna", Font.BOLD, 20);
		StdDraw.setFont(font);
		StdDraw.setPenColor(Color.PINK);
		// draw text
		StdDraw.text(x, +y, "{ Grade: " + grade + ",  Time left :" + timeToEnd + "  }");
	}

	/* Get/Set function */

	public ArrayList<Robot> robot() {
		return gameGraph.Robots();
	}

	public ArrayList<Fruit> fruit() {
		return gameGraph.Fruits();
	}

	public void runAuto() {
		while (this.game.isRunning()) {

			updateRobots(game.move());
			moveRobot(this.game, gameGraph.graph());
			// mouseClicked();
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

	@Override
	public void mouseClicked(MouseEvent e) {
		;
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
							System.out.println("232");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int mouseClick() {
		// the coordinates of the click position
		double x = StdDraw.mouseX();
		double y = StdDraw.mouseY();
		// initiate new point with this coordinates
		Point3D dest = new Point3D(x / 2000, y / 2000, 0);
		// checks whether there is a robot coming out to the clicked vertex
		int robotId = Utils.getRobotId(gameGraph.graph(), robot(), dest);
		// if robotId is not equals -1 then such a robot exists
		if (robotId != -1) {
			return Utils.getNodeId(gameGraph.graph().getV(), dest);
		}
		return -1;
	}

	@Override
	public void mousePressed(MouseEvent e) {

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

	public static void main(String[] args) {

		// MyGameGUI myg1 = new MyGameGUI(1);
		MyGameGUI myg2 = new MyGameGUI();
		// myg2.startGame(20, 9);
		// myg2.Jmenu();
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
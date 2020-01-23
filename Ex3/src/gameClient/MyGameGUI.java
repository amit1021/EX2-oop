<<<<<<< HEAD
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
	private int robotsOnGraph = 0;
	final static int INFINITE = Integer.MAX_VALUE;
	final static int MINUS_INFINITE = Integer.MIN_VALUE;
	final static double EPSILON = 0.0001;
	public static KML_Logger kml = null;
	private static DataBase DB;

	public MyGameGUI() {
		optionGame();
	}

	public void startGameAuto(int scenario_num, int ID) {
		Game_Server.login(ID);
		DB = new DataBase();
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
			numOfRobots = Utils.getNumOfRobots(gameOver);
			gameGraph.initRobot(robot(), numOfRobots);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (scenario_num == 16) {
			gameGraph.placeRobotLevel16(robot(), fruit(), game);
		
		}else {
			gameGraph.placeRobot(robot(), fruit(), game);
		}
		initGraph();
		DrawGraph();
		DrawFruits();
		game.startGame();
		runAuto(scenario_num, ID);

	}

//Game Manual 
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
			numOfRobots = Utils.getNumOfRobots(gameOver);
			gameGraph.initRobot(robot(), numOfRobots);

		} catch (Exception e) {
			e.printStackTrace();
		}
		initGraph();
		DrawGraph();
		DrawFruits();
		StdDraw.show();
		int numberRobots = numOfRobots;
		// Position the robots at the player's request
		while (numberRobots > 0) {
			String place = JOptionPane.showInputDialog("Please select a vertex that does not have a robot ");
			if (place == null) {
				System.exit(0);
			}
			try {
				int ver = Integer.parseInt(place);
				if (!Utils.existsRobot(robot(), ver)) {
					placeRobotManual(ver);
					node_data n = gameGraph.graph().getNode(ver);
					if (n != null) {
						numberRobots--;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		game.startGame();
		runManual();
	}

	public void initGraph() {
		StdDraw.setCanvasSize(1100, 600);
		Range x = Utils.rangeX(gameGraph.graph().getV());
		Range y = Utils.rangeY(gameGraph.graph().getV());
		// set scales
		StdDraw.setXscale(x.get_min() - 5, x.get_max() + 5);
		StdDraw.setYscale(y.get_min() - 5, y.get_max() + 5);
		DrawGraph();
		DrawFruits();
		DrawRobot();
		StdDraw.show();
	}

	// Choose which game to play.
	private void optionGame() {
		int scenario_num = -1;
		int ID = 0;
		String ID_string = JOptionPane.showInputDialog("Please enter you'r ID");
		String scenario_str = JOptionPane.showInputDialog("Please select a scenario from 0 to 23"); // window to select
																									// a scenario
		if (scenario_str == null) {
			System.exit(0);
		}
		try {
			ID = Integer.parseInt(ID_string);
			scenario_num = Integer.parseInt(scenario_str);

		} catch (Exception e) {
			throw new RuntimeException("the scenario need to be from 0 to 23");

		}
		String[] Game = { "Manually Game", "Automatic Game" };
		Object typeOfGame = JOptionPane.showInputDialog(null, "Please select a game type", "Game",
				JOptionPane.INFORMATION_MESSAGE, null, Game, Game[0]);
		if (typeOfGame == null) {
			System.exit(0);
		}
		// Automatic game
		if (typeOfGame == "Automatic Game") {
			StdDraw.clear();
			StdDraw.enableDoubleBuffering();
			kml = new KML_Logger(scenario_num);

			startGameAuto(scenario_num, ID);
			// Game manual
		} else {
			StdDraw.clear();
			StdDraw.enableDoubleBuffering();
			kml = new KML_Logger(scenario_num);
			startGameManual(scenario_num);
		}

	}

	// update the robots while the game is running
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
				if (MyGameGUI.kml != null) {
					// add to the KML
					MyGameGUI.kml.addPlaceMark("robot", Utils.getRobot(robot(), i).getLocation().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// update the fruit while the game is running
	public void updateFruits() {
		fruit().clear();
		List<String> updateFruits = this.game.getFruits();
		if (updateFruits != null) {
			for (int i = 0; i < updateFruits.size(); i++) {
				fruit().add(new Fruit(updateFruits.get(i)));
				Utils.matchFruitToEdge(gameGraph.graph(), fruit().get(i));
				if (MyGameGUI.kml != null) {
					// add to the KML
					if (fruit().get(i).getType() == 1) {
						// the type is apple
						MyGameGUI.kml.addPlaceMark("apple", fruit().get(i).getLocation().toString());
					} else {
						// the type is banana
						MyGameGUI.kml.addPlaceMark("banana", fruit().get(i).getLocation().toString());
					}
				}
			}
		}
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

					// weight
					double w = Utils.twoDigitsAfterPoint(e.getWeight());
					StdDraw.setPenColor(Color.cyan);
					double Wx = (x0 + 2 * x1) / 3;
					double Wy = (y0 + 2 * y1) / 3;
					String w1 = "" + w;
					Font font = new Font("BN anna", Font.BOLD, 12);
					StdDraw.setFont(font);
					StdDraw.text(Wx, Wy + 0.5, w1);

					// vertex
					StdDraw.setPenColor(Color.BLACK);
					StdDraw.point(x0, y0);
					String point = "";
					point += vertex.getKey();
					StdDraw.setPenRadius(0.02);
					StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
					Font font1 = new Font("BN anna", Font.BOLD, 15);
					StdDraw.setFont(font1);
					StdDraw.text(x0, y0 + 0.5, point); // the number of the vertex.
				}
			}
		}
	}

	// Draw fruits
	private void DrawFruits() {
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
		int moves = 0;
		int level = 0;
		try {
			JSONObject gameServer = new JSONObject(game.toString()).getJSONObject("GameServer"); // the grade from the
			moves = gameServer.getInt("moves"); // game
			grade = gameServer.getInt("grade");
			level = gameServer.getInt("game_level");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int timeToEnd = (int) game.timeToEnd() / 1000; // time to end
		// set the position of the text
		double x = Utils.rangeX(gameGraph.graph().getV()).get_min()
				+ Utils.rangeX(gameGraph.graph().getV()).get_length() / 2;
		double y = Utils.rangeY(gameGraph.graph().getV()).get_max()
				+ Utils.rangeX(gameGraph.graph().getV()).get_length() / 20;
		// set pen
		Font font = new Font("BN anna", Font.BOLD, 20);
		StdDraw.setFont(font);
		StdDraw.setPenColor(Color.PINK);
		// draw text
		StdDraw.text(x, +y, "{ Level : " + level + ", Grade: " + grade + ",  Moves: " + moves + ",  Time left :"
				+ timeToEnd + "  }");
	}

	/** Get/Set function **/

	public ArrayList<Robot> robot() {
		return gameGraph.Robots();
	}

	public ArrayList<Fruit> fruit() {
		return gameGraph.Fruits();
	}

	public KML_Logger getKML() {
		return this.kml;
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
					double speed = ttt.getDouble("speed");
					String p_string = ttt.getString("pos");
					Point3D p = new Point3D(p_string);
					Graph_Algo ga = new Graph_Algo();
					ga.init(graph);
					if (dest == -1) {// the fruit don't have dest.
						Fruit f = Utils.closestFruit(fruit(), gameGraph.graph(), ga, src);
						Robot r = Utils.getRobot(robot(), rid);
						if (f != r.getFruitTarget() && !f.getTarget()) { //check that the robot don't go to fruit that robot else go.
							if (r.getFruitTarget() != null) {
								r.getFruitTarget().setTarget(false);
							}
							f.setTarget(true);
							r.setFruitTarget(f);
						}
						edge_data fruitEdge = r.getFruitTarget().getEdge();
						List<node_data> shortList = ga.shortestPath(src, fruitEdge.getSrc());
						if (src == fruitEdge.getSrc()) { // if the src of the robot and the src of the fruit edge are
															// the same - move to fruit edge dest.
							Utils.getRobot(robot(), rid).setDest(fruitEdge.getDest());
							game.chooseNextEdge(rid, fruitEdge.getDest());
							System.out.println("Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
						}
						else if (!shortList.isEmpty()) { // The robot has not yet reached fruit
							dest = shortList.get(0).getKey();
							Utils.getRobot(robot(), rid).setDest(dest);
							game.chooseNextEdge(rid, dest);
							System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
						}
					}
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	// set thread with Boaz function.
	public void setThread(Fruit f, double speed) {
		edge_data fruitEdge = f.getEdge();
		node_data dest = gameGraph.graph().getNode(fruitEdge.getDest());
		node_data src = gameGraph.graph().getNode(fruitEdge.getSrc());
		double currPath1 = src.getLocation().distance2D(dest.getLocation());
		double currPath2 = src.getLocation().distance2D(f.getLocation());
		long dt = (long) ((((currPath2 / currPath1) * fruitEdge.getWeight()) / speed) * 1000); //the log of the sleep
		try {
			sleep(dt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// Check if the edge between the src and the dest are exists.
	public boolean existsEdge(int src, int dest) {
		Collection<edge_data> edge = gameGraph.graph().getE(src);
		for (edge_data e : edge) {
			if (dest == e.getDest()) {
				return true;
			}
		}
		return false;

	}
	//set the thread sleep for each scenario
	public void checkSleep(int senario) {
		long dt = 50;
		if (senario == 19 || senario == 20 || senario == 16) {
			if (senario == 19) {
				dt=81;
			}
			if (senario == 20) {
				dt=85;
			}
			if (senario == 16) {
				dt=82;
			}
			for (Fruit f : fruit()) {
				for (Robot r : robot()) {
					if (r.getSrc() == f.getEdge().getSrc() && r.getDest() == f.getEdge().getDest()) {
						setThread(f, r.getSpeed());
						return;
					}
				}
			}
		}
		if (senario == 23) {
			dt=45;
		for (Fruit f : fruit()) {
			for (Robot r : robot()) {
				if (r.getSrc() == f.getEdge().getSrc() && r.getDest() == f.getEdge().getDest()) {
					try {
						sleep(16);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		}
		if (senario == 13) {
			dt = 82;
		}
		if (senario == 5) {
			dt = 110;
		}
		if (senario == 9 || senario == 11) {
			dt = 94;
		}
		
		if (senario == 0 || senario == 1 || senario == 3 ) {
			dt = 99;
		}
		try {
			sleep(dt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void runAuto(int senario, int ID) {
		while (this.game.isRunning()) {
			updateRobots(game.getRobots());
			updateFruits();
			moveRobot(this.game, gameGraph.graph());
			this.DrawGraph();
			this.DrawRobot();
			this.DrawFruits();
			this.drawGradeTime();
			StdDraw.show();
			checkSleep(senario);
		}

		String res = game.toString();
		kml.end(); //close the KML
		String remark = kml.getString();
		game.sendKML(remark); // send KML
		KML();
		System.out.println(res);
		gameOver(); // show data about the game
		DB = new DataBase();
		displayScore(senario, ID); // show the table score
	}

	/** Manual game **/
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
	// move the robot according to the mouse press
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
					int dest = ttt.getInt("dest");
					if (dest == -1) {
						if (StdDraw.isMousePressed()) {
							int dest1 = mouseClick(); // take the coordinates
							game.chooseNextEdge(rid, dest1);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
//Position the robot at the user's request
	public void placeRobotManual(int node) {
		node_data n = gameGraph.graph().getNode(node);
		if (n != null) {
			// add all the robot to the game.
			game.addRobot(n.getKey());// add robot to the game
			updateRobots(game.getRobots()); // update robot
			robotsOnGraph++;
			DrawRobot();
			StdDraw.show();
			System.out.println(robotsOnGraph);
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
		KML();
		gameOver();
	}

	private void KML() {
		try {
			int ans = JOptionPane.showConfirmDialog(null, "Do you want a KML log", "KML logger",
					JOptionPane.YES_NO_OPTION);
			System.out.println(ans);
			if (ans == 0) {
				kml.end();
			}

		} catch (Exception e) {

		}
	}
//print the data about the game after the game is over
	private void gameOver() {
		try {
			JSONObject gameServer = new JSONObject(game.toString()).getJSONObject("GameServer"); // the grade from game
			int grade = gameServer.getInt("grade");
			int move = gameServer.getInt("moves");
			JOptionPane.showMessageDialog(null, "          Game Over \n Grade: " + grade + "  Moves: " + move);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//display the table score of all the users
	public void displayScore(int senario_num, int ID) {
		DB.scoreTable(senario_num);
		String[] res = DB.getAllScoresPerLevel();
		int grade = 0;
		int move = 0;
		String yourScour = "";
		int place = DB.getMyPlace(senario_num); //the place according to the best score
		int bestScore = DB.bestScore(senario_num); // the best score
		int[] moves = DB.getMoves();
		try {
			JSONObject gameServer = new JSONObject(game.toString()).getJSONObject("GameServer"); // the grade from game
			grade = gameServer.getInt("grade"); // take the grade 
			move = gameServer.getInt("moves");//take the moves
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (move <= moves[senario_num]) {
			yourScour = "   You'r best score: \n"+ "  " +place + "     " + ID + "   " + bestScore+ " \n" +"          Your score: \n" +  "              " + grade ;  
		} else {
			yourScour = "   You'r best score: \n"+ place + "     " + ID + "   " + bestScore+ " \n" + "          Your score: \n" + "   Too many steps Please try again" ;
		}

		String massage = "  Place:    ID:        score: \n   " + "  1" + res[0] + "\n" + "    2" + res[1] + "\n"
				+ "    3" + res[2] + "\n" + "    4" + res[3] + "\n" + "    5" + res[4] + "\n" + "    6" + res[5] + "\n"
				+ "    7" + res[6] + "\n" + "    8" + res[7] + "\n" + "    9" + res[8] + "\n" + "    10" + res[9] + "\n"
				+ yourScour;
		;

		JOptionPane.showMessageDialog(null, massage, "Score", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		;
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public static void main(String[] args) {
		MyGameGUI mgg = new MyGameGUI();
	}

=======
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
	private int robotsOnGraph = 0;
	final static int INFINITE = Integer.MAX_VALUE;
	final static int MINUS_INFINITE = Integer.MIN_VALUE;
	final static double EPSILON = 0.0001;
	public static KML_Logger kml = null;

	public MyGameGUI() {
		optionGame();
	}

	public void startGameAuto(int scenario_num) {
		int id = 293440512;
		Game_Server.login(id);
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
			numOfRobots = Utils.getNumOfRobots(gameOver);
			gameGraph.initRobot(robot(), numOfRobots);
		} catch (Exception e) {
			e.printStackTrace();
		}

		gameGraph.placeRobot(robot(), fruit(), game);
		initGraph();
		DrawGraph();
		DrawFruits();
		game.startGame();
		runAuto();

	}

//Game Manual 
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
			numOfRobots = Utils.getNumOfRobots(gameOver);
			gameGraph.initRobot(robot(), numOfRobots);

		} catch (Exception e) {
			e.printStackTrace();
		}
		initGraph();
		DrawGraph();
		DrawFruits();
		StdDraw.show();
		int numberRobots = numOfRobots;
		// Position the robots at the player's request
		while (numberRobots > 0) {
			String place = JOptionPane.showInputDialog("Please select a vertex that does not have a robot ");
			if (place == null) {
				System.exit(0);
			}
			try {
				int ver = Integer.parseInt(place);
				if (!Utils.existsRobot(robot(), ver)) {
					placeRobotManual(ver);
					node_data n = gameGraph.graph().getNode(ver);
					if (n != null) {
						numberRobots--;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		game.startGame();
		runManual();
	}

	public void initGraph() {
		StdDraw.setCanvasSize(1100, 600);
		Range x = Utils.rangeX(gameGraph.graph().getV());
		Range y = Utils.rangeY(gameGraph.graph().getV());
		// set scales
		StdDraw.setXscale(x.get_min() - 5, x.get_max() + 5);
		StdDraw.setYscale(y.get_min() - 5, y.get_max() + 5);
		DrawGraph();
		DrawFruits();
		DrawRobot();
		StdDraw.show();
	}

	// Choose which game to play.
	private void optionGame() {
		int scenario_num = -1;
		String scenario_str = JOptionPane.showInputDialog("Please select a scenario from 0 to 23"); // window to select
																									// a scenario
		if (scenario_str == null) {
			System.exit(0);
		}
		try {
			scenario_num = Integer.parseInt(scenario_str);

		} catch (Exception e) {
			throw new RuntimeException("the scenario need to be from 0 to 23");

		}
		String[] Game = { "Manually Game", "Automatic Game" };
		Object typeOfGame = JOptionPane.showInputDialog(null, "Please select a game type", "Game",
				JOptionPane.INFORMATION_MESSAGE, null, Game, Game[0]);
		if (typeOfGame == null) {
			System.exit(0);
		}
		// Automatic game
		if (typeOfGame == "Automatic Game") {
			StdDraw.clear();
			StdDraw.enableDoubleBuffering();
			kml = new KML_Logger(scenario_num);
			startGameAuto(scenario_num);
			// Game manual
		} else {
			StdDraw.clear();
			StdDraw.enableDoubleBuffering();
			kml = new KML_Logger(scenario_num);
			startGameManual(scenario_num);
		}

	}

	// update the robots while the game is running
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
				if (MyGameGUI.kml != null) {
					// add to the KML
					MyGameGUI.kml.addPlaceMark("robot", Utils.getRobot(robot(), i).getLocation().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// update the fruit while the game is running
	public void updateFruits() {
		List<String> updateFruits = this.game.getFruits();
		if (updateFruits != null) {
			for (int i = 0; i < fruit().size(); i++) {
				fruit().get(i).update(updateFruits.get(i));
				Utils.matchFruitToEdge(gameGraph.graph(), fruit().get(i));
				if (MyGameGUI.kml != null) {
					// add to the KML
					if (fruit().get(i).getType() == 1) {
						// the type is apple
						MyGameGUI.kml.addPlaceMark("apple", fruit().get(i).getLocation().toString());
					} else {
						// the type is banana
						MyGameGUI.kml.addPlaceMark("banana", fruit().get(i).getLocation().toString());
					}
				}
			}
		}
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

					// weight
					double w = Utils.twoDigitsAfterPoint(e.getWeight());
					StdDraw.setPenColor(Color.cyan);
					double Wx = (x0 + 2 * x1) / 3;
					double Wy = (y0 + 2 * y1) / 3;
					String w1 = "" + w;
					Font font = new Font("BN anna", Font.BOLD, 12);
					StdDraw.setFont(font);
					StdDraw.text(Wx, Wy + 0.5, w1);

					// vertex
					StdDraw.setPenColor(Color.BLACK);
					StdDraw.point(x0, y0);
					String point = "";
					point += vertex.getKey();
					StdDraw.setPenRadius(0.02);
					StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
					Font font1 = new Font("BN anna", Font.BOLD, 15);
					StdDraw.setFont(font1);
					StdDraw.text(x0, y0 + 0.5, point); // the number of the vertex.
				}
			}
		}
	}

	// Draw fruits
	private void DrawFruits() {
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
		int moves = 0;
		try {
			JSONObject gameServer = new JSONObject(game.toString()).getJSONObject("GameServer"); // the grade from the
			moves = gameServer.getInt("moves"); // game
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
		StdDraw.text(x, +y, "{ Grade: " + grade + ",  Moves: " + moves + ",  Time left :" + timeToEnd + "  }");
	}

	/** Get/Set function **/

	public ArrayList<Robot> robot() {
		return gameGraph.Robots();
	}

	public ArrayList<Fruit> fruit() {
		return gameGraph.Fruits();
	}

	public KML_Logger getKML() {
		return this.kml;
	}

	/** Automatic Game **/
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
					double speed = ttt.getDouble("speed");
					String p_string = ttt.getString("pos");
					Point3D p = new Point3D(p_string);
					Graph_Algo ga = new Graph_Algo();
					ga.init(graph);
					if (dest == -1) {
						Fruit f = Utils.closestFruit(fruit(), gameGraph.graph(), ga, src);
						edge_data fruitEdge = f.getEdge();
						List<node_data> shortList = ga.shortestPath(src, fruitEdge.getSrc());
						if (src == fruitEdge.getSrc()) { // if the src of the robot and the src of the fruit edge are
															// the same - move to fruit edge dest.
							setThread(f, speed);
							game.chooseNextEdge(rid, fruitEdge.getDest());
							System.out.println("Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
						}

						else if (!shortList.isEmpty()) { // The robot has not yet reached fruit
							dest = shortList.get(0).getKey();
							game.chooseNextEdge(rid, dest);
							System.out.println("Turn to node: " + dest + "  time to end:" + (t / 1000));
						}
					}
				}

				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// finding the fruit closest to the robot
	public void setThread(Fruit f, double speed) {
		edge_data fruitEdge = f.getEdge();
		node_data dest = gameGraph.graph().getNode(fruitEdge.getDest());
		node_data src = gameGraph.graph().getNode(fruitEdge.getSrc());
		double currPath1 = src.getLocation().distance2D(dest.getLocation());
		double currPath2 = src.getLocation().distance2D(f.getLocation());
		long distFruitToDest = (long) ((((currPath2 / currPath1) * fruitEdge.getWeight()) / speed) * 100);
		try {
			sleep(distFruitToDest);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// Check if the edge between the src and the dest are exists.
	public boolean existsEdge(int src, int dest) {
		Collection<edge_data> edge = gameGraph.graph().getE(src);
		for (edge_data e : edge) {
			if (dest == e.getDest()) {
				return true;
			}
		}
		return false;

	}

	public void checkSleep() {
		for (Fruit f : fruit()) {
			for (Robot r : robot()) {
				if (r.getSrc() == f.getEdge().getSrc() && r.getDest() == f.getEdge().getDest()) {
					setThread(f, r.getSpeed());
					return;
				}
			}
		}
		try {
			sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
			checkSleep();
		}

		String res = game.toString();
		kml.end();
		String remark = kml.toString();
		game.sendKML(remark);
		System.out.println(res);
		gameOver();

	}

	/** Manual game **/
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
					int dest = ttt.getInt("dest");
					if (dest == -1) {
						if (StdDraw.isMousePressed()) {
							int dest1 = mouseClick(); // take the coordinates
							game.chooseNextEdge(rid, dest1);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void placeRobotManual(int node) {
		node_data n = gameGraph.graph().getNode(node);
		if (n != null) {
			// add all the robot to the game.
			game.addRobot(n.getKey());// add robot to the game
			updateRobots(game.getRobots()); // update robot
			robotsOnGraph++;
			DrawRobot();
			StdDraw.show();
			System.out.println(robotsOnGraph);
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
		KML();
		gameOver();
	}

	private void KML() {
		try {
			int ans = JOptionPane.showConfirmDialog(null, "Do you want a KML log", "KML logger",
					JOptionPane.YES_NO_OPTION);
			System.out.println(ans);
			if (ans == 0) {
				kml.end();
			}

		} catch (Exception e) {

		}
	}

	private void gameOver() {
		try {
			JSONObject gameServer = new JSONObject(game.toString()).getJSONObject("GameServer"); // the grade from game
			int grade = gameServer.getInt("grade");
			int move = gameServer.getInt("moves");
			JOptionPane.showMessageDialog(null, "          Game Over \n Grade: " + grade + "  Moves: " + move);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		;
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
		MyGameGUI mgg = new MyGameGUI();
	}

>>>>>>> branch 'master' of https://github.com/ohadcohen1111/Ex3-oop.git
}
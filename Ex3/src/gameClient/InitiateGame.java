package gameClient;

import java.util.ArrayList;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import utils.Point3D;

public class InitiateGame {
	private DGraph g;
	private ArrayList<Fruit> Fruits = new ArrayList<Fruit>();
	private ArrayList<Robot> Robots = new ArrayList<Robot>();
	final double EPSILON = 0.0001;

	// initializes the graph from the json file
	public InitiateGame(String graph) {
		initGraphFromJSON(graph);
	}

	// initializes the graph from the json file
	public void initGraphFromJSON(String s) {
		g = new DGraph();
		g.initFromJSON(s);
	}

	// initializes the fruits from the json file
	public void initFruitFromJSON(String s) {
		try {
			Fruit f = new Fruit(s);
			Fruits.add(f); // Add the new fruit to the list
			Utils.matchFruitToEdge(g, f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// initializes the robots from the json file
	public void initRobotFromJSON(String s) {
		try {
			Robot r = new Robot(s);
			Robots.add(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Position the robot in the optimal place
	public void placeRobot(ArrayList<Robot> robot, ArrayList<Fruit> fruit, game_service game) {
		for (Robot r : robot) {
			edge_data e = Utils.maxFruitValue(robot, fruit, g); // the edge on which the fruit is found with the
																// greatest value
			// initiate the location where the robot will starts
			Point3D robotStart = g.getNode(e.getSrc()).getLocation();
			r.setSrc(e.getSrc());
			r.setDest(-1);
			r.setLocation(robotStart);
			game.addRobot(e.getSrc());
			System.out.println(game.getRobots());
		}
	}

	// add robots to the list
	public void initRobot(ArrayList<Robot> robot, int numOfRobots) {
		for (int i = 0; i < numOfRobots; i++) {
			robot.add(new Robot(i));
		}
	}

	// returns the type of fruit
	public String typeOfFruit(Fruit f) {
		if (f.getType() == 1) {
			return "apple.png";
		} else {
			return "banana.png";
		}
	}

	// return the fruits list
	public ArrayList<Fruit> Fruits() {
		return this.Fruits;
	}

	// return the robots list
	public ArrayList<Robot> Robots() {
		return this.Robots;
	}

	// return the graph
	public DGraph graph() {
		return this.g;
	}

}

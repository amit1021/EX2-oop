//package gameClient;
//
//import java.util.ArrayList;
//import dataStructure.nodeData;
//import java.util.Collection;
//import java.util.Iterator;
//import algorithms.Graph_Algo;
//import Server.fruits;
//import Server.game_service;
//import gameClient.MyGameGUI;
//import dataStructure.DGraph;
//import dataStructure.edge_data;
//import dataStructure.node_data;
//import oop_dataStructure.oop_edge_data;
//import oop_dataStructure.oop_graph;
//import utils.StdDraw;
//import java.util.List;
//import java.lang.Thread;
//import static java.lang.Thread.sleep;
//
//public class AutomaticGame {
//	private MyGameGUI myGame;
//	final int INFINITE = Integer.MAX_VALUE;
//
////	public AutomaticGame(int scenario_num) {
////		myGame = new MyGameGUI(scenario_num);
////	}
//	public AutomaticGame(int scenario_num) {
//		myGame = new MyGameGUI(scenario_num);
//		game_service game = myGame.game();
//		game.startGame();
//		System.out.println(game.getRobots());
////		Thread t = new Thread(this);
////		t.start();
//	}
//
//	public void shortestPathDistBetweenFruits(Fruit fruit) {
//		Graph_Algo ga = new Graph_Algo();
//		ga.init(myGame.gameGraph().graph());
//		Robot rob = new Robot();
//		ArrayList<node_data> minPath = new ArrayList();
//		edge_data e = myGame.gameGraph().matchFruitToEdge(fruit);
//		double minPathDist = INFINITE;
//		for (Robot robot : myGame.robots()) {
//			double currPathDist = ga.shortestPathDist(robot.getSrc(), e.getSrc());
//			double FruitEdgeWeight = e.getWeight();
//			double ratio = (currPathDist + FruitEdgeWeight) / robot.getSpeed();
//			if (ratio < minPathDist) {
//				minPathDist = ratio;
//				minPath = (ArrayList<node_data>) ga.shortestPath(robot.getSrc(), e.getSrc());
//				rob = robot;
//			}
//		}
//		if (rob.getSrc() == e.getSrc()) {
//			myGame.game().chooseNextEdge(rob.getId(), e.getDest());
//		} else {
//			myGame.game().chooseNextEdge(rob.getId(), minPath.get(0).getKey());
//			rob.setDest(minPath.get(0).getKey());
//			fruit.setExists(false);
//		}
//	}
//
//	public void moveRobot() {
//		game_service game = myGame.game();
//		List<String> log = game.move();
//		if (log != null) {
//			myGame.updateFruits();
//			myGame.updateRobots(log);
//			myGame.sortFruits();
//			for (Fruit fruit : myGame.fruit()) {
//				if (fruit.getExists()) {
//					shortestPathDistBetweenFruits(fruit);
//				}
//			}
//		}
//	}
//
//	public void run() {
//		while (this.myGame.game().isRunning()) {
//			moveRobot();
//			myGame.drawAll();
//			StdDraw.show();
//			StdDraw.clear();
//			try {
//				sleep(80);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		String results = this.myGame.game().toString();
//		System.out.println("Game Over: " + results);
//	}
//
//	public MyGameGUI my() {
//		return myGame;
//	}
//}

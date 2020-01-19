package gameClient;

import java.util.ArrayList;
import dataStructure.nodeData;
import java.util.Collection;
import java.util.Iterator;
import algorithms.Graph_Algo;
import Server.fruits;
import Server.game_service;
import gameClient.MyGameGUI;
import dataStructure.DGraph;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import utils.Point3D;
import utils.StdDraw;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Thread;
import static java.lang.Thread.sleep;

public class AutomaticGame {
	private MyGameGUI myGame;
	final int INFINITE = Integer.MAX_VALUE;

	public AutomaticGame() {
		;
	}

	/** Automatic Game **/
	public void moveRobot(game_service game, graph graph) {
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
						myGame.getRobot(rid).setShortPath(shortPathCur);
						if (!myGame.getRobot(rid).getShortPath().isEmpty()) {
							if (src == fruitEdge.getSrc()) {
								game.chooseNextEdge(rid, fruitEdge.getDest());
								System.out.println(
										"Turn to node: " + fruitEdge.getDest() + "  time to end:" + (t / 1000));
								if (src == fruitEdge.getDest()) {
									myGame.getRobot(rid).getShortPath().remove(0);
								}
								System.out.println("asaadadf");
							} else {
								System.out.println("qqqq");
								game.chooseNextEdge(rid, myGame.getRobot(rid).getShortPath().get(0).getKey());
								myGame.getRobot(rid).setDest(myGame.getRobot(rid).getShortPath().get(0).getKey());
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
									myGame.getRobot(rid).setDest(-1);
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

	public edge_data cloestFruit(Graph_Algo ga, int robotSrc) {
		edge_data fruitEdge = new edgeData();
		double shortestPath = INFINITE;
		for (Fruit f : myGame.fruit()) {
			edge_data currFruitEdge = myGame.gameGraph().matchFruitToEdge(f);
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

	public void doFalseToFruit(Point3D p) {
		for (Fruit f : myGame.fruit()) {
			double currX = f.getLocation().x();
			double currY = f.getLocation().y();
			if (currX - p.x() == 0 && currY - p.y() == 0) {
				f.setExists(false);
			}
		}
	}

}

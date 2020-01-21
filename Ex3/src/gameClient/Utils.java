package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import org.json.JSONObject;
import algorithms.Graph_Algo;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
import utils.Range;

public class Utils {

	final static double EPSILON = 0.0001;
	final static int INFINITE = Integer.MAX_VALUE;
	final static int MINUS_INFINITE = Integer.MIN_VALUE;

	// return the number of the robots in the game
	public static int getNumOfRobots(JSONObject json) {
		try {
			int numOfRobots = json.getInt("robots");
			return numOfRobots;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	// checks if there is a robot on the vertex
	public static boolean existsRobot(ArrayList<Robot> robots, int src) {
		for (Robot r : robots) {
			if (r.getSrc() == src) {
				return true;
			}
		}
		return false;
	}

	public static Range rangeX(Collection<node_data> V) {
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

	public static Range rangeY(Collection<node_data> V) {
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

	// display the number 2 digits after the decimal point
	public static double twoDigitsAfterPoint(double e) {
		double e1 = e * 100;
		e1 = (int) e1;
		e1 = e1 / 100;
		return e1;
	}

	// sorting the fruits from high value to low value
	public static void sortFruits(ArrayList<Fruit> fruit) {
		Comperator c = new Comperator();
		fruit.sort(c);
	}

	// checks whether there is a vertex in the clicked position
	public static int getNodeId(Collection<node_data> V, Point3D pressed) {
		for (node_data vertex : V) {
			double X = vertex.getLocation().x();
			double Y = vertex.getLocation().y();
			// if the coordinates of the vertex minus the coordinates of the location that
			// was clicked is less than Epsilon then there is such a vertex
			if (Math.abs(X - pressed.x()) <= EPSILON && Math.abs(Y - pressed.y()) <= EPSILON) {
				// return the clicked vertex
				return vertex.getKey();
			}
		}
		return -1;
	}

	// finding the edge on which the fruit is
	public static void matchFruitToEdge(graph g, Fruit fruit) {
		for (node_data vertex : g.getV()) {
			for (edge_data e : g.getE(vertex.getKey())) {
				// check if the fruit on the edge and return the type of the fruit
				node_data dest = g.getNode(e.getDest());
				double dist1 = vertex.getLocation().distance2D(fruit.getLocation());
				double dist2 = fruit.getLocation().distance2D(dest.getLocation());
				double dist = vertex.getLocation().distance2D(dest.getLocation());
				double res = dist - (dist1 + dist2);
				int type = 1;
				if (vertex.getKey() > dest.getKey()) {
					type = -1;
				}
				if ((Math.abs(res) <= Point3D.EPS2) && (fruit.getType() == type)) {
					fruit.setEdge(e);
				}
			}
		}
	}

	// finding the fruit closest to the robot
	public static Fruit closestFruit(ArrayList<Fruit> fruit, graph g, Graph_Algo ga, int robotSrc) {
		Fruit closeFruit = new Fruit();
		double shortestPath = INFINITE;
		for (Fruit f : fruit) {
			edge_data currFruitEdge = f.getEdge();
			if (currFruitEdge == null) {
				return null;
			}
			int fruitSrc = currFruitEdge.getSrc();
			// the length of the path from the robot to the fruit f
			double currentPath = ga.shortestPathDist(robotSrc, fruitSrc);
			// if the current distance is smaller than the shortest distance then replace so
			// the shortest distance will be equal to the current distance
			if (currentPath < shortestPath) {
				shortestPath = currentPath;
				closeFruit = f;
			}
		}
		return closeFruit;
	}

	// return the edge of this fruit with the greater value
	public static edge_data maxFruitValue(ArrayList<Robot> robot, ArrayList<Fruit> fruit, graph g) {
		sortFruits(fruit);
		if (!fruit.isEmpty()) {
			return fruit.get(0).getEdge();
		}
		return null;
	}

	// returns the id of the robot adjacent to the clicked vertex
	public static int getRobotId(graph g, ArrayList<Robot> robot, Point3D pressed) {
		Collection<node_data> V = g.getV();
		for (node_data vertex : V) {
			int robotId = getRobotIdHelp(robot, vertex);// Looking for the vertex that has a robot
			if (robotId != -1) { // if robotId is not equals -1 then such a robot exists on a vertex
				Collection<edge_data> edge = g.getE(vertex.getKey());
				if (edge != null) {
					for (edge_data e : edge) {
						// checks whether there is a robot on a vertex that connects to the clicked
						// vertex
						double neibX = g.getNode(e.getDest()).getLocation().x();
						double neibY = g.getNode(e.getDest()).getLocation().y();
						if (Math.abs(neibX - pressed.x()) <= EPSILON && Math.abs(neibY - pressed.y()) <= EPSILON) {
							return robotId;
						}
					}
				}
			}
		}
		return -1;
	}

	public static int getRobotIdHelp(ArrayList<Robot> robot, node_data n) {
		// checks whether there is a robot on the vertex
		for (Robot r : robot) {
			if (Math.abs(r.getLocation().x() - n.getLocation().x()) <= EPSILON
					&& Math.abs(r.getLocation().y() - n.getLocation().y()) <= EPSILON) {
				return r.getId();
			}
		}
		return -1;
	}

	// returns the robot according to the id
	public static Robot getRobot(ArrayList<Robot> robot, int id) {
		for (Robot r : robot) {
			if (r.getId() == id)
				return r;
		}
		return null;
	}

}

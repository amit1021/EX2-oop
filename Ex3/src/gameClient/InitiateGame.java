package gameClient;

import java.util.ArrayList;
import java.util.Collection;

import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;

public class InitiateGame {
	private DGraph g;
	private ArrayList<Fruit> Fruits = new ArrayList<Fruit>();
	private ArrayList<Robot> Robots = new ArrayList<Robot>();
	final double EPSILON = 0.0001;

	public InitiateGame(String graph) {
		initGraphFromJSON(graph);
	}

	public void initGraphFromJSON(String s) {
		g = new DGraph();
		g.initFromJSON(s);
	}

	public void initFruitFromJSON(String s) {
	//	Fruits.clear();
		try {
			Fruit f = new Fruit(s);
			Fruits.add(f); // Add the new fruit to the list
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(s);
	}

	public void initRobotFromJSON(String s) {
		try {
			Robot r = new Robot(s);
			Robots.add(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public int getRobotIdHelp(node_data n) {
		// checks whether there is a robot on the vertex
		for (int i = 0; i < Robots.size(); i++) {
			if (Math.abs(Robots.get(i).getLocation().x() - n.getLocation().x()) <= EPSILON
					&& Math.abs(Robots.get(i).getLocation().y() - n.getLocation().y()) <= EPSILON) {
				return Robots.get(i).getId();
			}
		}
		return -1;
	}

	public int getRobotByLocation(double x, double y) {
		for (int i = 0; i < this.Robots.size(); i++) {
			double robotX = this.Robots.get(i).getLocation().x();
			double robotY = this.Robots.get(i).getLocation().y();
			if (robotX == x && robotY == y) {
				return this.Robots.get(i).getId();
			}
		}
		return -1;
	}

	public String typeOfFruit(Fruit f) {
		if (f.getType() == 1) {
			return "apple.png";
		} else {
			return "banana.png";
		}
	}

	public edge_data matchFruitToEdge(Fruit f) {
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
						if (f.getType() == 1) {
							if (srcY < destY) {
								return e;
							}
						}
						if (f.getType() == -1) {
							if (srcY > destY) {
								return e;
							}
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

	public ArrayList<Fruit> Fruits() {
		return this.Fruits;
	}

	public ArrayList<Robot> Robots() {
		return this.Robots;
	}

	public DGraph graph() {
		return this.g;
	}

}

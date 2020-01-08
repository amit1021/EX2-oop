package algorithms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;

/**
 * This empty class represents the set of graph-theory algorithms which should
 * be implemented as part of Ex2 - Do edit this class.
 * 
 * @author
 *
 */
public class Graph_Algo implements graph_algorithms, Serializable {
	final int INFINITE = Integer.MAX_VALUE;
	private graph Dgraph;

	public Graph_Algo() {
		;
	}

	public Graph_Algo(graph g) {
		init(g);
	}

	@Override
	public void init(graph g) {
		this.Dgraph = g;
	}

	public void init(String file_name) {
		Dgraph = null;
		try {
			FileInputStream file = new FileInputStream(file_name);
			ObjectInputStream in = new ObjectInputStream(file);

			Dgraph = (graph) in.readObject();

			in.close();
			file.close();

			System.out.println("Object has been deserialized");

		}

		catch (IOException ex) {
			System.out.println("IOException is caught");
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}

	}

	@Override
	public void save(String file_name) {
		String filename = file_name;

		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);

			out.writeObject(Dgraph);

			out.close();
			file.close();

			System.out.println("Object has been serialized");
		} catch (IOException ex) {
			System.out.println("IOException is caught");
		}

	}

	@Override
	public boolean isConnected() {
		Collection<node_data> vertex = this.Dgraph.getV();
		for (node_data src : vertex) {
			for (node_data dest : vertex) {
				if (shortestPathDist(src.getKey(), dest.getKey()) == INFINITE) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public double shortestPathDist(int src, int dest) {
		Collection<node_data> vertex = this.Dgraph.getV();
		if (!vertex.contains(Dgraph.getNode(src)) || (!vertex.contains(Dgraph.getNode(dest)))) {
			throw new RuntimeException("The vertex not exits");
		}
		for (node_data ver : vertex) {
			ver.setTag(0);
			ver.setInfo("");
			ver.setWeight(INFINITE);
		}
		this.Dgraph.getNode(src).setWeight(0);
		for (int i = 1; i <= this.Dgraph.nodeSize();) {
			int min = findMinNode();
			Collection<edge_data> edge = this.Dgraph.getE(this.Dgraph.getNode(min).getKey());
			if (edge != null) {
				for (edge_data e : edge) {
					// change the destination value to the minimum value between the current weight
					// and the previous weight
					double currentWeight = this.Dgraph.getNode(e.getDest()).getWeight();
					double srcPlusEdge = this.Dgraph.getNode(min).getWeight() + e.getWeight();
					if (srcPlusEdge < currentWeight) {
						this.Dgraph.getNode(e.getDest()).setWeight(srcPlusEdge);
						this.Dgraph.getNode(e.getDest()).setInfo(this.Dgraph.getNode(min).getInfo() + min + ",");
					}
				}
			}
			this.Dgraph.getNode(min).setTag(1);
			i++;
		}
		return this.Dgraph.getNode(dest).getWeight();
	}

	public int findMinNode() {
		Collection<node_data> vertex = this.Dgraph.getV();
		// initiate minWeight variable to find the min node
		double minWeight = INFINITE;
		int id = 1;
		for (node_data ver : vertex) {
			// if the current weight node is lower then weight, update the weight
			if (ver.getWeight() <= minWeight && (ver.getTag() == 0)) {
				minWeight = ver.getWeight();
				id = ver.getKey();
			}
		}
		return id;
	}

	@Override
	public List<node_data> shortestPath(int src, int dest) {
		List<node_data> shortPath = new ArrayList<node_data>();
		double path = shortestPathDist(src, dest);// find the short path
		if (path == 0) {
			return shortPath;
		} else if (path == INFINITE) {
			throw new RuntimeException("No path");
		}
		String ans = this.Dgraph.getNode(dest).getInfo();// the string of the short path
		ans += this.Dgraph.getNode(dest).getKey(); // add the destination
		String[] node = ans.split(",");
		for (int i = 0; i < node.length; i++) {
			int key = Integer.parseInt(node[i]);
			shortPath.add(this.Dgraph.getNode(key));
		}
		return shortPath;
	}

	@Override
	public List<node_data> TSP(List<Integer> targets) {
		if (!this.isConnected()) {
			return null;
		}
		List<node_data> res = new ArrayList<node_data>();
		List<node_data> tmpRes = new ArrayList<node_data>();
		double currentDist = INFINITE;
		int dest = 0;
		int from = firstIterationTSP(targets);
		int i = 0;
		while (!targets.isEmpty()) {
			double minDist = INFINITE;
			for (int j = 0; j < targets.size(); j++) {
				if (!res.isEmpty()) {
					from = res.get(res.size() - 1).getKey();
				} else {
					if (i > 0) {
						from = targets.get(i);
					}
				}
				currentDist = shortestPathDist(from, targets.get(j));
				if (currentDist < minDist && currentDist != 0) {
					minDist = currentDist;
					tmpRes = shortestPath(from, targets.get(j));
					dest = targets.get(j);
				}
			}
			if (i == 0) {
				targets = remove(targets, from);

			}
			i++;
			res = copyPath(res, tmpRes);
			targets = remove(targets, dest);
		}

		return res;
	}

	public int firstIterationTSP(List<Integer> targets) {
		double currentDist = INFINITE;
		double minPath = INFINITE;
		int vertex = 0;
		for (int i = 0; i < targets.size(); i++) {
			for (int j = 0; j < targets.size(); j++) {
				currentDist = shortestPathDist(targets.get(i), targets.get(j));
				if (currentDist < minPath && currentDist != 0) {
					minPath = currentDist;
					vertex = targets.get(i);
				}
			}
		}
		return vertex;
	}

	public List<Integer> remove(List<Integer> targets, int dest) {

		for (int i = 0; i < targets.size(); i++) {
			if (targets.get(i) == dest) {
				targets.remove(i);
			}
		}
		return targets;
	}

	public List<node_data> copyPath(List<node_data> res, List<node_data> tmp) {
		for (int i = 0; i < tmp.size(); i++) {
			if (!res.isEmpty() && i == 0) {
				;
			} else {
				res.add(tmp.get(i));
			}
		}
		return res;
	}

	@Override
	public graph copy() {
		graph copy = new DGraph();
		Collection<node_data> vertex = this.Dgraph.getV();
		for (node_data ver : vertex) {
			node_data n = ver;
			copy.addNode(n);
		}
		for (node_data ver : vertex) {
			Collection<edge_data> edge = this.Dgraph.getE(ver.getKey());
			if (edge != null) {
				for (edge_data e : edge) {
					copy.connect(e.getSrc(), e.getDest(), e.getWeight());
				}
			}
		}
		return copy;
	}

}

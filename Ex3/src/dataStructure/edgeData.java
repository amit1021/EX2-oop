package dataStructure;

import java.io.Serializable;

public class edgeData implements edge_data, Serializable {
	private node_data src;
	private node_data dest;
	private double weight;
	private int tag;
	private String info;

	public edgeData(node_data src, node_data dest, double weight) {
		this.src = src;
		this.dest = dest;
		this.weight = weight;
		this.tag = 0;
		this.info = "";
	}

	public int getSrc() {
		return this.src.getKey();
	}

	public int getDest() {
		return this.dest.getKey();
	}

	public double getWeight() {
		return this.weight;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String s) {
		this.info = s;
	}

	public int getTag() {
		return this.tag;
	}

	public void setTag(int t) {
		this.tag = t;
	}
}

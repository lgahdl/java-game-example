package com.histudio.world;

public class Node {
	public Vector2i tile;
	public Node parent;
	public double totalCost, backCost, forwardCost;

	public Node(Vector2i tile, Node parent, double backCost, double forwardCost) {
		this.tile = tile;
		this.parent = parent;
		this.backCost = backCost;
		this.forwardCost = forwardCost;
		this.totalCost = backCost + forwardCost;
	}
}

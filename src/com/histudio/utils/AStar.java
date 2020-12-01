package com.histudio.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.world.Node;
import com.histudio.world.Tile;
import com.histudio.world.Vector2i;
import com.histudio.world.WallTile;
import com.histudio.world.World;

public class AStar {
	
	public static double lastTime = System.currentTimeMillis();
	private static Comparator<Node> nodeSorter = new Comparator<Node>() {
		@Override
		public int compare(Node n0, Node n1) {
			if (n1.totalCost < n0.totalCost) {
				return 1;
			} else if (n1.totalCost > n0.totalCost) {
				return -1;
			}
			return 0;
		}
	};

//	public static boolean clear() {
//		if (System.currentTimeMillis() - lastTime >= 1000) {
//			return true;
//		}
//		return false;
//	}

	public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
		
		lastTime = System.currentTimeMillis();
		List<Node> openList = new ArrayList<Node>();
		List<Node> closedList = new ArrayList<Node>();

		Node current = new Node(start, null, 0, getDistance(start, end));
		openList.add(current);
		while (openList.size() > 0) {
			Collections.sort(openList, nodeSorter);
			current = openList.get(0);
			if (current.tile.equals(end)) {
				// Arrived in the endpoint
				// Returns the value
				List<Node> path = new ArrayList<Node>();
				while (current.parent != null) {
					path.add(current);
					current = current.parent;
				}
				openList.clear();
				closedList.clear();
				return path;
			}

			openList.remove(current);
			closedList.add(current);

			for (int i = 0; i < 9; i++) {
				if (i == 4)
					continue;
				int x = current.tile.x;
				int y = current.tile.y;
				int xi = (i % 3) - 1;
				int yi = (i / 3) - 1;
				Tile tile = World.tiles[x + xi + ((y + yi) * Game.world.WIDTH)];
				if (tile == null)
					continue;
				if (tile instanceof WallTile)
					continue;

				if (i == 0) {
					Tile test = World.tiles[x + xi + 1 + (y + yi) * Game.world.WIDTH];
					Tile test2 = World.tiles[x + xi + (y + yi + 1) * Game.world.WIDTH];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				} else if (i == 2) {
					Tile test = World.tiles[x + xi - 1 + (y + yi) * Game.world.WIDTH];
					Tile test2 = World.tiles[x + xi + (y + yi + 1) * Game.world.WIDTH];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				} else if (i == 6) {
					Tile test = World.tiles[x + xi + 1 + (y + yi) * Game.world.WIDTH];
					Tile test2 = World.tiles[x + xi + (y + yi - 1) * Game.world.WIDTH];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				} else if (i == 8) {
					Tile test = World.tiles[x + xi - 1 + (y + yi) * Game.world.WIDTH];
					Tile test2 = World.tiles[x + xi + (y + yi - 1) * Game.world.WIDTH];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				}

				Vector2i newTile = new Vector2i(x + xi, y + yi);
				double backCost = current.backCost + getDistance(current.tile, newTile);
				double forwardCost = getDistance(newTile, end);

				Node node = new Node(newTile, current, backCost, forwardCost);

				if (vecInList(closedList, newTile) && backCost >= current.backCost)
					continue;
				if (!vecInList(openList, newTile)) {
					openList.add(node);
				} else if (backCost < current.backCost) {
					openList.remove(current);
					openList.add(node);
				}
			}
		}
		closedList.clear();
		return null;

	}

	private static boolean vecInList(List<Node> list, Vector2i vector) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).tile.equals(vector)) {
				return true;
			}
		}
		return false;
	}

	private static double getDistance(Vector2i tile, Vector2i goal) {
		double dx = tile.x - goal.x;
		double dy = tile.y - goal.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

}

package com.histudio.utils;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class QuadTree {

	public Rectangle boundary;
	private int capacity;
	private boolean isDivided = false;

	public List<QuadTreePoint> points;

	public QuadTree northwest;
	public QuadTree southwest;
	public QuadTree northeast;
	public QuadTree southeast;

	public QuadTree(Rectangle boundary, int capacity) {
		this.boundary = boundary;
		this.capacity = capacity;
		this.points = new ArrayList<QuadTreePoint>();
	}

	private void subdivide() {
		int x = this.boundary.x;
		int y = this.boundary.y;
		int width = this.boundary.width;
		int height = this.boundary.height;
		Rectangle northwestBoundary = new Rectangle(x - width / 2, y - height / 2, width / 2, height / 2);
		Rectangle northeastBoundary = new Rectangle(x + width / 2, y - width / 2, width / 2, height / 2);
		Rectangle southwestBoundary = new Rectangle(x - width / 2, y + width / 2, width / 2, height / 2);
		Rectangle southeastBoundary = new Rectangle(x + width / 2, y + width / 2, width / 2, height / 2);
		this.northwest = new QuadTree(northwestBoundary, this.capacity);
		this.northeast = new QuadTree(northeastBoundary, this.capacity);
		this.southwest = new QuadTree(southwestBoundary, this.capacity);
		this.southeast = new QuadTree(southeastBoundary, this.capacity);
		this.isDivided = true;
	}

	private void addPointSubDivision(QuadTreePoint point) {
		if (point.x > this.boundary.x) {
			if (point.y > this.boundary.y) {
				this.southeast.insert(point);
			} else {
				this.northeast.insert(point);
			}
		} else {
			if (point.y > this.boundary.y) {
				this.southwest.insert(point);
			} else {
				this.northwest.insert(point);
			}
		}
	}

	public void insert(QuadTreePoint point) {
		if (this.points.size() < this.capacity) {
			this.points.add(point);
		} else {
			if (!isDivided) {
				this.subdivide();
			}
			this.addPointSubDivision(point);
		}
	}

	public List<QuadTreePoint> query(Rectangle range) {
		List<QuadTreePoint> found = new ArrayList<QuadTreePoint>();
		System.out.println(this.boundary.x);
		System.out.println(this.boundary.width);
		if (!this.boundary.intersects(range)) {
			return found;
		} else {
			System.out.println("here");
			if (this.isDivided) {
				List<QuadTreePoint> foundNE = this.northeast.query(range);
				List<QuadTreePoint> foundNW = this.northwest.query(range);
				List<QuadTreePoint> foundSE = this.southeast.query(range);
				List<QuadTreePoint> foundSW = this.southwest.query(range);
				found.addAll(foundNE);
				found.addAll(foundNW);
				found.addAll(foundSE);
				found.addAll(foundSW);
			} else {
				for (int i = 0; i < this.points.size(); i++) {
					QuadTreePoint currentPoint = this.points.get(i);
					int RangeInitialX = range.x - range.width / 2;
					int RangeFinalX = range.x + range.width / 2;
					int RangeInitialY = range.y - range.height / 2;
					int RangeFinalY = range.y + range.height / 2;
					if (currentPoint.x > RangeInitialX && currentPoint.x < RangeFinalX) {
						if (currentPoint.y > RangeInitialY && currentPoint.y < RangeFinalY) {
							found.add(currentPoint);
						}
					}
				}
			}
		}
		return found;
	}
}

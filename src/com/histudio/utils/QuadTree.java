package com.histudio.utils;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.histudio.entities.Enemy;
import com.histudio.entities.Entity;
import com.histudio.entities.Fireball;
import com.histudio.entities.Particle;

public class QuadTree {

	public Rectangle boundary;
	private int capacity;
	private boolean isDivided = false;
	public boolean canSubdivide = true;
	public List<Entity> entities;

	public QuadTree northwest;
	public QuadTree southwest;
	public QuadTree northeast;
	public QuadTree southeast;

	public QuadTree(Rectangle boundary, int capacity) {
		this.boundary = boundary;
		this.capacity = capacity;
		this.entities = new ArrayList<Entity>();
	}
	
	private void subdivide() {
		int x = this.boundary.x;
		int y = this.boundary.y;
		int width = this.boundary.width;
		int height = this.boundary.height;
		Rectangle northwestBoundary = new Rectangle(x, y, width / 2, height / 2);
		Rectangle northeastBoundary = new Rectangle(x + width / 2, y, width / 2, height / 2);
		Rectangle southwestBoundary = new Rectangle(x, y + width / 2, width / 2, height / 2);
		Rectangle southeastBoundary = new Rectangle(x + width / 2, y + width / 2, width / 2, height / 2);
		this.northwest = new QuadTree(northwestBoundary, this.capacity);
		this.northeast = new QuadTree(northeastBoundary, this.capacity);
		this.southwest = new QuadTree(southwestBoundary, this.capacity);
		this.southeast = new QuadTree(southeastBoundary, this.capacity);
		if(width/2<=32 || height/2 > 32) {
			this.northwest.canSubdivide = false;
			this.northeast.canSubdivide = false;
			this.southwest.canSubdivide = false;
			this.southeast.canSubdivide = false;
		}
	}

	private void addEntityOnSubDivision(Entity entity) {

		if (entity.getX() >= (this.boundary.x + this.boundary.width / 2)) {
			if (entity.getY() >= (this.boundary.y + this.boundary.height / 2)) {
				this.southeast.insert(entity);
			} else {
				this.northeast.insert(entity);
			}
		} else {
			if (entity.getY() >= (this.boundary.y + this.boundary.height / 2)) {
				this.southwest.insert(entity);
			} else {
				this.northwest.insert(entity);
			}
		}
	}

	public void insert(Entity entity) {
		if (entity.collisionBox != null) {
			if (this.entities.size() < this.capacity && !isDivided) {
				this.entities.add(entity);
			} else {
				if (!isDivided && canSubdivide) {
					this.subdivide();
					for (int i = 0; i < this.entities.size(); i++) {
						addEntityOnSubDivision(this.entities.get(i));
						this.entities.remove(entities.get(i));
					}
					this.isDivided = true;
				} else if(isDivided) {
					this.addEntityOnSubDivision(entity);
				} else {
					this.entities.add(entity);
				}
			}
		}
	}

	public List<Entity> query(Rectangle range) {
		List<Entity> found = new ArrayList<Entity>();
		if (!this.boundary.intersects(range)) {
			return found;
		} else {
			if (this.isDivided) {
				List<Entity> foundNE = this.northeast.query(range);
				List<Entity> foundNW = this.northwest.query(range);
				List<Entity> foundSE = this.southeast.query(range);
				List<Entity> foundSW = this.southwest.query(range);
				found.addAll(foundNE);
				found.addAll(foundNW);
				found.addAll(foundSE);
				found.addAll(foundSW);
			} else {
				for (int i = 0; i < this.entities.size(); i++) {
					Entity currentEntity = this.entities.get(i);
					int RangeInitialX = range.x;
					int RangeFinalX = range.x + range.width;
					int RangeInitialY = range.y;
					int RangeFinalY = range.y + range.height;
					if (currentEntity.getX() > RangeInitialX && currentEntity.getX() < RangeFinalX) {
						if (currentEntity.getY() > RangeInitialY && currentEntity.getY() < RangeFinalY) {
							found.add(currentEntity);
						}
					}
				}
			}
		}
		return found;
	}
}

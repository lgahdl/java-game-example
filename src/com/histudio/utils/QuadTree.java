package com.histudio.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.histudio.entities.Enemy;
import com.histudio.entities.Entity;
import com.histudio.entities.Fireball;
import com.histudio.entities.Particle;
import com.histudio.world.Camera;

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
		this.isDivided = true;
		if (width / 2 <= 32) {
			this.northwest.canSubdivide = false;
			this.northeast.canSubdivide = false;
			this.southwest.canSubdivide = false;
			this.southeast.canSubdivide = false;
		}
	}

	private void addEntityOnSubDivision(Entity entity) {

		if (entity.getX()+16 > (this.boundary.x + this.boundary.width / 2)) {
			if (entity.getY()+16 > (this.boundary.y + this.boundary.height / 2)) {
				this.southeast.insert(entity);
			} else {

				this.northeast.insert(entity);
			}
		} else {
			if (entity.getY()+16 > (this.boundary.y + this.boundary.height / 2)) {

				this.southwest.insert(entity);
			} else {

				this.northwest.insert(entity);
			}
		}
	}

	public void insert(Entity entity) {
		if (entity.collisionBox != null) {
			if (!isDivided) {
				if (this.entities.size() < this.capacity || !canSubdivide) {
					this.entities.add(entity);
				} else if (canSubdivide) {
					this.subdivide();
					for (int i = 0; i < this.entities.size(); i++) {
						Entity e = this.entities.get(i);
						addEntityOnSubDivision(e);
					}
					this.entities.removeAll(this.entities);
					this.addEntityOnSubDivision(entity);
				}
			} else {
				this.addEntityOnSubDivision(entity);
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
				int rangeInitialX = range.x;
				int rangeFinalX = range.x + range.width;
				int rangeInitialY = range.y;
				int rangeFinalY = range.y + range.height;
				for (int i = 0; i < this.entities.size(); i++) {
					Entity currentEntity = this.entities.get(i);
					if (currentEntity.getX()+16 >= rangeInitialX && currentEntity.getX()+16 <= rangeFinalX) {
						if (currentEntity.getY()+16 >= rangeInitialY && currentEntity.getY()+16 <= rangeFinalY) {
							found.add(currentEntity);
						}
					}
				}
			}
		}
		return found;
	}

	public void render(Graphics g) {
		g.setColor(Color.PINK);
		g.drawRect(this.boundary.x - Camera.x, this.boundary.y - Camera.y, this.boundary.width, this.boundary.height);
		if (this.isDivided) {
			this.southeast.render(g);
			this.northeast.render(g);
			this.southwest.render(g);
			this.northwest.render(g);
		}
	}

}

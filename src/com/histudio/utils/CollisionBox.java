package com.histudio.utils;

import java.awt.Rectangle;

import com.histudio.entities.Entity;

public class CollisionBox extends Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Entity father = null;

	public CollisionBox(double x, double y, int width, int height) {
		super((int) x, (int) y, width, height);
	}

	public CollisionBox(int x, int y, int width, int height, Entity father) {
		super(x, y, width, height);
		this.father = father;
	}

}

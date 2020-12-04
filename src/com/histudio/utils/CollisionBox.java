package com.histudio.utils;

import java.awt.Rectangle;

import com.histudio.entities.Entity;

public class CollisionBox extends Rectangle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Entity father = null;

	public boolean solid = false;

	public Rectangle range;

	public static int rangeWidth = 400, rangeHeight = 400;

	public CollisionBox(double x, double y, int width, int height, boolean solid) {
		super((int) x, (int) y, width, height);
		this.solid = solid;
		this.range = new Rectangle((int) x - rangeWidth / 2, (int) y - rangeHeight / 2, rangeWidth, rangeHeight);
	}

	public CollisionBox(int x, int y, int width, int height) {
		super(x, y, width, height);
		this.range = new Rectangle((int) x - rangeWidth / 2 + this.width / 2, (int) y - rangeHeight / 2+ this.height / 2, rangeWidth, rangeHeight);
	}

	public void setRangeProps(int newRangeWidth, int newRangeHeight) {
		this.range = new Rectangle((int) x - newRangeWidth / 2 + this.width / 2,
				(int) y - newRangeHeight / 2 + this.height / 2, newRangeWidth, newRangeHeight);
	}

}

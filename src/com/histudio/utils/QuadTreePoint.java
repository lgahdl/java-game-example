package com.histudio.utils;

import java.awt.Rectangle;

import com.histudio.entities.Entity;

public class QuadTreePoint {
	public int x;

	public int y;

	public Entity entity;

	public QuadTreePoint(int x, int y, Entity entity) {
		this.x = x;
		this.y = y;
		this.entity = entity;
	}
}

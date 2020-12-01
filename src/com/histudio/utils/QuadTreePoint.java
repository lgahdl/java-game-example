package com.histudio.utils;

import java.awt.Rectangle;

import com.histudio.entities.Entity;

public class QuadTreePoint {
	public int x;

	public int y;

	public Entity entity;

	public QuadTreePoint(Entity entity) {
		this.entity = entity;
	}
}

package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.utils.CollisionBox;
import com.histudio.utils.QuadTreePoint;
import com.histudio.world.Camera;
import com.histudio.world.Node;
import com.histudio.world.Vector2i;

public class Entity {

	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6 * 32, 0, 32, 32);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7 * 32, 0, 32, 32);
	public static BufferedImage MANAPACK_EN = Game.spritesheet.getSprite(6 * 32, 32, 32, 32);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7 * 32, 32, 32, 32);
	public static BufferedImage SWORD_EN = Game.spritesheet.getSprite(5 * 32, 6 * 32, 32, 32);

	protected double x;
	protected double y;
	protected int width;
	protected int height;

	public CollisionBox collisionBox;
	
	public QuadTreePoint quadTreePoint;

	protected BufferedImage sprite;

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.collisionBox = new CollisionBox(x, y, width, height);
		this.sprite = sprite;
		this.quadTreePoint = new QuadTreePoint(this);
	}

	public Entity(int x, int y, int width, int height, BufferedImage sprite, CollisionBox collisionBox) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		this.collisionBox = collisionBox;
	}

	public int getX() {
		return (int) this.x;
	}

	public void setX(int x) {
		this.x = x;
		this.collisionBox.x = x;
		return;
	}

	public int getY() {
		return (int) this.y;
	}

	public void setY(int y) {
		this.y = y;
		this.collisionBox.y = y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public CollisionBox getCollisionBox() {
		return this.collisionBox;
	}

	public void setCollisionBox(CollisionBox collisionBox) {
		this.collisionBox = collisionBox;
	}

	public static Comparator<Entity> depthSorter = new Comparator<Entity>() {
		@Override
		public int compare(Entity e1, Entity e2) {
			if (e2.y < e1.y) {
				return 1;
			} else if (e2.y > e1.y) {
				return -1;
			}
			return 0;
		}
	};

	public void tick() {

	}

	public static double calculateDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y2 - y1), 2));
	}

	public boolean isColliding(Entity e1, Entity e2) {
		return e1.collisionBox.intersects(e2.collisionBox);
	}

	public void render(Graphics g) {
		renderCollisionBox(g);
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}

	private void renderCollisionBox(Graphics g) {
		g.setColor(Color.BLUE);
		g.fillRect(this.collisionBox.x - Camera.x, this.collisionBox.y - Camera.y, this.collisionBox.width,
				this.collisionBox.height);
	}
	
	public void onTriggerCollider(Object object) {
		System.out.println("OnTriggerCollider:" + object.getClass().getSimpleName());
	}
	
}

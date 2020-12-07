package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.utils.CollisionBox;
import com.histudio.world.Camera;
import com.histudio.world.Node;
import com.histudio.world.Vector2i;
import com.histudio.world.World;

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

	public String lastDirection = "right"; // Options: "right"(DEFAULT), "left", "up", "down"

	public double speedVertical = 0, speedHorizontal = 0, acceleration = 0.1;

	public CollisionBox collisionBox;

	protected BufferedImage sprite;

	public int collisionXOffset = 0, collisionYOffset = 0, collisionBoxWidth = 32, collisionBoxHeight = 32;

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.collisionBox = new CollisionBox(x + collisionXOffset, y + collisionYOffset, collisionBoxWidth,
				collisionBoxHeight);
		this.sprite = sprite;
	}

	public Entity(int x, int y, int width, int height, BufferedImage sprite, CollisionBox collisionBox) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		this.collisionBox = collisionBox;
	}

	public void setCollisionProps(int collisionXOffset, int collisionYOffset, int collisionBoxWidth,
			int collisionBoxHeight) {
		this.collisionXOffset = collisionXOffset;
		this.collisionYOffset = collisionYOffset;
		this.collisionBoxWidth = collisionBoxWidth;
		this.collisionBoxHeight = collisionBoxHeight;
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

	public boolean isColliding(CollisionBox collisionBox1, CollisionBox collisionBox2) {
		return collisionBox1.intersects(collisionBox2);
	}

	public void render(Graphics g) {
		renderCollisionBox(g);
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}

	public void renderCollisionBox(Graphics g) {
		g.setColor(Color.GREEN);
		g.drawRect(this.collisionBox.x - Camera.x, this.collisionBox.y - Camera.y, this.collisionBox.width,
				this.collisionBox.height);
	}
	
	public void renderRangeBox(Graphics g) {
		g.setColor(Color.BLUE);
		g.drawRect(this.collisionBox.range.x - Camera.x, this.collisionBox.range.y - Camera.y,
				this.collisionBox.range.width, this.collisionBox.range.height);
	}
	
	public void applyAcceleration(String direction, double accelerationCur) {
		switch (direction) {
		case "right":
			speedHorizontal += accelerationCur;
			this.lastDirection = "right";
			break;
		case "left":
			speedHorizontal -= accelerationCur;
			this.lastDirection = "left";
			break;
		case "up":
			speedVertical -= accelerationCur;
			this.lastDirection = "up";
			break;
		case "down":
			speedVertical += accelerationCur;
			this.lastDirection = "down";
			break;
		default:
			break;
		}
	}

	public void onTriggerCollider(Object object) {
		String className = object.getClass().getSimpleName();
		switch (className) {
		default:
			break;
		}
	}
	
}

package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.histudio.main.Game;
import com.histudio.world.Camera;

public class Entity {

	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6 * 32, 0, 32, 32);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7 * 32, 0, 32, 32);
	public static BufferedImage MANAPACK_EN = Game.spritesheet.getSprite(6 * 32, 32, 32, 32);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7 * 32, 32, 32, 32);

	protected double x;
	protected double y;
	protected int width;
	protected int height;

	protected BufferedImage sprite;

	public int maskx = 8, masky = 8, maskWidth = 12, maskHeight = 16;

	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
	}

	public int getX() {
		return (int) this.x;
	}

	public int getY() {
		return (int) this.y;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setMask(int maskx, int masky, int maskWidth, int maskHeight) {
		this.maskx = maskx;
		this.masky = masky;
		this.maskWidth = maskWidth;
		this.maskHeight = maskHeight;
	}

	public void tick() {

	}

	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx - Camera.x, e1.getY() + e1.masky - Camera.y, e1.maskWidth,
				e1.maskHeight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx - Camera.x, e2.getY() + e2.masky - Camera.y, e2.maskWidth,
				e2.maskHeight);
		return e2Mask.intersects(e1Mask);
	}

	public void render(Graphics g) {
//		g.setColor(Color.BLUE);
//		g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, this.maskWidth, this.maskHeight);
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
	}
}

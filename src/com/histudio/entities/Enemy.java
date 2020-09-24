package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gcstudios.world.Camera;
import com.gcstudios.world.World;
import com.histudio.main.Game;

public class Enemy extends Entity {

	private double speed = 0.8;

	private int maskx = 8, masky = 8, maskWidth = 16, maskHeight = 24;

	private int frames = 0, maxFrames = 7, index = 0, maxIndex = 4;

	private int damage = 10, framesToDamage = 15, damageFrames = 0;

	private boolean moved = false;
	private BufferedImage[] sprites;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[4];
		sprites[0] = Game.spritesheet.getSprite(224, 32, 32, 32);
		sprites[1] = Game.spritesheet.getSprite(256, 32, 32, 32);
		sprites[2] = Game.spritesheet.getSprite(224, 64, 32, 32);
		sprites[3] = Game.spritesheet.getSprite(256, 64, 32, 32);
		// TODO Auto-generated constructor stub
	}

	public void tick() {
		moved = false;
		if (isCollidingWithPlayer() == false) {

			if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY())
					&& !isColliding((int) (x + speed), this.getY())) {
				x += speed;
				moved = true;
			} else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY())
					&& !isColliding((int) (x - speed), this.getY())) {
				x -= speed;
				moved = true;
			}
			if (y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed))
					&& !isColliding(this.getX(), (int) (y + speed))) {
				y += speed;
				moved = true;
			} else if (y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed))
					&& !isColliding(this.getX(), (int) (y - speed))) {
				y -= speed;
				moved = true;
			}
		} else {
			damageFrames++;
			if (damageFrames >= framesToDamage) {
				damageFrames = 0;
				Game.player.setLife(Game.player.getLife() - this.damage);
				Game.player.setDamaged(true);
			}
		}
		if (moved) {
			frames++;
			if (frames >= maxFrames) {
				frames = 0;
				index++;
				if (index == maxIndex) {
					index = 0;
				}
			}
		}

	}

	public boolean isCollidingWithPlayer() {
		Rectangle currentEnemy = new Rectangle(this.getX() + maskx, this.getY() + masky, maskWidth, maskHeight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);
		return currentEnemy.intersects(player);
	}

	public boolean isColliding(int xnext, int ynext) {
		Rectangle currentEnemy = new Rectangle(xnext + maskx, ynext + masky, maskWidth, maskHeight);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this) {
				continue;
			}
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskWidth, maskHeight);
			if (currentEnemy.intersects(targetEnemy)) {
				return true;
			}
		}

		return false;

	}

	@Override
	public void render(Graphics g) {
		g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
//		g.setColor(Color.BLUE);
//		g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, maskWidth, maskHeight);
	}

}

package com.histudio.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.main.Sound;
import com.histudio.world.AStar;
import com.histudio.world.Camera;
import com.histudio.world.Node;
import com.histudio.world.Vector2i;
import com.histudio.world.World;

public class Enemy extends Entity {

	private double speed = 0.8;

	public int maskx = 16, masky = 4, maskWidth = 16, maskHeight = 32;

	private int frames = 0, maxFrames = 7, index = 0, maxIndex = 4;

	private int damage = 10, framesToDamage = 15, currentFrameToDamage = 0;

	private boolean moved = false;
	private BufferedImage[] sprites;
	private BufferedImage feedbackSprite;
	private boolean isDamaged = false;
	private int maxDamageFrames = 10, damageFrames = 0;
	public int life = 3;

	public boolean isActive = false;

	protected List<Node> path;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[4];
		sprites[0] = Game.spritesheet.getSprite(224, 32, 32, 32);
		sprites[1] = Game.spritesheet.getSprite(256, 32, 32, 32);
		sprites[2] = Game.spritesheet.getSprite(224, 64, 32, 32);
		sprites[3] = Game.spritesheet.getSprite(256, 64, 32, 32);
		feedbackSprite = Game.spritesheet.getSprite(288, 32, 32, 32);
		// TODO Auto-generated constructor stub
	}

	public void tick() {
		moved = false;
		if (life <= 0) {
			Game.entities.remove(this);
			Game.enemies.remove(this);
		}
		this.collidingFireball();
		this.collidingPlayer();
		double distance = calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY());
		if (distance < 80) {
			isActive = true;
		} else if (distance < 250 && (path == null || path.size() <= 12)) {
			isActive = true;
		} else {
			isActive = false;
		}
//			if(isActive) {
//				
//			if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY())
//					&& !isCollidingWithEnemy((int) (x + speed), this.getY())) {
//				x += speed;
//				moved = true;
//			} else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY())
//					&& !this.isCollidingWithEnemy((int) (x - speed), this.getY())) {
//				x -= speed;
//				moved = true;
//			}
//			if (y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed))
//					&& !isCollidingWithEnemy(this.getX(), (int) (y + speed))) {
//				y += speed;
//				moved = true;
//			} else if (y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed))
//					&& !isCollidingWithEnemy(this.getX(), (int) (y - speed))) {
//				y -= speed;
//				moved = true;
//			}
//		}
		if (isActive) {
			Vector2i start = new Vector2i(this.getX() / 32, this.getY() / 32);
			Vector2i end = new Vector2i(Game.player.getX() / 32, Game.player.getY() / 32);
			path = AStar.findPath(Game.world, start, end);
		}

		followPath(path);
		Game.ui.renderOnMinimap(this.getX() / 32, this.getY() / 32, "enemy");
		frames++;
		if (frames >= maxFrames) {
			frames = 0;
			index++;
			if (index == maxIndex) {
				index = 0;
			}
		}

		if (moved) {
		}

		if (isDamaged) {
			damageFrames++;
			if (damageFrames >= maxDamageFrames) {
				damageFrames = 0;
				isDamaged = false;
			}
		}
//		Game.ui.renderEnemyOnMinimap(this.getX() / 32, this.getY() / 32);
	}

	public void collidingFireball() {
		for (int i = 0; i < Game.fireballs.size(); i++) {
			FireballShoot e = Game.fireballs.get(i);
			e.setMask(FireballShoot.maskx, FireballShoot.masky, FireballShoot.maskHeight, FireballShoot.maskWidth);
			if (this.isCollidingWithFireball(e)) {
				Sound.enemyDamaged.play();
				life--;
				this.isDamaged = true;
				Game.fireballs.remove(i);
				return;
			}
		}
	}

	public boolean isCollidingWithFireball(FireballShoot e2) {
		Enemy e1 = this;
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx - Camera.x, e1.getY() + e1.masky - Camera.y, e1.maskWidth,
				e1.maskHeight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx - Camera.x, e2.getY() + e2.masky - Camera.y, e2.maskWidth,
				e2.maskHeight);
		return e2Mask.intersects(e1Mask);
	}

	public void collidingPlayer() {
		if (isCollidingWithPlayer()) {
			currentFrameToDamage++;
			if (currentFrameToDamage >= framesToDamage) {
				currentFrameToDamage = 0;
				Game.player.takeDamage(this.damage);
			}
		}
	}

	public boolean isCollidingWithPlayer() {
		Rectangle currentEnemy = new Rectangle(this.getX() + maskx, this.getY() + masky, maskWidth, maskHeight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);
		return currentEnemy.intersects(player);
	}

	public boolean isCollidingWithEnemy(int xnext, int ynext) {
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

	public void followPath(List<Node> path) {
		if (path != null) {
			if (path.size() > 0) {

				Vector2i target = path.get(path.size() - 1).tile;
				if (x < target.x * 32) {
					if (World.isFree((int) (x + speed), this.getY())
							&& !isCollidingWithEnemy((int) (x + speed), this.getY())) {
						x += speed;
						moved = true;
					}
				} else if (x > target.x * 32) {
					if (World.isFree((int) (x - speed), this.getY())
							&& !this.isCollidingWithEnemy((int) (x - speed), this.getY())) {
						x -= speed;
						moved = true;
					}
				}
				if (y < target.y * 32) {
					if (World.isFree(this.getX(), (int) (y + speed))
							&& !isCollidingWithEnemy(this.getX(), (int) (y + speed))) {
						y += speed;
						moved = true;
					}
				} else if (y > target.y * 32) {
					if (World.isFree(this.getX(), (int) (y - speed))
							&& !isCollidingWithEnemy(this.getX(), (int) (y - speed))) {
						y -= speed;
						moved = true;
					}
				}
				int tx32 = target.x * 32;
				int ty32 = target.y * 32;
				if ((this.getX() == tx32 || this.getX() + 1 == tx32 || this.getX() - 1 == tx32)
						&& (this.getY() == ty32 || this.getY() + 1 == ty32 || this.getY() - 1 == tx32)) {
					path.remove(path.size() - 1);
				}

			}
		}
	}

	@Override
	public void render(Graphics g) {
		if (!isDamaged) {
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else {
			g.drawImage(feedbackSprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}
	}

}

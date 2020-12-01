package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.main.Sound;
import com.histudio.utils.AStar;
import com.histudio.utils.CollisionBox;
import com.histudio.world.Camera;
import com.histudio.world.Node;
import com.histudio.world.Vector2i;
import com.histudio.world.World;

public class Enemy extends Entity {

	private int speed = 2;

	private int frames = 0, maxFrames = 7, index = 0, maxIndex = 4;

	public int damage = 10, framesToDamage = 15, currentFrameToDamage = 0;

	public int collisionXOffset = 8, collisionYOffset = 0, collisionBoxWidth = 18, collisionBoxHeight = 32;

	private boolean moved = false;
	private String direction = "right";
	public boolean canChangeDirection = false;
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
		this.collisionBox = new CollisionBox(x + collisionXOffset, y + collisionYOffset, collisionBoxWidth,
				collisionBoxHeight, this);
		// TODO Auto-generated constructor stub
	}

	public void tick() {
		moved = false;
		if (life <= 0) {
			Game.entities.remove(this);
			Game.enemies.remove(this);
		}
		if (this.isColliding(this, Game.player)) {
			currentFrameToDamage++;
			if (currentFrameToDamage >= framesToDamage) {
				currentFrameToDamage = 0;
				Game.player.onTriggerCollider(this);
			}
		}
		double distance = calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY());
		if (distance < 100) {
			isActive = true;
		} else if (distance < 300 && (path == null || path.size() <= 20)) {
			isActive = true;
		} else {
			isActive = false;
		}
//		if(isActive) {		
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
			Vector2i start = new Vector2i((this.getX() + 16) / 32, (this.getY() + 16) / 32);
			Vector2i end = new Vector2i((Game.player.getX() + 16) / 32, (Game.player.getY() + 16) / 32);
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

	public void getHit(int damage) {
		Sound.enemyDamaged.play();
		life = life - damage;
		this.isDamaged = true;
//		Game.fireballs.remove(i);
		World.generateParticles(200, this.getX(), this.getY());
		return;
	}

	public void onTriggerCollider(Object object) {
		String className = object.getClass().getSimpleName();
		switch (className) {
		case "FireballShoot":
			FireballShoot fireball = (FireballShoot) object;
			this.getHit(fireball.damage);
			break;
		default:
			((Entity) object).onTriggerCollider(this);
			System.out.println("Enemy Trigger not configured for:" + className);
			break;
		}
	}

	public boolean isCollidingWithEnemy(int xnext, int ynext) {
		Rectangle currentEnemy = new Rectangle(this.collisionBox);
		for (int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if (e == this) {
				continue;
			}
			Rectangle targetEnemy = new Rectangle(e.collisionBox);
			if (currentEnemy.intersects(targetEnemy)) {
				return true;
			}
		}

		return false;

	}

	public void followPath(List<Node> path) {
		if (path != null) {
			if (path.size() > 0) {

				CollisionBox nextPositionCollider = this.collisionBox;
				Vector2i target = path.get(path.size() - 1).tile;
				if (x < target.x * 32) {
					// GOING RIGHT
					nextPositionCollider = new CollisionBox(this.collisionBox.x + (int) Math.round(speed),
							this.collisionBox.y, this.collisionBox.width, this.collisionBox.height, this);
					if (World.isFree(nextPositionCollider) && !isCollidingWithEnemy((int) (x + speed), this.getY())) {
						x += speed;
						moved = true;
						this.collisionBox = nextPositionCollider;
						if (canChangeDirection) {
							direction = "right";
							canChangeDirection = false;
						} else {
							canChangeDirection = true;
						}
					}
				} else if (x > target.x * 32) {
					// GOING LEFT
					nextPositionCollider = new CollisionBox(this.collisionBox.x - (int) Math.round(speed),
							this.collisionBox.y, this.collisionBox.width, this.collisionBox.height, this);
					if (World.isFree(nextPositionCollider)
							&& !this.isCollidingWithEnemy((int) (x - speed), this.getY())) {
						x -= speed;
						moved = true;
						this.collisionBox = nextPositionCollider;
						if (canChangeDirection) {
							direction = "left";
							canChangeDirection = false;
						} else {
							canChangeDirection = true;
						}
					}
				}
				if (y < target.y * 32) {
					// GOING DOWN
					nextPositionCollider = new CollisionBox(this.collisionBox.x,
							this.collisionBox.y + (int) Math.round(speed), this.collisionBox.width,
							this.collisionBox.height, this);
					if (World.isFree(nextPositionCollider) && !isCollidingWithEnemy(this.getX(), (int) (y + speed))) {
						y += speed;
						moved = true;
						this.collisionBox = nextPositionCollider;
					}
				} else if (y > target.y * 32) {
					// GOING UP
					nextPositionCollider = new CollisionBox(this.collisionBox.x,
							this.collisionBox.y - (int) Math.round(speed), this.collisionBox.width,
							this.collisionBox.height, this);
					if (World.isFree(nextPositionCollider) && !isCollidingWithEnemy(this.getX(), (int) (y - speed))) {
						y -= speed;
						moved = true;
						this.collisionBox = nextPositionCollider;
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
		renderCollider(g);
		Graphics2D g2 = (Graphics2D) g;
		if (!isDamaged) {
			if (direction == "right") {
				g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			} else if (direction == "left") {
				g2.drawImage(sprites[index], this.getX() - Camera.x + this.getWidth(), this.getY() - Camera.y,
						-this.getWidth(), this.getHeight(), null);
			}
		} else {
			if (direction == "right") {
				g.drawImage(feedbackSprite, this.getX() - Camera.x, this.getY() - Camera.y, null);
			} else {
				g2.drawImage(feedbackSprite, this.getX() - Camera.x + this.getWidth(), this.getY() - Camera.y,
						-this.getWidth(), this.getHeight(), null);
			}
		}
	}

	private void renderCollider(Graphics g) {
		g.setColor(Color.BLUE);

		g.drawRect(this.collisionBox.x - Camera.x, this.collisionBox.y - Camera.y, this.collisionBox.width,
				this.collisionBox.height);
	}

}

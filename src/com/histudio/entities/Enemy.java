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

	public static int enemyCollisionXOffset = 8, enemyCollisionYOffset = 0, enemyCollisionBoxWidth = 18,
			enemyCollisionBoxHeight = 32;

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
		this.setCollisionProps(enemyCollisionXOffset, enemyCollisionYOffset, enemyCollisionBoxWidth,
				enemyCollisionBoxHeight);
		// TODO Auto-generated constructor stub
	}

	public void tick() {

		List<Entity> entitiesToCheck = Game.entitiesQuadTree.query(this.collisionBox.range);

		for (int i = 0; i < entitiesToCheck.size(); i++) {
			
			Entity e = entitiesToCheck.get(i);
			if (!e.equals(this)) {
				if (this.isColliding(this.collisionBox, e.collisionBox)) {
					e.onTriggerCollider(this);
				}
			}
		}

		moved = false;
		if (life <= 0) {
			Game.entities.remove(this);
			Game.enemies.remove(this);
		}
		double distance = calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY());
		if (distance < 100) {
			isActive = true;
		} else if (distance < 300 && (path == null || path.size() <= 20)) {
			isActive = true;
		} else {
			isActive = false;
		}
		// FIRST MOVEMENT STYLE:
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
		if (isActive && Game.world.isFree((Game.player.getX() + 16), (Game.player.getY() + 16))) {
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

	public void takeDamage(int damage) {
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
		case "Fireball":
			Fireball fireball = (Fireball) object;
			this.takeDamage(fireball.damage);
			World.generateParticles(200, this.getX(), this.getY());
			Game.entities.remove(fireball);
			break;
		case "Player":
			Player player = (Player) object;
			currentFrameToDamage++;
			if (currentFrameToDamage >= framesToDamage) {
				currentFrameToDamage = 0;
				player.takeDamage(this.damage);
				player.throwBack(this);
			}
			break;
		case "Enemy":
			break;
		case "MeleeAttack":
			System.out.println("here");
			MeleeAttack meleeAttack = (MeleeAttack) object;
			if(!meleeAttack.father.equals(this)) {
				this.takeDamage(meleeAttack.damage);			
			}
			break;
		default:
			((Entity) object).onTriggerCollider(this);
			System.out.println("Enemy Trigger not configured for:" + className);
			break;
		}
	}

	public void followPath(List<Node> path) {
		if (path != null) {

			if (path.size() > 0) {

				CollisionBox nextPositionCollider = this.collisionBox;
				Vector2i target = path.get(path.size() - 1).tile;
				if (x < target.x * 32) {
					// GOING RIGHT
					nextPositionCollider = new CollisionBox(this.collisionBox.x + (int) Math.round(speed),
							this.collisionBox.y, this.collisionBox.width, this.collisionBox.height);
					if (Game.world.isFree(nextPositionCollider)) {
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
							this.collisionBox.y, this.collisionBox.width, this.collisionBox.height);
					if (Game.world.isFree(nextPositionCollider)) {
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
							this.collisionBox.height);
					if (Game.world.isFree(nextPositionCollider)) {
						y += speed;
						moved = true;
						this.collisionBox = nextPositionCollider;
					}
				} else if (y > target.y * 32) {
					// GOING UP
					nextPositionCollider = new CollisionBox(this.collisionBox.x,
							this.collisionBox.y - (int) Math.round(speed), this.collisionBox.width,
							this.collisionBox.height);
					if (Game.world.isFree(nextPositionCollider)) {
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
		renderCollisionBox(g);
		renderRangeBox(g);
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
	

}

package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.main.Sound;
import com.histudio.utils.CollisionBox;
import com.histudio.world.Camera;
import com.histudio.world.World;

public class Player extends Entity {

	public boolean right, up, left, down, isUp, isRight = true, isRunning = false;

	public boolean meleeAttacking = false, isAttacking = false;

	public double speedVertical = 0, speedHorizontal = 0, normalMaxSpeed = 2, runningMaxSpeed = 3, acceleration = 0.1;

	public int throwBackFrames = 0, throwBackMaxFrames = 20, throwBackAcceleration = 0;

	public double sourceXPot = 0, sourceYPot = 0;

	public boolean isThrowed = false;

	private int frames = 0, maxFrames = 7, index = 0, maxIndex = 7;

	private boolean moved = false;
	private boolean damaged = false;

	private BufferedImage[] frontPlayer;

	private BufferedImage[] leftPlayer;

	private BufferedImage[] rightPlayer;

	private BufferedImage[] upPlayer;

	public BufferedImage[] playerDamaged;

	private double life = 100, maxLife = 100;

	private double mana = 100, maxMana = 100;

	private int attackFrames = 0, maxAttackFrames = 60;

	private Weapon weapon;

	private Sword sword;

	public MeleeAttack meleeAttack = null;

	private String lastPressedMovementKey = "right";

	public boolean shoot = false, mouseShoot = false;

	private boolean jump = false, isJumping = false;

	private int jumpFrames = 50, jumpCur = 0, jumpSpeed = 3;
	private int z = 0;
	private boolean zTop = false;

	public int mx, my;

	public static int playerCollisionXOffset = 6, playerCollisionYOffset = 12, playerCollisionBoxWidth = 18,
			playerCollisionBoxHeight = 16;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		frontPlayer = new BufferedImage[8];
		leftPlayer = new BufferedImage[8];
		rightPlayer = new BufferedImage[8];
		upPlayer = new BufferedImage[8];
		playerDamaged = new BufferedImage[8];
		this.setCollisionProps(playerCollisionXOffset, playerCollisionYOffset, playerCollisionBoxWidth,
				playerCollisionBoxHeight);
		for (int i = 0; i < 8; i++) {
			frontPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 0, 32, 32);
			rightPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 32, 32, 32);
			leftPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 64, 32, 32);
			upPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 96, 32, 32);
			playerDamaged[i] = Game.playerSpritesheet.getSprite(0, 0, 32, 32);
		}

		this.collisionBox = new CollisionBox(x + collisionXOffset, y + collisionYOffset, collisionBoxWidth,
				collisionBoxHeight);
	}

	public double getLife() {
		return this.life;
	}

	public void setLife(double newLife) {
		if (newLife <= this.maxLife) {
			this.life = newLife;
		} else {
			this.life = this.maxLife;
		}

	}

	public double getMaxLife() {
		return this.maxLife;
	}

	public void setMaxLife(double newMaxLife) {
		this.maxLife = newMaxLife;
	}

	public double getMana() {
		return this.mana;
	}

	public void setMana(double newMana) {
		if (newMana <= this.maxMana) {
			this.mana = newMana;
		} else {
			this.mana = this.maxMana;
		}
	}

	public double getMaxMana() {
		return this.maxMana;
	}

	public void setMaxMana(double newMaxMana) {
		this.maxMana = newMaxMana;
	}

	public boolean getJump() {
		return this.jump;
	}

	public void setJump(boolean value) {
		this.jump = value;
		return;
	}

	public boolean getIsJumping() {
		return this.isJumping;
	}

	public void setIsJumping(boolean value) {
		this.isJumping = value;
		return;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setDamaged(boolean value) {
		this.damaged = value;
	}

	public void setShoot(boolean value) {
		if (this.weapon instanceof Weapon && this.mana >= 2 && !this.isJumping) {
			this.shoot = value;
		}
	}

	public void setCollisionBoxPosition(int x, int y) {
		this.collisionBox.x = x;
		this.collisionBox.y = y;
	}

	public void setMouseShoot(boolean value) {

		if (this.weapon instanceof Weapon && this.mana >= 2 && !this.isJumping) {
			this.mouseShoot = value;
		}

	}

	public Weapon getWeapon() {
		return this.weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	public Sword getSword() {
		return this.sword;
	}

	public void setSword(Sword sword) {
		this.sword = sword;
		return;
	}

	public void swordAttack() {
		if (this.meleeAttack == null) {
			this.meleeAttacking = true;
		}
	}

	public void takeDamage(int damage) {
		this.setLife(this.life - damage);
		this.setDamaged(true);
		Sound.playerDamaged.play();
	}

	public void throwBack(Enemy enemy) {
		this.isThrowed = true;
		double playerCenterX = this.collisionBox.x + this.collisionBox.width / 2;
		double playerCenterY = this.collisionBox.y + this.collisionBox.height / 2;
		double enemyCenterX = enemy.collisionBox.x + enemy.collisionBox.width / 2;
		double enemyCenterY = enemy.collisionBox.y + enemy.collisionBox.height / 2;

		double relativeDistX = 10 * Math.abs(playerCenterX - enemyCenterX)
				/ (enemy.collisionBox.width / 2 + this.collisionBox.width / 2);
		double relativeDistY = 10 * Math.abs(playerCenterY - enemyCenterY)
				/ (enemy.collisionBox.height / 2 + this.collisionBox.height / 2);

		String horizontal = playerCenterX - enemyCenterX > 0 ? "right" : "left";
		String vertical = playerCenterY - enemyCenterY > 0 ? "down" : "up";

		sourceXPot = relativeDistX;
		sourceYPot = relativeDistY;

		applyAcceleration(horizontal, acceleration * relativeDistX);
		applyAcceleration(vertical, acceleration * relativeDistY);

		System.out.println(sourceXPot);
		System.out.println(sourceYPot);

	}

	@Override
	public void onTriggerCollider(Object object) {
		String className = object.getClass().getSimpleName();
		switch (className) {
		case "Player":
			Player player = (Player) object;
			if (player.equals(this)) {
				System.out.println("ERROR: Colliding with itself");
			}
			break;
		case "Fireball":
			Fireball fireball = (Fireball) object;
			if (fireball.player.equals(this)) {
				break;
			} else {
				this.takeDamage(fireball.damage);
			}
		case "Enemy":
			Enemy enemy = (Enemy) object;
			enemy.currentFrameToDamage++;
			if (enemy.currentFrameToDamage >= enemy.framesToDamage) {
				enemy.currentFrameToDamage = 0;
				this.takeDamage(enemy.damage);
				this.throwBack(enemy);
			}
			break;
		case "Weapon":
			System.out.println("Player Trigger for: Weapon");
			break;
		default:
			((Entity) object).onTriggerCollider(this);
			System.out.println("Player Trigger not implemented for:" + className);
			break;
		}

	}

	@Override
	public void tick() {

		List<Entity> entitiesToCheck = Game.entitiesQuadTree.query(this.collisionBox.range);

		moved = false;
		if (jump) {
			if (this.getIsJumping() == false) {
				this.setJump(false);
				this.setIsJumping(true);
			}
		}

		if (this.isJumping == true) {
			if (zTop == false) {
				jumpCur += jumpSpeed;
			} else if (zTop == true) {
				jumpCur -= jumpSpeed;
			}
			z = jumpCur;
			if (jumpFrames <= jumpCur) {
				zTop = true;
			}
			if (z <= 0 && zTop == true) {
				zTop = false;
				isJumping = false;
			}
		}
		if (this.meleeAttack == null) {

			if (right) {
				applyAcceleration("right", acceleration);
			} else if (left) {
				applyAcceleration("left", acceleration);
			}
			if (up) {
				applyAcceleration("up", acceleration);
			} else if (down) {
				applyAcceleration("down", acceleration);
			}
			CollisionBox nextPositionCollider = new CollisionBox(
					(int) Math.round(this.collisionBox.x + speedHorizontal),
					(int) Math.round(this.collisionBox.y + speedVertical), this.collisionBox.width,
					this.collisionBox.height);
			CollisionBox nextPositionColliderX = new CollisionBox(
					(int) Math.round(this.collisionBox.x + speedHorizontal), this.collisionBox.y,
					this.collisionBox.width, this.collisionBox.height);
			CollisionBox nextPositionColliderY = new CollisionBox(this.collisionBox.x,
					(int) Math.round(this.collisionBox.y + speedVertical), this.collisionBox.width,
					this.collisionBox.height);
			for (int i = 0; i < entitiesToCheck.size(); i++) {
				Entity currentEntity = entitiesToCheck.get(i);
				if (currentEntity.collisionBox.solid
						&& this.isColliding(nextPositionColliderX, currentEntity.collisionBox)) {
					System.out.println("here1");
					nextPositionColliderX = this.collisionBox;
					nextPositionCollider = nextPositionColliderY;
					speedHorizontal = 0;
				}
				
				if (currentEntity.collisionBox.solid
						&& this.isColliding(nextPositionColliderY, currentEntity.collisionBox)) {
					System.out.println("here2");
					nextPositionColliderY = this.collisionBox;
					nextPositionCollider = nextPositionColliderX;
					speedVertical = 0;
				}
				
				if (this.isColliding(this.collisionBox, currentEntity.collisionBox) && !currentEntity.equals(this)) {
					System.out.println("here3");
					currentEntity.onTriggerCollider(this);
				}
				
				
			}
			if (Game.world.isFree(nextPositionCollider)
					|| (!Game.world.isBorder((int) Math.round(x + speedHorizontal), (int) Math.round(y + speedVertical))
							&& this.getIsJumping())) {
				moved = true;
				this.setX((int) Math.round(x + speedHorizontal));
				this.setY((int) Math.round(y + speedVertical));
				this.collisionBox = nextPositionCollider;
			}
			if (moved) {
				Game.world.revealMap(this.getX(), this.getY());
				frames++;
				if (frames >= maxFrames) {
					frames = 0;
					index++;
					if (index == maxIndex) {
						index = 0;
					}
				}
			} else if (!moved) {
				speedVertical = 0;
				speedHorizontal = 0;
				index = 0;
			}
		}

		if (meleeAttacking && this.meleeAttack == null) {
			shoot = false;
			MeleeAttack meleeAttack = new MeleeAttack(this.getX(), this.getY(), 32, 32, null, this.sword.damage,
					this.lastPressedMovementKey, this);
			this.meleeAttack = meleeAttack;
			Game.entities.add(meleeAttack);
			meleeAttacking = false;
		}

		if (this.meleeAttack != null && this.meleeAttack.finished) {
			Game.entities.remove(this.meleeAttack);
			this.meleeAttack = null;
		}

		if (isThrowed) {

		}

		if (shoot && this.meleeAttack == null) {
			shoot = false;
			this.mana -= 2;
			double dx = this.lastPressedMovementKey == "right" ? 0.2 : this.lastPressedMovementKey == "left" ? -0.2 : 0;
			double dy = this.lastPressedMovementKey == "up" ? -0.2 : this.lastPressedMovementKey == "down" ? 0.2 : 0;

			if (right && !left) {
				dx = 1;
			}
			if (left && !right) {
				dx = -1;
			}
			if (up && !down) {
				dy = -1;
			}
			if (down && !up) {
				dy = 1;
			}
			if (dy != 0 && dx != 0) {
				dy = dy / 2;
				dx = dx / 2;
			}
			int px = 12;
			int py = 12;
			Fireball fireball = new Fireball(this.getX() + py, this.getY() + px, 6, 6, null, dx, dy, this.weapon.damage,
					this);
			Game.entities.add(fireball);
			Game.entities.add(fireball);
		}
		if (mouseShoot && this.meleeAttack == null) {
			mouseShoot = false;
			double angle = Math.atan2(this.my - (this.getY() + 16 - Camera.y), this.mx - (this.getX() + 16 - Camera.x));
			this.mana -= 2;
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			int px = 12;
			int py = 12;
			Fireball fireball = new Fireball(this.getX() + py, this.getY() + px, 6, 6, null, dx, dy, this.weapon.damage,
					this);
			Game.entities.add(fireball);
		}

		Game.ui.renderOnMinimap(this.getX() / 32, this.getY() / 32, "player");

		setCamera();

	}

	@Override
	public void applyAcceleration(String direction, double accelerationCur) {
		double maxSpeed = isRunning ? runningMaxSpeed : normalMaxSpeed;
		switch (direction) {
		case "right":
			if (this.lastPressedMovementKey == "left") {
				speedHorizontal = -0.2;
			}
			if (Math.abs(speedHorizontal) < maxSpeed) {
				speedHorizontal += accelerationCur;
			} else {
				speedHorizontal -= accelerationCur;
			}
			this.lastPressedMovementKey = "right";
			break;
		case "left":
			if (this.lastPressedMovementKey == "right") {
				speedHorizontal = 0.2;
			}
			if (Math.abs(speedHorizontal) < maxSpeed) {
				speedHorizontal -= accelerationCur;
			} else {
				speedHorizontal += accelerationCur;
			}

			this.lastPressedMovementKey = "left";
			break;
		case "up":
			if (this.lastPressedMovementKey == "down") {
				speedVertical = -0.2;
			}
			if (Math.abs(speedVertical) < maxSpeed) {
				speedVertical -= accelerationCur;
			} else {
				speedVertical += accelerationCur;
			}
			this.lastPressedMovementKey = "up";
			break;
		case "down":
			if (this.lastPressedMovementKey == "up") {
				speedHorizontal = 0.2;
			}
			if (Math.abs(speedVertical) < maxSpeed) {
				speedVertical += accelerationCur;
			} else {
				speedVertical -= accelerationCur;
			}
			this.lastPressedMovementKey = "down";
			break;
		default:
			break;
		}
	}

	public void setCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.getWIDTH() / 2), 0, Game.world.WIDTH * 32 - Game.getWIDTH());
		Camera.y = Camera.clamp(this.getY() - (Game.getHEIGHT() / 2), 0, Game.world.HEIGHT * 32 - Game.getHEIGHT());
	}

	@Override
	public void render(Graphics g) {
		int positionX = this.getX() - Camera.x;
		int positionY = this.getY() - Camera.y - z;
		if (isJumping) {
			g.setColor(Color.BLACK);
			g.fillOval(positionX + 8, positionY + z + 16, 15, 10);
		}
		switch (lastPressedMovementKey) {
		case "right":
			if (right) {
				g.drawImage(rightPlayer[index], positionX, positionY, null);
			} else {
				g.drawImage(rightPlayer[0], positionX, positionY, null);
			}
			break;
		case "left":
			if (left) {
				g.drawImage(leftPlayer[index], positionX, positionY, null);
			} else {
				g.drawImage(leftPlayer[0], positionX, positionY, null);
			}
			break;
		case "up":
			if (up) {
				g.drawImage(upPlayer[index], positionX, positionY, null);
			} else {
				g.drawImage(upPlayer[0], positionX, positionY, null);
			}
			break;
		case "down":
			if (down) {
				g.drawImage(frontPlayer[index], positionX, positionY, null);
			} else {
				g.drawImage(frontPlayer[0], positionX, positionY, null);
			}
			break;
		default:
			break;
		}
		if (this.weapon instanceof Weapon) {
			Graphics2D g2 = (Graphics2D) g;
			double cy = positionY + 16 - Game.my;
			double cx = positionX + 16 - Game.mx;
			double correctionAngle = Math.toRadians(-135);
			g2.rotate(Math.atan2(cy, cx) + correctionAngle, positionX + 16, positionY + 16);
			g.drawImage(this.weapon.sprite, positionX, positionY, null);
			g2.rotate(-Math.atan2(cy, cx) - correctionAngle, positionX + 16, positionY + 16);
		}

		if (this.meleeAttack != null) {
			meleeAttack.render(g);
		}
		renderCollisionBox(g);
		renderRangeBox(g);
	}

}

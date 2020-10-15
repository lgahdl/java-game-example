package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.histudio.main.Game;
import com.histudio.main.Sound;
import com.histudio.world.Camera;
import com.histudio.world.World;

public class Player extends Entity {

	public boolean right, up, left, down, isUp, isRight = true, isRunning = false;

	public double speed = 1;

	private int frames = 0, maxFrames = 7, index = 0, maxIndex = 7;
	private boolean moved = false;
	private boolean damaged = false;

	private BufferedImage[] frontPlayer;

	private BufferedImage[] leftPlayer;

	private BufferedImage[] rightPlayer;

	private BufferedImage[] upPlayer;

	public BufferedImage[] playerDamaged;

	private double life = 100, maxLife = 100;

	private double mana = 70, maxMana = 100;

	private int damageFrames = 0, maxDamageFrames = 20;

	private Weapon weapon;

	private String lastPressedMovementKey = "right";

	public boolean shoot = false, mouseShoot = false;

	private boolean jump = false, isJumping = false;

	private int jumpFrames = 50, jumpCur = 0, jumpSpeed = 3;
	private int z = 0;
	private boolean zTop = false;

	public int mx, my;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		frontPlayer = new BufferedImage[8];
		leftPlayer = new BufferedImage[8];
		rightPlayer = new BufferedImage[8];
		upPlayer = new BufferedImage[8];
		playerDamaged = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			frontPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 0, 32, 32);
			rightPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 32, 32, 32);
			leftPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 64, 32, 32);
			upPlayer[i] = Game.playerSpritesheet.getSprite(32 * i, 96, 32, 32);
			playerDamaged[i] = Game.playerSpritesheet.getSprite(0, 0, 32, 32);
		}
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

	public void takeDamage(int damage) {
		this.setLife(this.life - damage);
		this.setDamaged(true);
		Sound.playerDamaged.play();
	}

	@Override
	public void tick() {
		moved = false;
		double movingSpeed = speed * (isRunning ? 2 : 1);
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

		if (right) {
			this.lastPressedMovementKey = "right";
			isRight = true;
			if (World.isFree(this.getX() + (int) (movingSpeed), this.getY())
					|| (!World.isBorder(this.getX() + (int) (movingSpeed), this.getY()) && this.getIsJumping())) {
				moved = true;
				x += movingSpeed;
			}
		} else if (left) {
			this.lastPressedMovementKey = "left";
			isRight = false;
			if (World.isFree(this.getX() - (int) (movingSpeed), this.getY())
					|| (!World.isBorder(this.getX() - (int) (movingSpeed), this.getY()) && this.getIsJumping())) {
				moved = true;
				x -= movingSpeed;
			}
		}
		if (up) {
			this.lastPressedMovementKey = "up";
			isUp = true;
			if (World.isFree(this.getX(), this.getY() - (int) (movingSpeed))
					|| (!World.isBorder(this.getX(), this.getY() - (int) (movingSpeed)) && this.getIsJumping())) {
				moved = true;
				y -= movingSpeed;
			}
		} else if (down) {
			this.lastPressedMovementKey = "down";
			isUp = false;
			if (World.isFree(this.getX(), this.getY() + (int) (movingSpeed))
					|| (!World.isBorder(this.getX(), this.getY() + (int) (movingSpeed)) && this.getIsJumping())) {
				moved = true;
				y += movingSpeed;
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
		if (!moved) {
			index = 0;
		}

		if (damaged) {
			this.damageFrames++;
			if (this.damageFrames == maxDamageFrames) {
				this.damageFrames = 0;
				damaged = false;
			}
		}

		if (shoot) {
			shoot = false;
			this.mana -= 2;
			int dx = this.lastPressedMovementKey == "right" ? 1 : this.lastPressedMovementKey == "left" ? -1 : 0;
			int dy = this.lastPressedMovementKey == "up" ? -1 : this.lastPressedMovementKey == "down" ? 1 : 0;

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
			FireballShoot fireball = new FireballShoot(this.getX() + 12, this.getY() + 32, 6, 6, null, dx, dy);
			Game.fireballs.add(fireball);
		}
		if (mouseShoot) {
			mouseShoot = false;
			double angle = Math.atan2(this.my - (this.getY() + 16 - Camera.y), this.mx - (this.getX() + 16 - Camera.x));
			this.mana -= 2;
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
			int px = 12;
			int py = 12;
			FireballShoot fireball = new FireballShoot(this.getX() + py, this.getY() + px, 6, 6, null, dx, dy);
			Game.fireballs.add(fireball);
		}

		setCamera();

	}

	public void setCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 32 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 32 - Game.HEIGHT);
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
				if (this.weapon instanceof Weapon) {
					BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
					g.drawImage(weaponSprites[index < 4 ? index : 0], positionX, positionY, null);
				}
			} else {
				g.drawImage(rightPlayer[0], positionX, positionY, null);
			}
			break;
		case "left":
			if (left) {
				g.drawImage(leftPlayer[index], positionX, positionY, null);
				if (this.weapon instanceof Weapon) {
					BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
					g.drawImage(weaponSprites[index < 4 ? index : 0], positionX, positionY, null);
				}
			} else {
				g.drawImage(leftPlayer[0], positionX, positionY, null);
			}
			break;
		case "up":
			if (up) {
				g.drawImage(upPlayer[index], positionX, positionY, null);
				if (this.weapon instanceof Weapon) {
					BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
					g.drawImage(weaponSprites[index < 4 ? index : 0], positionX, positionY, null);
				}
			} else {
				g.drawImage(upPlayer[0], positionX, positionY, null);
			}
			break;
		case "down":
			if (down) {
				g.drawImage(frontPlayer[index], positionX, positionY, null);
				if (this.weapon instanceof Weapon) {
					BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
					g.drawImage(weaponSprites[index < 4 ? index : (index - index / 2)], positionX, positionY, null);
				}
			} else {
				g.drawImage(frontPlayer[0], positionX, positionY, null);
			}
			break;
		default:
			break;
		}

//		if (!damaged) {
//
//			if (isRight) {
//				if (isUp) {
//					if (right || up) {
//						g.drawImage(rightPlayerBack[index], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
//							g.drawImage(weaponSprites[index], positionX, positionY, null);
//						}
//					} else {
//						g.drawImage(rightPlayerBack[0], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
//							g.drawImage(weaponSprites[0], positionX, positionY, null);
//						}
//					}
//
//				} else {
//					if (right || down) {
//						g.drawImage(rightPlayerFront[index], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getFrontRightSprites();
//							g.drawImage(weaponSprites[index], positionX, positionY, null);
//						}
//					} else {
//						g.drawImage(rightPlayerFront[0], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getFrontRightSprites();
//							g.drawImage(weaponSprites[0], positionX, positionY, null);
//						}
//					}
//				}
//			} else {
//				if (isUp) {
//					if (left || up) {
//						g.drawImage(leftPlayerBack[index], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getBackLeftSprites();
//							g.drawImage(weaponSprites[index], positionX, positionY, null);
//						}
//					} else {
//						g.drawImage(leftPlayerBack[0], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getBackLeftSprites();
//							g.drawImage(weaponSprites[0], positionX, positionY, null);
//						}
//					}
//				} else {
//					if (left || down) {
//						g.drawImage(leftPlayerFront[index], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getFrontLeftSprites();
//							g.drawImage(weaponSprites[index], positionX, positionY, null);
//						}
//					} else {
//						g.drawImage(leftPlayerFront[0], positionX, positionY, null);
//						if (this.weapon instanceof Weapon) {
//							BufferedImage[] weaponSprites = this.weapon.getFrontLeftSprites();
//							g.drawImage(weaponSprites[0], positionX, positionY, null);
//						}
//					}
//				}
//			}
//		} else {
//			if (isRight) {
//				if (isUp) {
//					g.drawImage(playerDamaged[2], positionX, positionY, null);
//					if (this.weapon instanceof Weapon) {
//						BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
//						g.drawImage(weaponSprites[0], positionX, positionY, null);
//					}
//				} else {
//					g.drawImage(playerDamaged[0], positionX, positionY, null);
//					if (this.weapon instanceof Weapon) {
//						BufferedImage[] weaponSprites = this.weapon.getFrontRightSprites();
//						g.drawImage(weaponSprites[0], positionX, positionY, null);
//					}
//				}
//			} else {
//				if (isUp) {
//					g.drawImage(playerDamaged[3], positionX, positionY, null);
//					if (this.weapon instanceof Weapon) {
//						BufferedImage[] weaponSprites = this.weapon.getBackLeftSprites();
//						g.drawImage(weaponSprites[0], positionX, positionY, null);
//					}
//				} else {
//					g.drawImage(playerDamaged[1], positionX, positionY, null);
//					if (this.weapon instanceof Weapon) {
//						BufferedImage[] weaponSprites = this.weapon.getFrontLeftSprites();
//						g.drawImage(weaponSprites[0], positionX, positionY, null);
//					}
//				}
//			}
//		}

	}

}

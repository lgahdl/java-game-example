package com.histudio.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gcstudios.world.Camera;
import com.gcstudios.world.World;
import com.histudio.main.Game;

public class Player extends Entity {

	public boolean right, up, left, down, isUp, isRight = true, isRunning = false;

	public double speed = 1;

	private int frames = 0, maxFrames = 7, index = 0, maxIndex = 4;
	private boolean moved = false;
	private boolean damaged = false;

	private BufferedImage[] rightPlayerFront;

	private BufferedImage[] leftPlayerFront;

	private BufferedImage[] rightPlayerBack;

	private BufferedImage[] leftPlayerBack;

	public BufferedImage[] playerDamaged;

	private double life = 100, maxLife = 100;

	private double mana = 20, maxMana = 100;

	private int damageFrames = 0, maxDamageFrames = 20;

	private Weapon weapon;

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		rightPlayerFront = new BufferedImage[4];
		leftPlayerFront = new BufferedImage[4];
		rightPlayerBack = new BufferedImage[4];
		leftPlayerBack = new BufferedImage[4];
		playerDamaged = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			rightPlayerFront[i] = Game.spritesheet.getSprite(64 + 32 * i, 0, 32, 32);
			leftPlayerFront[i] = Game.spritesheet.getSprite(64 + 32 * i, 32, 32, 32);
			rightPlayerBack[i] = Game.spritesheet.getSprite(64 + 32 * i, 64, 32, 32);
			leftPlayerBack[i] = Game.spritesheet.getSprite(64 + 32 * i, 96, 32, 32);
			playerDamaged[i] = Game.spritesheet.getSprite(64 + 32 * i, 128, 32, 32);
		}
	}

	public double getLife() {
		return this.life;
	}

	public double getMaxLife() {
		return this.maxLife;
	}

	public double getMana() {
		return this.mana;
	}

	public double getMaxMana() {
		return this.maxMana;
	}

	public void setMaxMana(double newMaxMana) {
		this.maxMana = newMaxMana;
	}

	public void setMana(double newMana) {
		if (newMana <= this.maxMana) {
			this.mana = newMana;
		} else {
			this.mana = this.maxMana;
		}
	}

	public void setLife(double newLife) {
		if (newLife <= this.maxLife) {
			this.life = newLife;
		} else {
			this.life = this.maxLife;
		}

	}

	public void setMaxLife(double newMaxLife) {
		this.maxLife = newMaxLife;
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

	public Weapon getWeapon() {
		return this.weapon;
	}

	public void setWeapon(Weapon weapon) {
		this.weapon = weapon;
	}

	@Override
	public void tick() {
		moved = false;
		double movingSpeed = speed * (isRunning ? 2 : 1);
		if (right && World.isFree(this.getX() + (int) (movingSpeed), this.getY())) {
			moved = true;
			isRight = true;
			x += movingSpeed;
		} else if (left && World.isFree(this.getX() - (int) (movingSpeed), this.getY())) {
			moved = true;
			isRight = false;
			x -= movingSpeed;
		}
		if (up && World.isFree(this.getX(), this.getY() - (int) (movingSpeed))) {
			moved = true;
			isUp = true;
			y -= movingSpeed;
		} else if (down && World.isFree(this.getX(), this.getY() + (int) (movingSpeed))) {
			moved = true;
			isUp = false;
			y += movingSpeed;
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

		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 32 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGHT * 32 - Game.HEIGHT);
	}

	@Override
	public void render(Graphics g) {
		int positionX = this.getX() - Camera.x;
		int positionY = this.getY() - Camera.y;
		if (!damaged) {

			if (isRight) {
				if (isUp) {
					if (right || up) {
						g.drawImage(rightPlayerBack[index], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
							g.drawImage(weaponSprites[index], positionX, positionY, null);
						}
					} else {
						g.drawImage(rightPlayerBack[0], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
							g.drawImage(weaponSprites[0], positionX, positionY, null);
						}
					}

				} else {
					if (right || down) {
						g.drawImage(rightPlayerFront[index], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getFrontRightSprites();
							g.drawImage(weaponSprites[index], positionX, positionY, null);
						}
					} else {
						g.drawImage(rightPlayerFront[0], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getFrontRightSprites();
							g.drawImage(weaponSprites[0], positionX, positionY, null);
						}
					}
				}
			} else {
				if (isUp) {
					if (left || up) {
						g.drawImage(leftPlayerBack[index], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getBackLeftSprites();
							g.drawImage(weaponSprites[index], positionX, positionY, null);
						}
					} else {
						g.drawImage(leftPlayerBack[0], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getBackLeftSprites();
							g.drawImage(weaponSprites[0], positionX, positionY, null);
						}
					}
				} else {
					if (left || down) {
						g.drawImage(leftPlayerFront[index], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getFrontLeftSprites();
							g.drawImage(weaponSprites[index], positionX, positionY, null);
						}
					} else {
						g.drawImage(leftPlayerFront[0], positionX, positionY, null);
						if (this.weapon instanceof Weapon) {
							BufferedImage[] weaponSprites = this.weapon.getFrontLeftSprites();
							g.drawImage(weaponSprites[0], positionX, positionY, null);
						}
					}
				}
			}
		} else {
			if (isRight) {
				if (isUp) {
					g.drawImage(playerDamaged[2], positionX, positionY, null);
					if (this.weapon instanceof Weapon) {
						BufferedImage[] weaponSprites = this.weapon.getBackRightSprites();
						g.drawImage(weaponSprites[0], positionX, positionY, null);
					}
				} else {
					g.drawImage(playerDamaged[0], positionX, positionY, null);
					if (this.weapon instanceof Weapon) {
						BufferedImage[] weaponSprites = this.weapon.getFrontRightSprites();
						g.drawImage(weaponSprites[0], positionX, positionY, null);
					}
				}
			} else {
				if (isUp) {
					g.drawImage(playerDamaged[3], positionX, positionY, null);
					if (this.weapon instanceof Weapon) {
						BufferedImage[] weaponSprites = this.weapon.getBackLeftSprites();
						g.drawImage(weaponSprites[0], positionX, positionY, null);
					}
				} else {
					g.drawImage(playerDamaged[1], positionX, positionY, null);
					if (this.weapon instanceof Weapon) {
						BufferedImage[] weaponSprites = this.weapon.getFrontLeftSprites();
						g.drawImage(weaponSprites[0], positionX, positionY, null);
					}
				}
			}
		}

	}

}

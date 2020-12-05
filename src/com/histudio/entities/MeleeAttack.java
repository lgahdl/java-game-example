package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.histudio.main.Game;
import com.histudio.utils.CollisionBox;
import com.histudio.utils.Spritesheet;
import com.histudio.world.Camera;

public class MeleeAttack extends Entity {

	public int damage;
	public String direction;
	public Entity father;
	public int phase = 0, phaseFrames = 0, maxPhases = 4, maxPhaseFrames = 5;
	public int initialPositionX, initialPositionY;
	public boolean finished = false;
	public BufferedImage[] sprites;

	public MeleeAttack(int x, int y, int width, int height, BufferedImage sprite, int damage, String direction,
			Entity father) {

		super(x, y, width, height, sprite);
		this.damage = damage;
		this.direction = direction;
		this.father = father;
		this.sprites = new BufferedImage[4];

		for (int i = 0; i < this.sprites.length; i++) {
			this.sprites[i] = Game.spritesheet.getSprite(192 + i * 32, 192, 32, 32);
		}
		this.sprite = this.sprites[0];
		switch (direction) {
		case "up":
			this.setY(this.getY() - 10);
			this.setX(this.getX() + 13);
			break;
		case "down":
			this.setY(this.getY() + 13);
			this.setX(this.getX() - 10);
			break;
		case "left":
			this.setX(this.getX() - 13);
			this.setY(this.getY() - 10);
			break;
		case "right":
			this.setX(this.getX() + 10);
			this.setY(this.getY() + 10);
			break;
		default:
			break;
		}
		this.initialPositionX = this.getX();
		this.initialPositionY = this.getY();
	}

	public void tick() {
		phaseFrames++;
		if (phaseFrames >= maxPhaseFrames) {
			phase++;
			if (phase == maxPhases) {
				finished = true;
			}
			phaseFrames = 0;
			int horizontalMove = direction == "up" ? -5 : direction == "down" ? 5 : 0;
			int verticalMove = direction == "right" ? -5 : direction == "left" ? 5 : 0;
			switch (phase) {
			case 0:
				this.setX(this.initialPositionX);
				this.setY(this.initialPositionY);
				this.sprite = this.sprites[phase];
				break;
			case 1:
				this.setX(this.getX() + horizontalMove);
				this.setY(this.getY() + verticalMove);
				this.sprite = this.sprites[phase];
				break;
			case 2:
				this.setX(this.getX() + horizontalMove);
				this.setY(this.getY() + verticalMove);
				this.sprite = this.sprites[phase];
				break;
			case 3:
				this.setX(this.getX() + horizontalMove);
				this.setY(this.getY() + verticalMove);
				this.sprite = this.sprites[phase];
				break;
			}
		}
	}

	@Override
	public void render(Graphics g) {
		renderCollisionBox(g);
		Graphics2D g2 = (Graphics2D) g;
		int RelativePositionX = this.getX() - Camera.x;
		int RelativePositionY = this.getY() - Camera.y;
		switch (direction) {
		case "up":
			g2.drawImage(this.sprite, RelativePositionX + this.getWidth(),
					RelativePositionY + this.getHeight(), -this.getWidth(), -this.getHeight(), null);
			break;
		case "down":
			g.drawImage(this.sprite, RelativePositionX, RelativePositionY, null);
			break;
		case "left":
			g2.rotate(Math.PI / 2, RelativePositionX + 16, RelativePositionY + 16);
			g.drawImage(this.sprite, RelativePositionX, RelativePositionY, null);
			g2.rotate(-Math.PI / 2, RelativePositionX + 16, RelativePositionY + 16);
			break;
		case "right":
			g2.rotate(-Math.PI / 2, RelativePositionX + 16, RelativePositionY + 16);
			g.drawImage(this.sprite, RelativePositionX, RelativePositionY, null);
			g2.rotate(Math.PI / 2, RelativePositionX + 16, RelativePositionY + 16);
			break;
		default:
			g.drawImage(this.sprite, RelativePositionX, RelativePositionY, null);
			break;
		}
	}

}

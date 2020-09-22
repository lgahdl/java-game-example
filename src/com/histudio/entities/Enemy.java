package com.histudio.entities;

import java.awt.image.BufferedImage;

import com.gcstudios.world.World;
import com.histudio.main.Game;

public class Enemy extends Entity {

	private double speed = 0.8;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		// TODO Auto-generated constructor stub
	}

	public void tick() {
//		if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY())) {
//			x += speed;
//		} else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY())) {
//			x -= speed;
//		}
//		if (y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed))) {
//			y += speed;
//		} else if (y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed))) {
//			y -= speed;
//		}
	}

}

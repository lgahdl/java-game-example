package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gcstudios.world.Camera;
import com.gcstudios.world.World;
import com.histudio.main.Game;

public class FireballShoot extends Entity {

	private double dx, dy;
	private double speed = 6;
	
	private int time = 80, curTime = 0;

	public int maskx = 12, masky = 12, maskWidth = 8, maskHeight = 8;
	
	public FireballShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
		this.maskWidth = width;
		this.maskHeight = height;
		this.maskx = (World.TILE_SIZE - width)/2;
		this.masky = (World.TILE_SIZE - height)/2;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick() {
		x += dx * speed;
		y += dy * speed;
		curTime++;
		if(curTime == time) {
			Game.fireballs.remove(this);
			return;
		}
	}

	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, this.width, this.height);
	}

}

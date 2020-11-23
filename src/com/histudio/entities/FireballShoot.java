package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.histudio.main.Game;
import com.histudio.world.Camera;
import com.histudio.world.World;

public class FireballShoot extends Entity {

	private double dx, dy;
	private double speed = 8;

	private int time = 1000, curTime = 0;

	public static int maskx = 12;
	public static int masky = 12;
	public static int maskWidth = 8;
	public static int maskHeight = 8;
	
	private int damage=1;

	public FireballShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy, int damage) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
		this.maskWidth = width;
		this.maskHeight = height;
		this.damage = damage;
	}

	@Override
	public void tick() {
		int xNext = (int) (x + (dx * speed));
		int yNext = (int) (y + (dy * speed));
		curTime++;
		x = xNext;
		y = yNext;
		
		
		
		if(!World.isFree(xNext, yNext, maskWidth, maskHeight)) {
			World.generateParticles(200, xNext, yNext);
			Game.fireballs.remove(this);
		}
		
		for(int i = 0; i<Game.enemies.size();i++) {
			if(isColliding(this, Game.enemies.get(i))) {
				Game.enemies.get(i).getHit(this.damage);
				Game.fireballs.remove(this);
			}
		}
		
		if (curTime == time) {
			Game.fireballs.remove(this);
			return;
		}
	}

	public void render(Graphics g) {
		g.setColor(Color.YELLOW);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, this.width, this.height);
	}

}

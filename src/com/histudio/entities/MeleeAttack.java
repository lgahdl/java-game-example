package com.histudio.entities;

import java.awt.image.BufferedImage;

import com.histudio.main.Game;

public class MeleeAttack extends Entity {

	public int damage;
	public String direction;
	public Player player;
	public int phase = 0;

	public MeleeAttack(int x, int y, int width, int height, BufferedImage sprite, int damage, String direction,
			Player player) {

		super(x, y, width, height, sprite);

		this.damage = damage;
		this.direction = direction;
		this.player = player;
		switch(direction) {
			case "up":
				this.setY(this.getY()-10);
				break;
			case "down":
			case "left":
			case "right":
			default:
		}
	}

	public void tick() {
		switch (phase) {
			case 0:
				
			case 1:
				
			case 2:
				
			case 3:
				
		}
	}

}

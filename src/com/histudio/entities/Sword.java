package com.histudio.entities;

import java.awt.image.BufferedImage;

import com.histudio.main.Game;

public class Sword extends Entity{
	
	public int damage;
	
	public boolean equipped = false;
	
	public Sword(int x, int y, int width, int height, BufferedImage sprite, int damage) {
		super(x, y, width, height, sprite);
		// TODO Auto-generated constructor stub
		this.damage = damage;
	}
	
	public void tick() {
		Game.ui.renderOnMinimap(this.getX()/32, this.getY()/32, "weapon");
		if(this.isColliding(this, Game.player)) {
			Game.player.setSword(this);
			this.equipped = true;
			Game.entities.remove(this);
		}
	}
	
}

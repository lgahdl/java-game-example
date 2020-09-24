package com.histudio.entities;

import java.awt.image.BufferedImage;

import com.histudio.main.Game;

public class Manapack extends Entity {

	public static double manaPoints = 20;

	public Manapack(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		// TODO Auto-generated constructor stub
	}

	public void manaRegen(Player player) {
		System.out.println(player.getMana());
		System.out.println(this.manaPoints);
		player.setMana(player.getMana() + this.manaPoints);
	}

	@Override
	public void tick() {
		if (this.isColliding(this, Game.player)) {
			this.manaRegen(Game.player);
			Game.entities.remove(this);
		}
	}
}

package com.histudio.entities;

import java.awt.image.BufferedImage;

import com.histudio.main.Game;

public class Lifepack extends Entity {

	public static int healingPoints = 30;

	public Lifepack(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void tick() {
		Game.ui.renderOnMinimap(this.getX() / 32, this.getY() / 32, "lifepack");
		if (this.isColliding(this, Game.player)) {
			// Game.player.setLife(Game.player.getLife() + healingPoints);
			Game.player.onTriggerCollider(this);

		}
	}

	public void onTriggerCollider(Object object) {
		switch (object.getClass().getSimpleName()) {
		case "Player":
			Player player = (Player) object;
			player.setLife(player.getLife() + healingPoints);
			Game.entities.remove(this);
			break;
		default:
			break;
		}
	}
}

package com.histudio.entities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.world.World;

public class Manapack extends Entity {

	public double manaPoints = 20;

	public Manapack(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
	}

	public void manaRegen(Player player) {
		player.setMana(player.getMana() + this.manaPoints);
	}

	@Override
	public void tick() {
		Game.ui.renderOnMinimap(this.getX() / 32, this.getY() / 32, "manapack");
		if (this.isColliding(this, Game.player)) {
			Game.player.onTriggerCollider(this);
		}
		List<Entity> entitiesToCheck = Game.entitiesQuadTree.query(this.collisionBox.range);
		for (int i = 0; i < entitiesToCheck.size(); i++) {
			Entity e = entitiesToCheck.get(i);
			if (isColliding(this, e)) {
				this.onTriggerCollider(e);
			}
		}
	}

	public void onTriggerCollider(Object object) {
		switch (object.getClass().getSimpleName()) {
		case "Player":
			this.manaRegen(Game.player);
			Game.entities.remove(this);
			break;
		case "Fireball":
			Fireball fireball = (Fireball) object;
			Game.entities.remove(fireball);
			World.generateParticles(200,this.getX(), this.getY(), Color.GRAY);
			break;
		default:
			break;
		}
	}
}

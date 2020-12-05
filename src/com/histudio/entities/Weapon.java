package com.histudio.entities;

import java.awt.image.BufferedImage;

import com.histudio.main.Game;
import com.histudio.world.World;

public class Weapon extends Entity {
	
	private BufferedImage[] frontRightSprites;
	private BufferedImage[] frontLeftSprites;
	private BufferedImage[] backRightSprites;
	private BufferedImage[] backLeftSprites;

	private int frames = 0, maxFrames = 7, index = 0, maxIndex = 4;
	
	private boolean equipped = false;
	
	public int damage;
	
	public Weapon(int x, int y, int width, int height, BufferedImage sprite, int damage) {
		super(x, y, width, height, sprite);
		frontRightSprites = new BufferedImage[4];
		frontLeftSprites = new BufferedImage[4];
		backRightSprites = new BufferedImage[4];
		backLeftSprites = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			frontRightSprites[i] = Game.spritesheet.getSprite(32 + i * 32, 192, 32, 32);
			frontLeftSprites[i] = Game.spritesheet.getSprite(32 + i * 32, 224, 32, 32);
			backRightSprites[i] = Game.spritesheet.getSprite(32 + i * 32, 256, 32, 32);
			backLeftSprites[i] = Game.spritesheet.getSprite(32 + i * 32, 288, 32, 32);
		}
		this.damage = damage;
	}
	
	
	@Override
	public void tick() {
		Game.ui.renderOnMinimap(this.getX()/32, this.getY()/32, "weapon");
	}
	
	public void onTriggerCollider(Object object) {
		String className = object.getClass().getSimpleName();
		switch (className) {
		case "Player":
			Game.player.setWeapon(this);
			this.equipped = true;
			Game.entities.remove(this);
			break;
		default:
			((Entity) object).onTriggerCollider(this);
			System.out.println("Weapon Trigger not configured for:" + className);
			break;
		}
	}
	
	public BufferedImage[] getFrontRightSprites() {
		return this.frontRightSprites;
	}
	
	public BufferedImage[] getFrontLeftSprites() {
		return this.frontLeftSprites;
	}
	
	public BufferedImage[] getBackRightSprites() {
		return this.backRightSprites;
	}
	
	public BufferedImage[] getBackLeftSprites() {
		return this.backLeftSprites;
	}
	
}

package com.histudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.histudio.main.Game;
import com.histudio.utils.CollisionBox;
import com.histudio.utils.QuadTreePoint;
import com.histudio.world.Camera;
import com.histudio.world.World;

public class FireballShoot extends Entity {

	private double dx, dy;
	private double speed = 8;

	private int time = 1000, curTime = 0;

	public int damage = 1;

	public Rectangle range;

	public FireballShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy, int damage) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
		this.collisionBox = new CollisionBox(x, y, width, height);
		this.damage = damage;
		this.range = new Rectangle(x - 200, y - 200, 400, 400);
	}

	@Override
	public void tick() {
		int xNext = (int) (x + (dx * speed));
		int yNext = (int) (y + (dy * speed));
		this.range.x += dx * speed;
		this.range.y += dy * speed;
		curTime++;
		x = xNext;
		y = yNext;
		CollisionBox nextPositionCollider = new CollisionBox(xNext, yNext, this.collisionBox.width,
				this.collisionBox.height);
		if (!Game.world.isFree(nextPositionCollider)) {
			World.generateParticles(200, xNext, yNext);
			Game.fireballs.remove(this);
		}

		List<Entity> entities = Game.entitiesQuadTree.query(this.range);

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (isColliding(this, e)) {
				e.onTriggerCollider(this);
			}
		}

//		for (int i = 0; i < Game.enemies.size(); i++) {
//			if (isColliding(this, Game.enemies.get(i))) {
//				Game.enemies.get(i).getHit(this.damage);
//				Game.fireballs.remove(this);
//			}
//		}

		this.collisionBox = nextPositionCollider;

		if (curTime == time) {
			Game.fireballs.remove(this);
			return;
		}
	}

	public void render(Graphics g) {
		//renderRangeBox(g);
		g.setColor(Color.YELLOW);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, this.width, this.height);
	}

	public void renderRangeBox(Graphics g) {
		g.fillRect(this.range.x - Camera.x, this.range.y - Camera.y, this.range.width, this.range.height);
	}

}

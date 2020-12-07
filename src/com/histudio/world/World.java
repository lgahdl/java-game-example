package com.histudio.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.histudio.entities.*;
import com.histudio.main.Game;
import com.histudio.utils.CollisionBox;

public class World {

	public static Tile[] tiles;
	public int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 32;

	public World(String path) {
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			int[] pixels = new int[WIDTH * HEIGHT];
			tiles = new Tile[pixels.length];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			for (int i = 0; i < map.getWidth(); i++) {
				for (int j = 0; j < map.getHeight(); j++) {
					int pixelToAnalyze = pixels[i + (j * map.getWidth())];
					tiles[i + (j * WIDTH)] = new FloorTile(i * 32, j * 32, Tile.TILE_FLOOR);
					switch (pixelToAnalyze) {
					case 0xFFFFFFFF:
						tiles[i + (j * WIDTH)] = new WallTile(i * 32, j * 32, Tile.TILE_WALL);
						// WALL
						break;
					case 0xFFFF0000: // VERMELHO
						// ENEMY
						Enemy en = new Enemy(i * 32, j * 32, 32, 32, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
						break;
					case 0xFFFFFF00: // AMARELO
						Game.entities.add(new Manapack(i * 32, j * 32, 32, 32, Entity.MANAPACK_EN));
						break;
					case 0xFF00FFFF: // CIANO
						Game.entities.add(new Weapon(i * 32, j * 32, 32, 32, Entity.WEAPON_EN, 1));
						break;
					case 0xFF404040: // GRAFITE
						Game.entities.add(new Sword(i * 32, j * 32, 32, 32, Entity.SWORD_EN, 1));
						break;
					case 0xFF00FF00: // VERDE
						Game.entities.add(new Lifepack(i * 32, j * 32, 32, 32, Entity.LIFEPACK_EN));
						// LIFEPACK
						break;
					case 0xFF0000FF: // AZUL
						// PLAYER
						Game.player.setX(i * 32);
						Game.player.setY(j * 32);
						Game.player.setCollisionBoxPosition(i * 32 + Game.player.collisionXOffset,
								j * 32 + Game.player.collisionYOffset);
						break;
					default:
						tiles[i + (j * WIDTH)] = new FloorTile(i * 32, j * 32, Tile.TILE_FLOOR);
						// FLOOR
						break;
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public World(int width, int height, int pathSize) {
		this.WIDTH = width;
		this.HEIGHT = height;
		tiles = new Tile[this.WIDTH * this.HEIGHT];
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				tiles[i + j * WIDTH] = new WallTile(i * 32, j * 32, Tile.TILE_WALL);
			}
		}
		String[] directions = { "up", "down", "left", "right" };
		int dirIndex = 0; // DIRECTION OF THE PATH MAKER FOR RANDOM MAP
		int xx = 1, yy = 1;
		if (pathSize > width * height - 2 * (width + height)) {
			pathSize = width * height - 2 * (width + height);
		}
		for (int i = 0; i < pathSize; i++) {
			switch (directions[dirIndex]) {
			case "up":
				if (yy > 1) {
					yy--;
					break;
				}
			case "down":
				if (yy < HEIGHT - 2) {
					yy++;
					break;
				}
			case "left":
				if (xx > 1) {
					xx--;
					break;
				}
			case "right":
				if (xx < WIDTH - 2) {
					xx++;
					break;
				}
			default:
				break;
			}
			tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 32, yy * 32, Tile.TILE_FLOOR);

			if (Game.rand.nextInt(100) > 20) {
				dirIndex = Game.rand.nextInt(4);
			}
			if (i % 121 == 0) {
				Game.entities.add(new Manapack(xx * 32, yy * 32, 32, 32, Entity.MANAPACK_EN));
			}
			if (i % 120 == 0) {
				Game.entities.add(new Lifepack(xx * 32, yy * 32, 32, 32, Entity.LIFEPACK_EN));
			}
			if (i % 50 == 0) {
				Enemy en = new Enemy(xx * 32, yy * 32, 32, 32, Entity.ENEMY_EN);
				Game.entities.add(en);
				Game.enemies.add(en);
			}
			if (i == pathSize / 2) {
				Game.entities.add(new Weapon(xx * 32, yy * 32, 32, 32, Entity.WEAPON_EN, 1));
			}
			if (i == pathSize - 1) {
				Game.player.setX(xx * 32);
				Game.player.setY(yy * 32);
				Game.player.setCollisionBoxPosition(xx * 32 + Game.player.collisionXOffset,
						yy * 32 + Game.player.collisionYOffset);
			}
		}

	}

	public static void generateParticles(int amount, int x, int y) {
		for (int i = 0; i < amount; i++) {
			Particle particle = new Particle(x,y,1,1,null);
			Game.entities.add(particle);
		}
	}
	
	public static void generateParticles(int amount, int x, int y, Color color) {
		for (int i = 0; i < amount; i++) {
			Particle particle = new Particle(x,y,1,1,null);
			particle.color = color;
			Game.entities.add(particle);
		}
	}
	
	public boolean isBorder(int xNext, int yNext) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;

		int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;

		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

		int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;
		if (x1 == 0 || y1 == 0 || x2 == this.WIDTH - 1 || y3 == this.HEIGHT - 1) {
			return true;
		}
		return false;
	}

	public boolean isFree(int xNext, int yNext) {

		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;

		int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;

		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

		int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

		return !(tiles[x1 + (y1 * this.WIDTH)] instanceof WallTile
				|| tiles[x2 + (y2 * this.WIDTH)] instanceof WallTile
				|| tiles[x3 + (y3 * this.WIDTH)] instanceof WallTile
				|| tiles[x4 + (y4 * this.WIDTH)] instanceof WallTile);
	}

	public boolean isFree(int xNext, int yNext, int xNegativeOffset, int yNegativeOffset) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;

		int x2 = (xNext + xNegativeOffset - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;

		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + yNegativeOffset - 1) / TILE_SIZE;

		int x4 = (xNext + xNegativeOffset - 1) / TILE_SIZE;
		int y4 = (yNext + yNegativeOffset - 1) / TILE_SIZE;

		return !(tiles[x1 + (y1 * this.WIDTH)] instanceof WallTile
				|| tiles[x2 + (y2 * this.WIDTH)] instanceof WallTile
				|| tiles[x3 + (y3 * this.WIDTH)] instanceof WallTile
				|| tiles[x4 + (y4 * this.WIDTH)] instanceof WallTile);
	}

	public boolean isFree(int xNext, int yNext, int maskOffsetX, int maskOffsetY, int maskWidth,
			int maskHeight) {
		int x1 = (xNext + maskOffsetX) / TILE_SIZE;
		int y1 = (yNext + maskOffsetY) / TILE_SIZE;

		int x2 = (xNext + (maskOffsetX + maskWidth) - 1) / TILE_SIZE;
		int y2 = (yNext + maskOffsetY) / TILE_SIZE;

		int x3 = (xNext + maskOffsetX) / TILE_SIZE;
		int y3 = (yNext + (maskOffsetY + maskHeight) - 1) / TILE_SIZE;

		int x4 = (xNext + (maskOffsetX + maskWidth) - 1) / TILE_SIZE;
		int y4 = (yNext + (maskOffsetY + maskHeight) - 1) / TILE_SIZE;

		return !(tiles[x1 + (y1 * this.WIDTH)] instanceof WallTile
				|| tiles[x2 + (y2 * this.WIDTH)] instanceof WallTile
				|| tiles[x3 + (y3 * this.WIDTH)] instanceof WallTile
				|| tiles[x4 + (y4 * this.WIDTH)] instanceof WallTile);
	}

	public boolean isFree(CollisionBox collisionBox) {

		int collisionBoxInitialX = collisionBox.x;
		int collisionBoxFinalX = collisionBox.x + collisionBox.width;
		int collisionBoxInitialY = collisionBox.y;
		int collisionBoxFinalY = collisionBox.y + collisionBox.height;

		int x1 = (collisionBoxInitialX) / TILE_SIZE;
		int y1 = (collisionBoxInitialY) / TILE_SIZE;

		int x2 = (collisionBoxFinalX - 1) / TILE_SIZE;
		int y2 = (collisionBoxInitialY) / TILE_SIZE;

		int x3 = (collisionBoxInitialX) / TILE_SIZE;
		int y3 = (collisionBoxFinalY - 1) / TILE_SIZE;

		int x4 = (collisionBoxFinalX - 1) / TILE_SIZE;
		int y4 = (collisionBoxFinalY - 1) / TILE_SIZE;

		boolean response = !(tiles[x1 + (y1 * this.WIDTH)] instanceof WallTile
				|| tiles[x2 + (y2 * this.WIDTH)] instanceof WallTile
				|| tiles[x3 + (y3 * this.WIDTH)] instanceof WallTile
				|| tiles[x4 + (y4 * this.WIDTH)] instanceof WallTile);

		return response;
	}

	public void revealMap(int x, int y) {
		int x1 = x / TILE_SIZE;
		int y1 = y / TILE_SIZE;
		int x2 = (x + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = (y + TILE_SIZE - 1) / TILE_SIZE;

		for (int i = x1 - 2; i < x2 + 2; i++) {
			for (int j = y1 - 2; j < y2 + 2; j++) {
				int curTile = i + j * this.WIDTH;
				curTile = curTile > 0 ? curTile < tiles.length ? curTile : 0 : 0;
				tiles[curTile].show = true;

			}
		}

//		tiles[x1 + y1 * World.WIDTH].show = true;
//		tiles[x1 + y2 * World.WIDTH].show = true;
//		tiles[x2 + y1 * World.WIDTH].show = true;
//		tiles[x2 + y2 * World.WIDTH].show = true;
	}

	public void render(Graphics g) {
		int xstart = Camera.x >> 5;
		int ystart = Camera.y >> 5;

		int xfinal = xstart + (Game.getWIDTH() >> 5) + 10;
		int yfinal = ystart + (Game.getHEIGHT() >> 5) + 10;
		for (int i = xstart; i < xfinal; i++) {
			for (int j = ystart; j <= yfinal; j++) {
				if (i < 0 || j < 0 || i >= WIDTH || j >= HEIGHT) {
					continue;
				}
				try {
					Tile tile = tiles[i + (j * WIDTH)];
					tile.render(g);
				} catch (Exception e) {
				}
			}
		}
	}

}

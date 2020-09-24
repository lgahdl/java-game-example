package com.gcstudios.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.histudio.entities.*;
import com.histudio.main.Game;

public class World {

	private static Tile[] tiles;
	public static int WIDTH, HEIGHT;
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
						Game.entities.add(new Weapon(i * 32, j * 32, 32, 32, Entity.WEAPON_EN));
						break;
					case 0xFF00FF00: // VERDE
						Game.entities.add(new Lifepack(i * 32, j * 32, 32, 32, Entity.LIFEPACK_EN));
						// LIFEPACK
						break;
					case 0xFF0000FF: // AZUL
						// PLAYER
						Game.player.setX(i * 32);
						Game.player.setY(j * 32);
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

	public static boolean isFree(int xNext, int yNext) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;

		int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;

		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

		int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

		return !(tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile
				|| tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile
				|| tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile
				|| tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile);
	}

	public void render(Graphics g) {
		int xstart = Camera.x >> 5;
		int ystart = Camera.y >> 5;

		int xfinal = xstart + (Game.WIDTH >> 5) + 10;
		int yfinal = ystart + (Game.HEIGHT >> 5) + 10;
		for (int i = xstart; i < xfinal; i++) {
			for (int j = ystart; j <= yfinal; j++) {
				if (i < 0 || j < 0 || i >= WIDTH || j >= HEIGHT) {
					continue;
				}
				Tile tile = tiles[i + (j * WIDTH)];
				tile.render(g);
			}
		}
	}

}

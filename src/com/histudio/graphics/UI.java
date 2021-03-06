package com.histudio.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.histudio.main.Game;
import com.histudio.world.WallTile;
import com.histudio.world.World;

public class UI {

	private static int fadingFrames = 0, maxFadingFrames = 30;
	private static boolean showPressSpace = false;
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	public Font font;

	public BufferedImage minimap;

	public int[] minimapPixels;

	public Map<String, Integer> minimapColors;

	public UI() {
		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapPixels = ((DataBufferInt) minimap.getRaster().getDataBuffer()).getData();
		minimapColors = new HashMap<String, Integer>();
		setMinimapColors();
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(17f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		this.setWorldMinimapPixels();
	}

	private void setMinimapColors() {
		minimapColors.put("default", 0);
		minimapColors.put("enemy", 0xff0000);
		minimapColors.put("wall", 0xffffff);
		minimapColors.put("player", 0x0000ff);
		minimapColors.put("lifepack", 0xff00ff);
		minimapColors.put("manapack", 0x00ffff);
		minimapColors.put("weapon", 0xffff00);

	}

	public void renderLife(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(5, 5, 100, 15);
		g.setColor(Color.GREEN);
		g.fillRect(5, 5, (int) ((Game.player.getLife() / Game.player.getMaxLife()) * 100), 15);
		g.setColor(Color.WHITE);
		g.drawString("Life: " + (int) ((Game.player.getLife() / Game.player.getMaxLife()) * 100), 10, 19);
	}

	public void renderMana(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(Game.WIDTH - 105, 5, 100, 15);
		g.setColor(Color.BLUE);
		g.fillRect(Game.WIDTH - 105, 5, (int) ((Game.player.getMana() / Game.player.getMaxMana()) * 100), 15);
		g.setColor(Color.WHITE);
		g.drawString("Mana: " + (int) ((Game.player.getMana() / Game.player.getMaxMana()) * 100), Game.WIDTH - 96, 19);
	}

	public void setWorldMinimapPixels() {
		System.out.println("WIDTH: " + World.WIDTH);
		System.out.println("HEIGHT: " + World.HEIGHT);
		for (int i = 0; i < minimapPixels.length; i++) {
			minimapPixels[i] = minimapColors.get("default");
		}
		for (int xx = 0; xx < World.WIDTH; xx++) {
			for (int yy = 0; yy < World.HEIGHT; yy++) {
				if (World.tiles[xx + (yy * World.WIDTH)] instanceof WallTile) {
					minimapPixels[xx + (yy * World.WIDTH)] = minimapColors.get("wall");
				}
			}
		}
	}

	public void renderOnMinimap(int x, int y, String key) {
		minimapPixels[x + (y * World.WIDTH)] = minimapColors.get(key);
	}

	public void renderMinimap(Graphics g) {
		g.drawImage(minimap, 5, Game.HEIGHT - World.WIDTH - 5, World.WIDTH, World.HEIGHT, null);
		setWorldMinimapPixels();
	}

	public void render(Graphics g) {
		g.setFont(font); // 16
		this.renderLife(g);
		this.renderMana(g);
		this.renderMinimap(g);
		if (Game.gameState == "GAMEOVER") {
			this.renderGameOver(g);
		}

		if (Game.gameState == "SAVE") {
			this.renderSaveScreen(g);
		}

	}

	public void renderGameOver(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		g.setColor(Color.RED);
		g.setFont(new Font("arial", Font.BOLD, 42));
		g.drawString("GAME OVER", (Game.WIDTH / 2 - 130), Game.HEIGHT / 2);
		if (showPressSpace) {
			g.setColor(Color.RED);
			g.setFont(new Font("arial", Font.BOLD, 20));
			g.drawString(">press SPACE to restart<", (Game.WIDTH / 2 - 124), Game.HEIGHT / 2 + 40);
		}
		fadingFrames++;
		if (fadingFrames >= maxFadingFrames) {
			fadingFrames = 0;
			showPressSpace = !showPressSpace;
		}
	}

	public void renderSaveScreen(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 12));
		g.drawString("Press X to save or Z to continue without save", (Game.WIDTH / 2 - 130), Game.HEIGHT / 2);
	}
}

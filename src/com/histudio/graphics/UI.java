package com.histudio.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.histudio.main.Game;

public class UI {

	private static int fadingFrames = 0, maxFadingFrames = 30;
	private static boolean showPressSpace = false;

	public UI() {

	}

	public void renderLife(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(5, 5, 100, 15);
		g.setColor(Color.GREEN);
		g.fillRect(5, 5, (int) ((Game.player.getLife() / Game.player.getMaxLife()) * 100), 15);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 16));
		g.drawString("Life: " + (int) ((Game.player.getLife() / Game.player.getMaxLife()) * 100), 20, 18);
	}

	public void renderMana(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(Game.WIDTH - 105, 5, 100, 15);
		g.setColor(Color.BLUE);
		g.fillRect(Game.WIDTH - 105, 5, (int) ((Game.player.getMana() / Game.player.getMaxMana()) * 100), 15);
		g.setColor(Color.WHITE);
		g.setFont(new Font("arial", Font.BOLD, 16));
		g.drawString("Mana: " + (int) ((Game.player.getMana() / Game.player.getMaxMana()) * 100), Game.WIDTH - 92, 18);
	}

	public void render(Graphics g) {
		this.renderLife(g);
		this.renderMana(g);

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

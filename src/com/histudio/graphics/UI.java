package com.histudio.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.histudio.main.Game;

public class UI {

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
	}
}

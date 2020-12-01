package com.histudio.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.histudio.world.World;

public class Menu {

	private static String[] options = { "play", "quit" };

	public static int unitHeight = Game.getHEIGHT() / 100, unitWidth = Game.getWIDTH() / 100;

	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	
	private Font font;

	public Menu() {
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(20f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 100));
		g2.fillRect(0, 0, Game.getWIDTH() * Game.SCALE, Game.getHEIGHT() * Game.SCALE);
		g.setFont(font);

		g.setColor(new Color(135, 135, 100));
		g.fillRect(25 * unitWidth, 35 * unitHeight, 50 * unitWidth, 15 * unitHeight);
		g.setColor(Color.BLACK);
		g.drawString("PLAY", 43 * unitWidth, 47 * unitHeight);

		g.setColor(new Color(135, 135, 100));
		g.fillRect(25 * unitWidth, 55 * unitHeight, 50 * unitWidth, 15 * unitHeight);
		g.setColor(Color.BLACK);
		g.drawString("LOAD", 43 * unitWidth, 67 * unitHeight);

		g.setColor(new Color(135, 135, 100));
		g.fillRect(25 * unitWidth, 75 * unitHeight, 50 * unitWidth, 15 * unitHeight);
		g.setColor(Color.BLACK);
		g.drawString("QUIT", 43 * unitWidth, 87 * unitHeight);

		return;
	}

	public void onClick(int x, int y) {
		x = x / Game.SCALE;
		y = y / Game.SCALE;
		if (x > 25 * unitWidth && x < 75 * unitWidth) {
			if (y > 35 * unitHeight && y < 50 * unitHeight) {
				this.onClickPlay();
			} else if (y > 55 * unitHeight && y < 70 * unitHeight) {
				this.onClickLoad();
			} else if (y > 75 * unitHeight && y < 90 * unitHeight) {
				this.onClickQuit();
			}
		}
	}

	private void onClickPlay() {
		// TODO Auto-generated method stub
		Game.gameState = "PLAYING";
	}

	private void onClickQuit() {
		Game.exitGame();
	}

	public void onClickLoad() {
		File file = new File("save.txt");
		if (file.exists()) {
			String saver = loadGame(-10);
			applySave(saver);
		}
	}

	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("save.txt"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < val1.length; i++) {
			String current = val1[i];
			current += ":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			for (int n = 0; n < value.length; n++) {
				value[n] += encode;
				current += value[n];
			}
			try {
				writer.write(current);
				if (i < val1.length - 1) {
					writer.newLine();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		if (file.exists()) {
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					while ((singleLine = reader.readLine()) != null) {
						String[] transition = singleLine.split(":");
						char[] val = transition[1].toCharArray();
						transition[1] = "";
						for (int i = 0; i < val.length; i++) {
							val[i] -= encode;
							transition[1] += val[i];
						}
						line += transition[0];
						line += ":";
						line += transition[1];
						line += "/";
					}
				} catch (IOException e) {

				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return line;
	}

	public static void applySave(String str) {
		String[] spl = str.split("/");
		for (int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch (spl2[0]) {
			case "level":
				Game.startNewLevel("/map" + spl2[1] + ".png");
				break;
			case "life":
				Game.player.setLife((double) Integer.parseInt(spl2[1]));
				break;
			}
		}
	}

}

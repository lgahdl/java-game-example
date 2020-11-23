package com.histudio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.histudio.entities.Entity;

public class GraphicsManager extends Canvas {

	private static final long serialVersionUID = 1L;

	public static JFrame frame;

	private boolean isRunning;

	public static String displayMode = "WINDOW"; // FULLSCREEN or WINDOW

	public static int WIDTH = 420;

	public static int HEIGHT = 237;

	public static int selectedScreen = 0;

	public final static int SCALE = 3;

	private static BufferedImage image;

	public static GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

	public Graphics g;

	public BufferStrategy bs;

	public int[] pixels;

	public int[] lightMap;

	public GraphicsManager() {
		initFrame();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.g = image.getGraphics();
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		return;
	}

	private void initFrame() {
		frame = new JFrame("Game");
		frame.add(this);
		setMouseIcon(frame);
		// FULLSCREEN
		if (displayMode == "FULLSCREEN") {
			showOnScreen(selectedScreen, frame);
		}
		// WINDOW MODE
		else if (displayMode == "WINDOW") {
			setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
	}

	private void setMouseIcon(JFrame frame) {
		Image mouseImage = null;
		try {
			mouseImage = ImageIO.read(getClass().getResource("/mouseimage.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage(getClass().getResource("/mouseimage.png"));
		Cursor c = toolkit.createCustomCursor(image, new Point(0, 0), "img");
		frame.setCursor(c);
		frame.setIconImage(mouseImage);
		frame.setAlwaysOnTop(true);
	}

	public void showOnScreen(int screen, JFrame frame) {
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		setPreferredSize(screenSize);
		GraphicsConfiguration[] screenConfig = screenDevices[screen].getConfigurations();
		System.out.println("width:" + screenConfig[0].getBounds().getWidth());
		System.out.println("height:" + screenConfig[0].getBounds().getHeight());
		int width = (int) screenConfig[0].getBounds().getWidth();
		int height = (int) screenConfig[0].getBounds().getHeight();
		setPreferredSize(new Dimension(width, height));
		WIDTH = width / SCALE;
		HEIGHT = height / SCALE;
		Game.WIDTH = WIDTH;
		Game.HEIGHT = HEIGHT;
		// frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (screen > -1 && screen < screenDevices.length) {
			screenDevices[screen].setFullScreenWindow(frame);
		} else if (screenDevices.length > 0) {
			screenDevices[0].setFullScreenWindow(frame);
		} else {
			throw new RuntimeException("No Screens Found");
		}
	}

	public void render() {
		// BUFFERSTRATEGY
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = image.getGraphics();
		// *****
		// Graphics2D g2 = (Graphics2D) g;
		g.setColor(new Color(25, 10, 100));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		/* RENDERIZANDO DO JOGO */

		Game.world.render(g);
		Collections.sort(Game.entities, Entity.depthSorter);
		for (int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < Game.fireballs.size(); i++) {
			Game.fireballs.get(i).render(g);
		}
		if (Game.gameState != "WAIT") { // solving renderMinimap conflict;
			Game.ui.render(g);
		}

		if (Game.gameState == "MENU") {
			Game.menu.render(g);
		}
//		Graphics2D g2d = (Graphics2D) g;
//		double angleMouse = Math.atan2(100+25-my, 100+25-mx);
//		g2d.rotate(angleMouse,100+25,100+25);
//		g.setColor(Color.RED);
//		g.fillRect(100, 100, 50, 50);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		// drawRectangleExample(40, 40, 64, 64);
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		bs.show();
	}

}

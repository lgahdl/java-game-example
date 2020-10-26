package com.histudio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.Toolkit;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.histudio.entities.Enemy;
import com.histudio.entities.Entity;
import com.histudio.entities.FireballShoot;
import com.histudio.entities.Player;
import com.histudio.graphics.Spritesheet;
import com.histudio.graphics.UI;
import com.histudio.world.Camera;
import com.histudio.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning;
	public static String displayMode = "WINDOW"; // FULLSCREEN or WINDOW
	public static int WIDTH = displayMode == "FULLSCREEN" ? 1536 / 3 : 420;
	public static int HEIGHT = displayMode == "FULLSCREEN" ? 864 / 3 : 237;
	public final static int SCALE = 3;

	private static BufferedImage image;

	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<FireballShoot> fireballs;

	public static Spritesheet spritesheet;

	public static Spritesheet playerSpritesheet;

	public static World world;

	public static Player player;

	public static Random rand;

	public static UI ui;

	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");

	public Font newFont;

	public static Menu menu;

	public static String gameState = "MENU";

	public static int curLevel = 1, maxLevel = 2;

	public static int mx, my;

	public int[] pixels;

	public Game() {
		// Sound.musicBackground.loop();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		double screenHeight = screenSize.getHeight();
//		double screenWidth = screenSize.getWidth();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		initFrame();

		// Inicializando Objetos
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		fireballs = new ArrayList<FireballShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		playerSpritesheet = new Spritesheet("/player.png");
		player = new Player(0, 0, 32, 32, spritesheet.getSprite(64, 0, 32, 32));
		entities.add(player);
		world = new World("/map" + curLevel + ".png");
		rand = new Random();
		ui = new UI();
		menu = new Menu();
		try {
			newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(20f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		// MOVE THE CAMERA TO THE RIGHT POSITION
		player.setCamera();
	}

	private void initFrame() {
		// FULLSCREEN
		if (displayMode == "FULLSCREEN") {

			frame = new JFrame("Game");
			frame.add(this);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setUndecorated(true);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			showOnScreen(0, frame);
		}
		// WINDOW MODE
		else if (displayMode == "WINDOW") {
			setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
			frame = new JFrame("Game");
			frame.add(this);
			frame.setResizable(false);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		}
	}

	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {

		isRunning = false;

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	public void tick() {
		if (gameState == "PLAYING") {

			if (this.player.getLife() <= 0) {
				this.gameState = "GAMEOVER";
			}

			if (enemies.size() == 0) {
				curLevel++;
				if (curLevel > maxLevel) {
					curLevel = 1;
				}
				this.gameState = "SAVE";
			}

			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				if (e instanceof Player) {
				}
				e.tick();
			}

			for (int i = 0; i < fireballs.size(); i++) {
				fireballs.get(i).tick();
			}
		} else if (gameState == "GAMEOVER") {
		}
	}

	public static void startNewLevel(String path) {
		gameState = "WAIT";

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		fireballs = new ArrayList<FireballShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		// player = new Player(0, 0, 32, 32, spritesheet.getSprite(64, 0, 32, 32));
		entities.add(player);
		world = new World(path);
		rand = new Random();
		ui = new UI();
		gameState = "PLAYING";
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = image.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(new Color(25, 10, 100));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		/* RENDERIZANDO DO JOGO */

		world.render(g);
		Collections.sort(entities, Entity.depthSorter);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < fireballs.size(); i++) {
			fireballs.get(i).render(g);
		}
		if(gameState!="WAIT") {
			ui.render(g);			
		}

		if (gameState == "MENU") {
			menu.render(g);
		}
//		Graphics2D g2d = (Graphics2D) g;
//		double angleMouse = Math.atan2(100+25-my, 100+25-mx);
//		g2d.rotate(angleMouse,100+25,100+25);
//		g.setColor(Color.RED);
//		g.fillRect(100, 100, 50, 50);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		drawRectangleExample(40, 40, 1, 1);
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		bs.show();
	}

	public void drawRectangleExample(int offsetX, int offsetY, int width, int height) {
		for (int xx = 0; xx < width; xx++) {
			for (int yy = 0; yy < height; yy++) {
				int xOff = xx + offsetX;
				int yOff = yy + offsetY;
				if (xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT)
					continue;
				pixels[xOff + (yOff * WIDTH)] = 0xff0000;
			}
		}

	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				frames = 0;
				timer += 1000;
			}
		}

		stop();
	}

	public static void exitGame() {
		System.exit(1);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (this.gameState == "PLAYING") {

			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				player.right = true;
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				player.left = true;
			}

			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
				player.up = true;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
				player.down = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				player.isRunning = true;
			}
			if (e.getKeyCode() == KeyEvent.VK_K) {
				player.setShoot(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				Game.player.setJump(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.gameState = "MENU";
			}
			return;
		}
		if (this.gameState == "SAVE") {
			Game.player.right = false;
			Game.player.up = false;
			Game.player.left = false;
			Game.player.down = false;
			if (e.getKeyCode() == KeyEvent.VK_X) {
				this.saveGame();
				this.startNewLevel("/map" + curLevel + ".png");
			} else if (e.getKeyCode() == KeyEvent.VK_Z) {
				this.startNewLevel("/map" + curLevel + ".png");
			}
			return;
		}
		if (this.gameState == "MENU") {
			Game.player.right = false;
			Game.player.up = false;
			Game.player.left = false;
			Game.player.down = false;
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				this.gameState = "PLAYING";
			}
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (this.gameState == "PLAYING") {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				player.right = false;
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				player.left = false;
			}

			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
				player.up = false;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
				player.down = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				player.isRunning = false;
			}
		}
		if (this.gameState == "GAMEOVER") {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				player.setLife(100);
				player.setMana(70);
				startNewLevel("/map1.png");
				return;
			}
		}

	}

	public static void showOnScreen(int screen, JFrame frame) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if (screen > -1 && screen < gs.length) {
			gs[screen].setFullScreenWindow(frame);
		} else if (gs.length > 0) {
			gs[0].setFullScreenWindow(frame);
		} else {
			throw new RuntimeException("No Screens Found");
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
		if (gameState == "PLAYING") {
			player.setMouseShoot(true);
		} else if (gameState == "MENU") {
			menu.onClick(e.getX(), e.getY());
		}

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private void saveGame() {
		String[] opt1 = { "life", "level" };
		int[] opt2 = { (int) (Game.player.getLife()), this.curLevel };

		Menu.saveGame(opt1, opt2, -10);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		this.mx = e.getX() / SCALE;
		this.my = e.getY() / SCALE;
	}
}

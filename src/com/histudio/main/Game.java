package com.histudio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
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

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.histudio.entities.Enemy;
import com.histudio.entities.Entity;
import com.histudio.entities.Fireball;
import com.histudio.entities.MeleeAttack;
import com.histudio.entities.Npc;
import com.histudio.entities.Player;
import com.histudio.utils.QuadTree;
import com.histudio.utils.Spritesheet;
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
	public static int WIDTH = 420;
	public static int HEIGHT = 237;

	public static int selectedScreen = 0;

	public final static int SCALE = 3;

	private static BufferedImage image;

	public static List<Entity> entities;

	public static QuadTree entitiesQuadTree;

	public static List<Enemy> enemies;

	public static List<Fireball> fireballs;

	public static Spritesheet spritesheet;

	public static Spritesheet playerSpritesheet;

	public static World world;

	public static Player player;

	public static Random rand;

	public static UI ui;

	public static GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");

	public Font newFont;

	public static Menu menu;

	public static String gameState = "MENU";

	public static int curLevel = 1, maxLevel = 2;

	public static int mx, my;

	public int[] pixels;

	public int[] lightMap;

	public Npc npc;

	public Game() {
		// Sound.music.loop();

//		double screenHeight = screenSize.getHeight();
//		double screenWidth = screenSize.getWidth();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		initFrame();

		// Inicializando Objetos
		rand = new Random();
		lightMap = new int[WIDTH * HEIGHT];
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		fireballs = new ArrayList<Fireball>();
		spritesheet = new Spritesheet("/spritesheet.png");
		playerSpritesheet = new Spritesheet("/player.png");
		player = new Player(0, 0, 32, 32, spritesheet.getSprite(64, 0, 32, 32));
		entities.add(player);
		world = new World("/map" + curLevel + ".png");
//		world = new World(100, 100, 25000);
		ui = new UI();
		menu = new Menu();
		npc = new Npc(128, 128, 32, 32, spritesheet.getSprite(64, 0, 32, 32));
		entities.add(npc);
		try {
			newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(20f);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		// MOVE THE CAMERA TO THE RIGHT POSITION
		player.setCamera();
	}

	public void setWIDTH(int newWIDTH) {
		WIDTH = newWIDTH;
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		lightMap = new int[WIDTH * HEIGHT];
	}

	public void setHEIGHT(int newHEIGHT) {
		HEIGHT = newHEIGHT;
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		lightMap = new int[WIDTH * HEIGHT];
	}

	public static int getWIDTH() {
		return WIDTH;
	}

	public static int getHEIGHT() {
		return HEIGHT;
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
			entitiesQuadTree = new QuadTree(
					new Rectangle(0, 0, world.WIDTH*32, world.HEIGHT*32), 1);
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				entitiesQuadTree.insert(e);
			}
			if (player.getLife() <= 0) {
				gameState = "GAMEOVER";
			}

			if (enemies.size() == 0) {
				curLevel++;
				if (curLevel > maxLevel) {
					curLevel = 1;
				}
				gameState = "SAVE";
			}
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
		} else if (gameState == "GAMEOVER") {
			
		}
		ui.tick();
	}

	public static void startNewLevel(String path) {
		gameState = "WAIT";
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		player.meleeAttack=null;
		enemies = new ArrayList<Enemy>();
		fireballs = new ArrayList<Fireball>();
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
		// Graphics2D g2 = (Graphics2D) g;
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
		if (gameState != "WAIT") { // solving renderMinimap conflict;
			ui.render(g);
		}

		if (gameState == "MENU") {
			menu.render(g);
		}
		if(entitiesQuadTree!=null) {
			entitiesQuadTree.render(g);			
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

	public void applyLight() {
		for (int xx = 0; xx < WIDTH; xx++) {
			for (int yy = 0; yy < HEIGHT; yy++) {
				if (lightMap[xx + yy * WIDTH] == 0xFF000000) {

				}
			}
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		@SuppressWarnings("unused")
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
//				System.out.println(frames);
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
		if (gameState == "PLAYING") {

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
			if (e.getKeyCode() == KeyEvent.VK_J) {
				player.swordAttack();
			}

			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				Game.player.setJump(true);
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = "MENU";
			}
			if (ui.dialogue && e.getKeyCode() == KeyEvent.VK_ENTER) {
				ui.dialogue = false;
				ui.dialogueClosed = true;
			}
			return;
		}
		if (gameState == "SAVE") {
			Game.player.right = false;
			Game.player.up = false;
			Game.player.left = false;
			Game.player.down = false;
			if (e.getKeyCode() == KeyEvent.VK_X) {
				this.saveGame();
				startNewLevel("/map" + curLevel + ".png");
			} else if (e.getKeyCode() == KeyEvent.VK_Z) {
				startNewLevel("/map" + curLevel + ".png");
			}
			return;
		}
		if (gameState == "MENU") {
			Game.player.right = false;
			Game.player.up = false;
			Game.player.left = false;
			Game.player.down = false;
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = "PLAYING";
			}
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (gameState == "PLAYING") {
			if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				player.right = false;
				player.speedHorizontal=0;
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				player.left = false;
				player.speedHorizontal=0;
			}

			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
				player.up = false;
				player.speedVertical=0;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
				player.down = false;
				player.speedVertical=0;
			}
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				player.isRunning = false;
			}
		}
		if (gameState == "GAMEOVER") {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				player.setLife(100);
				player.setMana(70);
				startNewLevel("/map1.png");
				return;
			}
		}

	}

	public void showOnScreen(int screen, JFrame frame) {
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		setPreferredSize(screenSize);
		GraphicsConfiguration[] screenConfig = screenDevices[screen].getConfigurations();
		int width = (int) screenConfig[0].getBounds().getWidth();
		int height = (int) screenConfig[0].getBounds().getHeight();
		setPreferredSize(new Dimension(width, height));
		setWIDTH(width / SCALE);
		setHEIGHT(height / SCALE);
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
		int[] opt2 = { (int) (Game.player.getLife()), curLevel };

		Menu.saveGame(opt1, opt2, -10);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		mx = e.getX() / SCALE;
		my = e.getY() / SCALE;
	}

}

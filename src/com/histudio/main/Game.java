package com.histudio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.gcstudios.world.Camera;
import com.gcstudios.world.World;
import com.histudio.entities.Enemy;
import com.histudio.entities.Entity;
import com.histudio.entities.FireballShoot;
import com.histudio.entities.Player;
import com.histudio.entities.Weapon;
import com.histudio.graphics.Spritesheet;
import com.histudio.graphics.UI;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning;
	public static int WIDTH = 500;
	public static int HEIGHT = 250;
	public final static int SCALE = 3;

	private BufferedImage image;

	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<FireballShoot> fireballs;

	public static Spritesheet spritesheet;

	public static World world;

	public static Player player;

	public static Random rand;

	public static UI ui;

	public static String gameState = "PLAYING";

	public Game() {
		addKeyListener(this);
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();

		// Inicializando Objetos

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		fireballs = new ArrayList<FireballShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 32, 32, spritesheet.getSprite(64, 0, 32, 32));
		entities.add(player);
		world = new World("/map.png");
		rand = new Random();
		ui = new UI();
	}

	private void initFrame() {
		frame = new JFrame("Game");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
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
				this.startNewLevel("/map2.png");
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
		}
		else if(gameState=="GAMEOVER") {
			
		}
	}

	public void startNewLevel(String path) {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		fireballs = new ArrayList<FireballShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 32, 32, spritesheet.getSprite(64, 0, 32, 32));
		entities.add(player);
		world = new World(path);
		rand = new Random();
		ui = new UI();
	}

	public void render() {
		BufferStrategy bs = this.getBufferStrategy();

		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		Graphics g = image.getGraphics();
		g.setColor(new Color(25, 10, 100));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		/* RENDERIZANDO DO JOGO */
		world.render(g);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < fireballs.size(); i++) {
			fireballs.get(i).render(g);
		}
		ui.render(g);
		/***/
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		if (gameState == "GAMEOVER") {

		}
		bs.show();
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
//				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}

		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
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
	}

	@Override
	public void keyReleased(KeyEvent e) {
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
		player.setMouseShoot(true);

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
}

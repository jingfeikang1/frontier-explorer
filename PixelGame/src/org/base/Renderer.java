package org.base;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.constants.Constants;
import org.game.Game;
import org.game.Library;

public class Renderer {

	private static Frame frame;
	public static Canvas canvas;

	public static int canvasWidth = 0;
	public static int canvasHeight = 0;

	public static int gameWidth = 0;
	public static int gameHeight = 0;

	private static long lastFpsCheck = 0;
	private static int currentFps = 0;
	private static int totalFrames = 0;

	public Renderer() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unused")
	private static void getBestSize() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		boolean done = false;
		while (!done) {
			canvasWidth += Constants.GAME_WIDTH;
			canvasHeight += Constants.GAME_HEIGHT;

			if (canvasWidth > screenSize.width || canvasHeight > screenSize.height) {
				canvasWidth -= Constants.GAME_WIDTH;
				canvasHeight -= Constants.GAME_HEIGHT;
				done = true;
			}
		}

		int xDiff = screenSize.width - canvasWidth;
		int yDiff = screenSize.height - canvasHeight;
		int factor = canvasWidth / Constants.GAME_WIDTH;

		canvasWidth = Constants.GAME_WIDTH * factor + xDiff;
		canvasHeight = Constants.GAME_HEIGHT * factor + yDiff;

		gameWidth = canvasWidth / factor;
		gameHeight = canvasHeight / factor;
	}

	private static void makeFullscreen() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = env.getDefaultScreenDevice();

		if (gd.isFullScreenSupported()) {
			frame.setUndecorated(true);
			gd.setFullScreenWindow(frame);
		}
	}

	public static void init() {
		// getBestSize();
		gameWidth = 600;
		gameHeight = 600;
		canvasHeight = 600;
		canvasWidth = 600;

		frame = new Frame();
		canvas = new Canvas();

		canvas.setPreferredSize(new Dimension(canvasWidth, canvasHeight));

		frame.add(canvas);

		// makeFullscreen();

		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Game.quit();
			}
		});

		frame.setVisible(true);

		canvas.addKeyListener(new KeyInput());
		canvas.addMouseListener(new MouseInput());
		MouseInput.init();

		startRendering();
	}

	private static void startRendering() {
		Thread thread = new Thread() {
			private static long lastTime = 0;
			private static float deltaTime = 0;
			public void run() {
				lastTime = System.nanoTime();
				updateFPS();

				GraphicsConfiguration gc = canvas.getGraphicsConfiguration();
				VolatileImage vImage = gc.createCompatibleVolatileImage(gameWidth, gameHeight);

				while (true) {
					lastTime = System.nanoTime();
					updateFPS();
					
					// Create graphics
					if (vImage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
						vImage = gc.createCompatibleVolatileImage(gameWidth, gameHeight);
					}

					Graphics2D g2d = vImage.createGraphics();
					g2d.setColor(Color.black);
					g2d.fillRect(0, 0, gameWidth, gameHeight);

					// RENDER STUFF
					Game.update(g2d, deltaTime);
					KeyInput.updateDebounce();
					MouseInput.updateMouseData();

					// Draw FPS counter
					g2d.setColor(Color.LIGHT_GRAY);
					g2d.setFont(Library.font1.deriveFont(40.0f));
					g2d.drawString(String.valueOf(currentFps), 2, gameHeight - 2);

					g2d.dispose();

					Graphics g = canvas.getGraphics();
					g.drawImage(vImage, 0, 0, canvasWidth, canvasHeight, null);

					g.dispose();

					deltaTime = (System.nanoTime()-lastTime) / 1000000000.0f;
				}
			}

			private void updateFPS() {
				totalFrames++;
				if (System.nanoTime() - 1000000000 > lastFpsCheck) {
					lastFpsCheck = System.nanoTime();
					currentFps = totalFrames;
					totalFrames = 0;
				}
			}
		};
		thread.setName("Rendering Thread");
		thread.start();
	}

	public static BufferedImage loadImage(String path) throws IOException {
		BufferedImage rawImage = ImageIO.read(Renderer.class.getResource(path));
		BufferedImage finalImage = canvas.getGraphicsConfiguration().createCompatibleImage(rawImage.getWidth(),
				rawImage.getHeight(), rawImage.getTransparency());

		finalImage.getGraphics().drawImage(rawImage, 0, 0, rawImage.getWidth(), rawImage.getHeight(), null);

		return finalImage;
	}

}

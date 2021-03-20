import java.awt.Font;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.ResourceLoader;

public class StarfighterMain extends Scene {
	private static final int TARGET_FPS = 60;
	private TrueTypeFont font, other;
	private Font awtFont, awtFont2;
	private long currTime;
	private int score;
	private MouseSprite game;
	private List<Entity> eProjectiles;
	private List<Entity> projectiles;
	private List<Enemy> enemyShips;
	private int health, enemyCount, enemiesKilled, totalEnemies;
	private float projSpeed;
	private double rateOfFire;
	private boolean newRow;
	private Audio modStream;

	public StarfighterMain(List<Entity> projectiles, List<Entity> eProjectiles, List<Enemy> enemyShips) {
		this.projectiles = projectiles;
		this.eProjectiles = eProjectiles;

		this.enemyShips = enemyShips;
		awtFont = new Font("Times New Roman", Font.BOLD, 40);
		awtFont2 = new Font("Times New Roman", Font.BOLD, 50);
		font = new TrueTypeFont(awtFont, true);
		other = new TrueTypeFont(awtFont2, true);

		health = 100;
		enemyCount = 5;
		projSpeed = .8f;
		newRow = false;
		rateOfFire = 3000;

	}

	public boolean loseCondition() {
		Mouse.setGrabbed(true);
		Menu loseMenu = new Menu();
		List<Entity> projectiles = new LinkedList<>();
		List<Entity> eProjectiles = new LinkedList<>();

		List<Enemy> enemyShips = new LinkedList<>();
		loseMenu.addItem("Play Again", new StarfighterMain(projectiles, eProjectiles, enemyShips));
		loseMenu.addItem("Main Menu", null);
		loseMenu.setFPS(60);
		Scene loseScene = loseMenu;

		while (loseScene.go()) {
			loseScene = loseScene.nextScene();

			if (loseScene == null) {
				return false;
			}
		}
		return false;
	}

	public void addKill() {
		enemiesKilled++;
	}

	public void waveManager(int enemyCount, MouseSprite e) {
		for (int i = 0; i < enemyCount; i++) {
			enemyShips.add(new Enemy(50, 100 + 500 * i, 100, eProjectiles, e.getRectangle(), 1, projectiles,
					rateOfFire, 20, currTime, e, projSpeed));
			totalEnemies++;
			if (enemyCount % 9 == 0 || newRow == true) {
				totalEnemies = totalEnemies + 2;
				newRow = true;
				enemyShips.add(new Enemy(50, 500 * i, 500, eProjectiles, e.getRectangle(), 1, projectiles, rateOfFire,
						20, currTime, e, projSpeed));
				enemyShips.add(new Enemy(50, 300 * i, 500, eProjectiles, e.getRectangle(), 1, projectiles, rateOfFire,
						20, currTime, e, projSpeed));
			}
		}
	}

	public boolean drawFrame(float delta) {
		// TODO Auto-generated method stub

		boolean killConfirm = false;

		newRow = false;
		boolean start = true;
		Mouse.setGrabbed(true);
		// score=0;
		rateOfFire = 3000;
		boolean startCount = false;
		projSpeed = .8f;
		enemyCount = 5;
		enemiesKilled = 0;
		totalEnemies = 0;
		int waveCount = 1;
		game = new MouseSprite(100, projectiles, eProjectiles, enemyShips, 100, 3, 5);
		waveManager(enemyCount, game);
		// enemyShips.add(new Enemy(50, 100+100, 100, eProjectiles, game.getRectangle(),
		// 1, projectiles, 3000, 20, currTime, game));
		Background back = new Background(Display.getWidth());
		Crosshair crosshair = new Crosshair(50);

		long time = (Sys.getTime() * 1000) / Sys.getTimerResolution(); // ms
		currTime = time;
		long testTime, testTime2, testTime3 = 0;
		testTime = time;
		try {
			modStream = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/bossmusic.wav"));
			// modStream.playAsMusic(1.0f, 1.0f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		modStream.playAsMusic(1.0f, 1.0f, true);

		while (!Display.isCloseRequested()) {

			long time2 = (Sys.getTime() * 1000) / Sys.getTimerResolution(); // ms
			int delta1 = (int) (time2 - time);

			// System.out.println(delta);
			if (!game.getHasDied()) {
				if (game.testCollision(game.geteProjectiles()) && game.getHealth() > 0) {
					game.setHealth(game.getHealth() - 20);
				}
			}
			if (!game.getHasDied()) {
				game.update(delta1, start);
			}

			if (game.getHealth() <= 0) {
				TextureImpl.bindNone();
				other.drawString(Display.getWidth() * 1 / 2, Display.getHeight() * 1 / 2, "YOU LOSE");
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				if (game.getHasDied() == false) {
					testTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();
					game.playDeathSound();
				}
				game.setHasDied(true);

				testTime2 = (Sys.getTime() * 1000) / Sys.getTimerResolution();
				if (testTime2 - testTime > 3000) {
					enemyShips.removeAll(enemyShips);
					modStream.stop();
					return false;
				}

			}

			for (Entity p : game.projectiles) {
				p.update(delta1 / 8);
			}

			for (Enemy k : game.getEnemyShips()) {

				if (k.getHealth() > 0) {
					k.update(delta1, start);

				}
				if (k.getStatus() && !k.getIgnore()) {
					game.setScore(game.getScore() + 5);
					game.setEnemiesKilled();
					k.setIgnore(true);
					killConfirm = true;
				}
				k.updateEnemy(delta1);

			}
			if (game.getScore() != 0) {
				if (game.getEnemiesKilled() == totalEnemies) {
					start = false;
					startCount = true;
					game.setPosition(Display.getWidth() / 2, Display.getHeight() / 2);
					projSpeed = (float) (projSpeed * 1.2);
					rateOfFire = rateOfFire * .9;
					waveManager(enemyCount, game);
					enemyCount += 2;
					game.geteProjectiles().removeAll(eProjectiles);
					game.getProjectiles().removeAll(projectiles);

					waveCount++;

				}
			}

			for (Entity l : game.geteProjectiles()) {
				l.update(projSpeed);
			}

			back.draw();
			if (startCount == true) {
				testTime3 = (Sys.getTime() * 1000) / Sys.getTimerResolution();
				startCount = false;
			}

			if (start == false) {
				TextureImpl.bindNone();
				other.drawString(Display.getWidth() * 7 / 16, Display.getHeight() * 1 / 2,
						"Combat In: " + Integer.toString((int) (5 - (time - testTime3) / 1000)));
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			}

			if (time2 - testTime3 > 5000 && start == false) {
				start = true;
			}

			// grid.draw();

			TextureImpl.bindNone();
			font.drawString(Display.getWidth() * 1 / 4, Display.getHeight() * 3 / 4,
					"Score: " + Integer.toString(game.getScore()));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			TextureImpl.bindNone();
			font.drawString(Display.getWidth() * 2 / 4, Display.getHeight() * 7 / 8,
					"Wave: " + Integer.toString(waveCount));
			TextureImpl.bindNone();
			font.drawString(Display.getWidth() * 2 / 4, Display.getHeight() * 3 / 4,
					"Health: " + Integer.toString(game.getHealth()));
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

			for (Entity p : game.getProjectiles()) {
				p.draw();

			}

			for (Entity l : game.geteProjectiles()) {
				if (!l.getHasHit())
					l.draw();
			}
			for (Enemy k : game.getEnemyShips()) {

				if (k.getHealth() > 0)
					k.draw();

			}

			// UPDATE DISPLAY

			// thing.drawBox();

			if (!game.getHasDied()) {
				game.draw();
			}
			if (game.getHasDied() && (time - testTime < 1000)) {
				game.drawExplosion((int) (time - testTime));
			}
			// showCursor();
			crosshair.draw();

			Display.update();

			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			Display.sync(TARGET_FPS);
			time = time2;
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				Mouse.setGrabbed(true);
				enemyShips.removeAll(enemyShips);
				eProjectiles.removeAll(eProjectiles);
				modStream.stop();
				
				return false;
			}

		}
		return false;

	}

	public static void initGL(int width, int height) throws LWJGLException {
		// open window of appropriate size
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.create();
		Display.setVSyncEnabled(true);

		// enable 2D textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// set "clear" color to black
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// set viewport to entire window
		GL11.glViewport(0, 0, width, height);

		// set up orthographic projectionr
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		// GLU.gluPerspective(90f, 1.333f, 2f, -2f);
		// GL11.glTranslated(0, 0, -500);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
}

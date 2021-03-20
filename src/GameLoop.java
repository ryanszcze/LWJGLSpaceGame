//Ryan Szczepanski Final Project
import org.lwjgl.Sys;
import org.lwjgl.util.Rectangle;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.LWJGLException;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

public class GameLoop {
	public static final int TARGET_FPS = 60;
	public static final int SCR_WIDTH = 3000;
	public static final int SCR_HEIGHT = 2000;

	public int increment = 0;

	private int score;

	public static void main(String[] args) throws LWJGLException {
		initGL(SCR_WIDTH, SCR_HEIGHT);
		List<Entity> projectiles1 = new LinkedList<>();
		List<Entity> eProjectiles1 = new LinkedList<>();
		List<Entity> projectiles2 = new LinkedList<>();
		List<Entity> eProjectiles2 = new LinkedList<>();
		// MouseSprite e = new MouseSprite(200, projectiles);
		List<Enemy> enemyShips1 = new LinkedList<>();
		List<Enemy> enemyShips2 = new LinkedList<>();
		Mouse.setGrabbed(true);
		Menu gameMenu = new Menu();
		// gameMenu.addItem("Play", new MouseSprite(25, projectiles, eProjectiles,
		// enemyShips, 100, 3, 5));
		gameMenu.addItem("Easy", new Leveltwo(projectiles2, eProjectiles2, enemyShips2));
		gameMenu.addItem("Oh god why", new StarfighterMain(projectiles1, eProjectiles1, enemyShips1));

		gameMenu.addSpecial("Exit", Menu.DO_EXIT);
		gameMenu.setFPS(60);

		Scene currScene = gameMenu;

		while (currScene.go()) {
			currScene = currScene.nextScene();

			if (currScene == null) {
				currScene = gameMenu;
			}

		}
		// UPDATE DISPLAY

		Display.destroy();
		AL.destroy();
	}

	public static void initGL(int width, int height) throws LWJGLException {
		// open window of appropriate size
		DisplayMode displayMode = null;
		DisplayMode[] modes = Display.getAvailableDisplayModes();

		for (int i = 0; i < modes.length; i++) {
			if (modes[i].getWidth() == width && modes[i].getHeight() == height && modes[i].isFullscreenCapable()) {
				displayMode = modes[i];
			}
		}
		// Display.setDisplayMode(new DisplayMode(width, height));
		Display.setFullscreen(true);
		Display.setTitle("Space Game");
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
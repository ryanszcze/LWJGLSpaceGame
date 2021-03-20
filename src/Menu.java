import java.util.List;
import java.util.LinkedList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import java.awt.Font;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Color;
import java.io.IOException;

public class Menu extends Scene {

	public static final int TARGET_FPS = 100;
	public static final int SCR_WIDTH = 1980;
	public static final int SCR_HEIGHT = 1080;

	// a menu item: label and associated Scene to jump to
	private static class Item {
		public String label;
		public Scene scene;

		public Item(String label, Scene s) {
			this.label = label;
			this.scene = s;
		}

	}

	public static final int DO_EXIT = 0;

	public static final void fullscreenOn() {
		try {
			Display.setFullscreen(true);
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class Special extends Item {
		public int tag;

		public Special(String label, int tag) {
			super(label, null);
			this.tag = tag;
		}
	}

	// these menu items
	private List<Item> items;

	// currently selected items
	private int currItem;

	public Menu() {
		items = new LinkedList<>();

		try {
			AudioManager.getInstance().loadSample("menuSelect", "res/menusplat.wav");
			AudioManager.getInstance().loadSample("menuChoose", "res/fanfare.wav");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// reset menu
	public void clear() {
		items.clear();
	}

	public void addItem(String label, Scene s) {
		items.add(new Item(label, s));
	}

	public void addSpecial(String label, int tag) {
		items.add(new Special(label, tag));

	}

	public Scene nextScene() {
		return items.get(currItem).scene;
	}

	public boolean drawFrame(float delta) {

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		// process keyboard input

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) // key was pressed
			{
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_DOWN:
					currItem = (currItem + 1) % items.size();

					AudioManager.getInstance().play("menuSelect");

					break;

				case Keyboard.KEY_UP:

					currItem--;

					if (currItem < 0) {
						currItem += items.size(); // go to end
					}

					AudioManager.getInstance().play("menuSelect");

					break;

				case Keyboard.KEY_RETURN:

					// TODO: play sound
					Item item = items.get(currItem);

					if (item instanceof Special) {
						switch (((Special) item).tag) {
						case DO_EXIT:
							exit();
							break;
						}

					}

					AudioManager.getInstance().play("menuChoose");

					return false;

				}
			}
		}

		// draw menu, highlighting currItem

		float spacing = Display.getHeight() / (items.size() + 4);
		float offset = 2 * spacing;

		TrueTypeFont menuFont = new TrueTypeFont(new Font("Times New Roman", Font.BOLD, 50), true);

		for (int i = 0; i < items.size(); i++) {
			if (i == currItem) {

				// GL11.glPushMatrix();
				// GL11.glScalef(1f,1.5f,1);
				menuFont.drawString(Display.getWidth() / 2, offset, items.get(i).label, Color.yellow);
				// GL11.glPopMatrix();
			} else {
				menuFont.drawString(Display.getWidth() / 2, offset, items.get(i).label);
			}

			offset += spacing;
		}
		// font binds a texture, so let's turn it off..

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return true;
	}

	public static void initGL(int width, int height) throws LWJGLException {
		// open window of appropriate size

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
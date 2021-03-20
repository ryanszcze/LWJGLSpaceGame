import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class MouseSprite {
	private static final int TARGET_FPS = 60;
	private Rectangle hitbox, testBox;
	private Texture sprite, explosion1, explosion2, explosion3;

	private static enum State {
		START, LEFT, RIGHT, DOWN, UP, STOP
	};

	private State state;
	private List<Enemy> enemyShips;
	private float speed;
	private int centerX, centerY;
	private double xVel, yVel;
	private float wr, hr;
	List<Entity> projectiles;
	private List<Entity> eProjectiles;

	private long currTime;
	private int rateOfFire;
	private TrueTypeFont font, other;
	private Font awtFont, awtFont2;
	private int score;
	public boolean hasMouse;
	private Cursor cursor;
	public int widthTest;
	public int heightTest;
	private boolean hasDied;
	private int health;
	private int wavesCount, startCount;
	private MouseSprite me;
	private int enemiesKilled;

	public MouseSprite(float width, List<Entity> projectiles, List<Entity> eProjectiles, List<Enemy> enemyShips2,
			int health, int wavesCount, int startCount) {
		this.speed = .6f;
		state = State.DOWN;
		rateOfFire = 150;
		this.projectiles = projectiles;
		this.setEnemyShips(enemyShips2);
		widthTest = Display.getWidth();
		heightTest = Display.getHeight();
		this.wavesCount = wavesCount;
		this.startCount = startCount;
		enemiesKilled = 0;
		this.setHealth(health);
		setHasDied(false);
		this.seteProjectiles(eProjectiles);
		awtFont = new Font("Times New Roman", Font.BOLD, 40);
		awtFont2 = new Font("Times New Roman", Font.BOLD, 50);
		font = new TrueTypeFont(awtFont, true);
		other = new TrueTypeFont(awtFont2, true);

		try {

			sprite = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/spaceship.png"));
			explosion1 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/explosion1.png"));
			explosion2 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/explosion2.png"));
			explosion3 = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/explosion3.png"));

			testBox = new Rectangle(0, 0, Display.getWidth(), Display.getHeight());
			hitbox = new Rectangle(Display.getWidth() / 2, Display.getHeight() / 2, (int) width,
					(int) (width * sprite.getImageHeight() / sprite.getImageWidth()));

			wr = 1.0f * sprite.getImageWidth() / sprite.getTextureWidth();
			hr = 1.0f * sprite.getImageHeight() / sprite.getTextureHeight();
			centerX = hitbox.getX() + hitbox.getWidth() / 2;
			centerY = hitbox.getY() + hitbox.getHeight() / 2;
			xVel = 0;
			yVel = 0;
			AudioManager.getInstance().loadSample("menuSelect", "res/menusplat.wav");
			AudioManager.getInstance().loadSample("menuChoose", "res/fanfare.wav");
			AudioManager.getInstance().loadSample("deathsound", "res/explosion.wav");
			currTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();
			// enemyShips2.add(new Enemy(50, 400, 400, eProjectiles, hitbox,0,
			// projectiles,300, 20));

			// enemyShips2.add(new Enemy(50, 100+100, 100, eProjectiles, hitbox, 1,
			// projectiles, 2000, 20, .6f));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void drawString(int x, int y, String text) {
		awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, false); // <-- slow
		Color.white.bind();
		font.drawString(x, y, text);
	}

	public void playDeathSound() {
		AudioManager.getInstance().play("deathsound");
	}

	public Rectangle getRectangle() {
		return hitbox;
	}

	public void setPosition(int x, int y) {
		hitbox.setX(x);
		hitbox.setY(y);
	}

	public Rectangle intersection(Entity other) {
		Rectangle rval = new Rectangle();
		return hitbox.intersection(other.hitbox, rval);
	}

	boolean testCollision(List<Entity> other) {
		boolean temp = false;
		for (Entity p : other) {

			if (hitbox.intersects(p.getHitbox()) && !p.getHasHit()) {
				p.setHasHit(true);

				p.destroy();
				// setHealth(getHealth() - 20);
				temp = true;

			}

		}
		return temp;
	}

	public void keyupdate(float delta) {
		if (testBox.contains(hitbox)) {

			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {// state=State.RIGHT;
				if (xVel < speed * delta)
					xVel += (int) (speed * delta) / 10;

				// box.translate(xVel, yVel);

			}
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {// state=State.LEFT;
				if (xVel > -speed * delta)
					xVel += (int) (-speed * delta) / 10;

			}

			if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
				// state=State.DOWN;
				if (yVel < speed * delta)
					yVel += (int) (speed * delta) / 10;

			}

			if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
				// state=State.UP;
				if (yVel > -speed * delta)
					yVel += (int) (-speed * delta) / 10;

			} else {
				if (xVel > 0)
					xVel -= (delta * speed / 50);
				if (xVel < 0)
					xVel += (delta * speed / 50);
				if (yVel > 0)
					yVel -= (delta * speed / 50);
				if (yVel < 0)
					yVel += (delta * speed / 50);
			}

		} else {
			xVel = -xVel;
			if (!testBox.contains(hitbox))
				yVel = -yVel;
		}
	}

	public void update(float delta, boolean start) {
		testCollision(geteProjectiles());

		switch (state) {
		case START:
			keyupdate(delta);
			break;

		case STOP:

			break;
		case RIGHT:
			keyupdate(delta);
			break;

		case LEFT:
			keyupdate(delta);

			break;
		case DOWN:

			keyupdate(delta);

			break;

		case UP:

			keyupdate(delta);

			break;

		}

		hitbox.translate((int) xVel, (int) yVel);

		if (Mouse.isButtonDown(0) && start == true)

		{
			long timeCheck = (Sys.getTime() * 1000) / Sys.getTimerResolution();

			if (timeCheck - currTime > rateOfFire) {
				fire();
				currTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();
			}

		}

	}

	public void loseScreen() {
		TextureImpl.bindNone();
		font.drawString(Display.getWidth() * 1 / 2, Display.getHeight() * 1 / 2, "YOU LOSE");
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void addScore(int x) {
		this.setScore(getScore() + x);
	}

	public boolean loseCondition(MouseSprite e) {
		Mouse.setGrabbed(true);
		Menu loseMenu = new Menu();
		List<Entity> projectiles = new LinkedList<>();
		List<Entity> eProjectiles = new LinkedList<>();
		// MouseSprite e = new MouseSprite(200, projectiles);
		List<Enemy> enemyShips = new LinkedList<>();
		// loseMenu.addItem("Play Again", e =new MouseSprite(100, projectiles,
		// eProjectiles, enemyShips, 100, 3, 5));
		// loseMenu.addItem("Main Menu", null);
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

	public void fire() {
		int mx = Mouse.getX();
		int my = Display.getHeight() - Mouse.getY();

		int bx = hitbox.getX() + hitbox.getWidth() / 2;
		int by = hitbox.getY() + hitbox.getHeight() / 2;

		Vector2f vel = new Vector2f(mx - bx, my - by);
		projectiles.add(new Projectile(bx, by, vel, 1, 1, 0));

		AudioManager.getInstance().play("menuSelect", .3f);

		// System.out.println("fire!");

	}

	public boolean intersects(Rectangle other) {
		return hitbox.intersects(other);
	}

	public void drawExplosion(int time) {// System.out.println(time);
		int bx = hitbox.getX() + hitbox.getWidth() / 2;
		int by = hitbox.getY() + hitbox.getHeight() / 2;
		float mouseX = Mouse.getX() - bx;
		xVel = 0;
		yVel = 0;

		float mouseY = (Display.getHeight() - Mouse.getY()) - by;
		double direction = Math.toDegrees(Math.atan2(mouseY, mouseX));

		float x = (float) hitbox.getX();
		float y = (float) hitbox.getY();
		float w = (float) hitbox.getWidth();
		float h = (float) hitbox.getHeight();

		// GL11.glPushMatrix();

		GL11.glTranslatef(bx, by, 0);

		// draw this rectangle using the loaded sprite
		if (time < 333)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, explosion1.getTextureID());
		else if (time < 666)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, explosion2.getTextureID());
		else if (time < 1000)
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, explosion3.getTextureID());

		GL11.glColor3f(1, 1, 1);
		GL11.glTranslatef(-bx, -by, 0);

		//
		/*
		 * GL11.glScalef( sprite.getTextureWidth() * 0.5f, sprite.getTextureHeight() *
		 * 0.5f, 1.0f);
		 */
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);

		GL11.glTexCoord2f(wr, 0);
		GL11.glVertex2f(x + w, y);

		GL11.glTexCoord2f(wr, hr);
		GL11.glVertex2f(x + w, y + h);

		GL11.glTexCoord2f(0, hr);
		GL11.glVertex2f(x, y + h);

		GL11.glEnd();

		// unbind the sprite so that other objects can be drawn
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glLoadIdentity();
		// GL11.glPopMatrix();
	}

	public void draw() {
		int bx = hitbox.getX() + hitbox.getWidth() / 2;
		int by = hitbox.getY() + hitbox.getHeight() / 2;
		float mouseX = Mouse.getX() - bx;

		float mouseY = (Display.getHeight() - Mouse.getY()) - by;
		double direction = Math.toDegrees(Math.atan2(mouseY, mouseX));

		float x = (float) hitbox.getX();
		float y = (float) hitbox.getY();
		float w = (float) hitbox.getWidth();
		float h = (float) hitbox.getHeight();

		// GL11.glPushMatrix();

		GL11.glTranslatef(bx, by, 0);

		GL11.glRotatef((float) direction, 0, 0, 1);
		// draw this rectangle using the loaded sprite

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, sprite.getTextureID());
		GL11.glColor3f(1, 1, 1);
		GL11.glTranslatef(-bx, -by, 0);

		//
		/*
		 * GL11.glScalef( sprite.getTextureWidth() * 0.5f, sprite.getTextureHeight() *
		 * 0.5f, 1.0f);
		 */
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);

		GL11.glTexCoord2f(wr, 0);
		GL11.glVertex2f(x + w, y);

		GL11.glTexCoord2f(wr, hr);
		GL11.glVertex2f(x + w, y + h);

		GL11.glTexCoord2f(0, hr);
		GL11.glVertex2f(x, y + h);

		GL11.glEnd();

		// unbind the sprite so that other objects can be drawn
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glLoadIdentity();
		// GL11.glPopMatrix();
	}

	public void setHasDied(boolean hasDied) {
		this.hasDied = hasDied;
	}

	/*
	 * public void waveManager(int enemyCount, MouseSprite e) { for (int i=0;
	 * i<enemyCount; i++) { e.enemyShips.add(new Enemy(50, 100+100*i, 100,
	 * eProjectiles, e.getRectangle(), 1, projectiles, 3000, 20, currTime, e));
	 * 
	 * } }
	 */
	public void setMe(MouseSprite me) {
		this.me = me;
	}

	public int getHealth() {
		return health;
	}

	/*
	 * public boolean drawFrame(float delta) { // TODO Auto-generated method stub
	 * 
	 * List<Entity> projectiles = new LinkedList<>(); List<Entity> eProjectiles =
	 * new LinkedList<>();
	 * 
	 * List<Enemy> enemyShips = new LinkedList<>(); int enemyCount=5;
	 * 
	 * 
	 * 
	 * 
	 * MouseSprite e = new MouseSprite(100, projectiles, eProjectiles, enemyShips,
	 * 100,3 ,5);
	 * 
	 * 
	 * Background back = new Background(Display.getWidth()); Crosshair crosshair =
	 * new Crosshair(50);
	 * 
	 * waveManager(enemyCount, e);
	 * 
	 * 
	 * Mouse.setGrabbed(true); setScore(0); grid = new InfluenceWorld(100, 100);
	 * 
	 * 
	 * long time = (Sys.getTime()*1000)/Sys.getTimerResolution(); // ms
	 * currTime=time; long testTime, testTime2; testTime=time;
	 * 
	 * while (! Display.isCloseRequested()) {
	 * 
	 * 
	 * long time2 = (Sys.getTime()*1000)/ Sys.getTimerResolution(); // ms int delta1
	 * = (int)(time2-time); //System.out.println(delta); if(!e.getHasDied()) {
	 * if(e.testCollision(e.eProjectiles)&& e.getHealth()>0) {
	 * e.setHealth(e.getHealth() - 20); } } if(!e.getHasDied()) { e.update(delta1,
	 * hasDied); }
	 * 
	 * 
	 * if (e.getHealth()<=0) { TextureImpl.bindNone();
	 * other.drawString(Display.getWidth()*1/2, Display.getHeight()*1/2,
	 * "YOU LOSE"); GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	 * 
	 * if(e.getHasDied()==false) { testTime=(Sys.getTime()*1000)/
	 * Sys.getTimerResolution(); } e.setHasDied(true); testTime2=
	 * (Sys.getTime()*1000)/ Sys.getTimerResolution(); if (testTime2-testTime >
	 * 5000) { return loseCondition(e); }
	 * 
	 * }
	 * 
	 * for (Entity p : e.projectiles) { p.update(delta1/8); }
	 * 
	 * 
	 * for (Enemy k: e.enemyShips) {
	 * 
	 * if (k.getHealth()>0) { k.update(delta1);
	 * 
	 * } if (k.getStatus()&& !k.getIgnore()) { e.setScore(e.getScore() + 5);
	 * k.setIgnore(true); } k.updateEnemy(delta1);
	 * 
	 * }
	 * 
	 * for (Entity l : e.eProjectiles) { l.update(delta1/10); }
	 * 
	 * 
	 * 
	 * back.draw();
	 * 
	 * // grid.draw();
	 * 
	 * TextureImpl.bindNone(); font.drawString(Display.getWidth()*1/4,
	 * Display.getHeight()*3/4, "Score: " +Integer.toString(e.getScore()));
	 * GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0); TextureImpl.bindNone();
	 * font.drawString(Display.getWidth()*2/4, Display.getHeight()*3/4, "Health: "+
	 * Integer.toString(e.getHealth())); GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	 * 
	 * for (Entity p : projectiles) { p.draw();
	 * 
	 * }
	 * 
	 * for (Entity l : e.eProjectiles) {if (!l.getHasHit()) l.draw(); } for (Enemy k
	 * : e.enemyShips) {
	 * 
	 * if (k.getHealth()>0) k.draw();
	 * 
	 * 
	 * }
	 * 
	 * // UPDATE DISPLAY
	 * 
	 * 
	 * // thing.drawBox();
	 * 
	 * 
	 * if(!e.getHasDied()) { e.draw(); } //showCursor(); crosshair.draw();
	 * 
	 * Display.update();
	 * 
	 * GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	 * 
	 * 
	 * Display.sync(TARGET_FPS); time = time2; if
	 * (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {Mouse.setGrabbed(true); return
	 * false; }
	 * 
	 * 
	 * } return false;
	 * 
	 * 
	 * 
	 * }
	 */
	/*
	 * public static void initGL(int width, int height) throws LWJGLException { //
	 * open window of appropriate size Display.setDisplayMode(new DisplayMode(width,
	 * height)); Display.create(); Display.setVSyncEnabled(true);
	 * 
	 * // enable 2D textures GL11.glEnable(GL11.GL_TEXTURE_2D);
	 * 
	 * // set "clear" color to black GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	 * 
	 * // enable alpha blending GL11.glEnable(GL11.GL_BLEND);
	 * GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	 * 
	 * // set viewport to entire window GL11.glViewport(0,0,width,height);
	 * 
	 * // set up orthographic projectionr GL11.glMatrixMode(GL11.GL_PROJECTION);
	 * GL11.glLoadIdentity(); GL11.glOrtho(0, width, height, 0, 1, -1); //
	 * GLU.gluPerspective(90f, 1.333f, 2f, -2f); // GL11.glTranslated(0, 0, -500);
	 * GL11.glMatrixMode(GL11.GL_MODELVIEW); }
	 */
	public boolean getHasDied() {
		return hasDied;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public List<Entity> getProjectiles() {
		return projectiles;
	}

	public void setProjectiles(List<Entity> Projectiles) {
		this.projectiles = Projectiles;
	}

	public List<Entity> geteProjectiles() {
		return eProjectiles;
	}

	public void seteProjectiles(List<Entity> eProjectiles) {
		this.eProjectiles = eProjectiles;
	}

	public List<Enemy> getEnemyShips() {
		return enemyShips;
	}

	public void setEnemyShips(List<Enemy> enemyShips) {
		this.enemyShips = enemyShips;
	}

	public long getCurrTime() {
		// TODO Auto-generated method stub
		return currTime;
	}

	public int getEnemiesKilled() {
		return enemiesKilled;
	}

	public void setEnemiesKilled() {
		enemiesKilled++;
	}
}


import org.newdawn.slick.opengl.Texture;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.util.concurrent.TimeUnit;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.WritablePoint;
import org.lwjgl.util.vector.Vector2f;

import java.util.LinkedList;
import java.util.List;
import org.lwjgl.Sys;

public class Enemy extends Entity {
	private static enum State {
		START, LEFT, RIGHT
	};

	private Texture sprite;
	private Rectangle hitbox;
	private float speed;
	private List<Entity> projectiles;
	private Rectangle player;
	private List<Entity> eProjectiles;
	private int xVelocity, yVelocity;
	private State state;
	private long currTime;
	private double rateOfFire;
	private int mode;
	private int health;
	float projSpeed;
	private boolean destroyed, ignore;
	private MouseSprite mousePlayer;
	private int inputY, inputX;

	public Enemy(float width, int x, int y, List<Entity> projectiles, Rectangle player, int mode,
			List<Entity> eProjectiles, double rateOfFire, int health, long currTime, MouseSprite mousePlayer,
			float projSpeed) {
		this.health = health;
		this.player = player;
		this.projectiles = projectiles;
		this.eProjectiles = eProjectiles;
		this.projSpeed = projSpeed;
		this.mousePlayer = mousePlayer;
		speed = .2f;
		this.rateOfFire = rateOfFire;
		xVelocity = 0;
		yVelocity = 0;
		state = State.START;
		inputY = y;
		this.mode = mode;
		this.currTime = currTime;
		// modes 1= side to side.
		destroyed = false;
		ignore = false;
		/*
		 * currTime= (Sys.getTime()*1000)/ Sys.getTimerResolution();
		 */

		try {

			sprite = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/enemy.png"));

			hitbox = new Rectangle(x, y - 50, (int) width,
					(int) (width * sprite.getImageHeight() / sprite.getImageWidth()));
			wr = 1.0f * sprite.getImageWidth() / sprite.getTextureWidth();
			hr = 1.0f * sprite.getImageHeight() / sprite.getTextureHeight();
			AudioManager.getInstance().loadSample("fire", "res/enemyfire.wav");
			AudioManager.getInstance().loadSample("explosion", "res/explosion.wav");
			spawnAbove();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public void spawnAbove() {
		while (hitbox.getY() < inputY) {
			hitbox.translate(0, 10);
		}

	}

	public void update(float delta, boolean start) {
		long timeCheck = (Sys.getTime() * 1000) / Sys.getTimerResolution();

		testCollision(eProjectiles);
			

		if (timeCheck - currTime > rateOfFire && mousePlayer.getHealth() > 0 && start == true) {
			fire();
			currTime = (Sys.getTime() * 1000) / Sys.getTimerResolution();

		}

		if (health < 0 && destroyed == false) {
			destroyed = true;
			AudioManager.getInstance().play("explosion", .3f);

		}
	}

	public boolean getIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore)

	{
		this.ignore = ignore;

	}

	public boolean getStatus() {
		return destroyed;
	}

	public void destroy() {
		hitbox = null;
	}

	public int getHealth() {
		return health;
	}

	public Rectangle getHitbox() {
		return hitbox;
	}

	public Rectangle intersection(Entity other) {
		Rectangle rval = new Rectangle();
		return hitbox.intersection(other.hitbox, rval);
	}

	private boolean testCollision(List<Entity> other) {
		boolean temp = false;
		for (Entity p : other) {

			if (hitbox.intersects(p.getHitbox()) && !p.getHasHit()) {
				p.setHasHit(true);

				health -= 15;
				p.destroy();

				temp = true;

			}

		}
		return temp;
	}

	public void onCollision(Entity other) {// System.out.println("Collison");

		// projectiles.remove(1);
	}

	public boolean intersects(Entity other) {
		return true;
	}

	public void fire() {
		int mx = player.getX() + player.getWidth() / 2;
		int my = player.getY() + player.getHeight() / 2;

		int bx = hitbox.getX() + hitbox.getWidth() / 2;
		int by = hitbox.getY() + hitbox.getHeight() / 2;

		Vector2f vel = new Vector2f(mx - bx, my - by);
		projectiles.add(new Projectile(bx, by, vel, 1, 0, 0));
		AudioManager.getInstance().play("fire");
		// System.out.println("fire!");

	}

	public void updateEnemy(float delta) {
		if (mode == 1) {
			switch (state) {
			case START:
				spawnAbove();
				state = State.RIGHT;
				break;
			case RIGHT:

				hitbox.translate(4, 0);

				if (hitbox.getX() >= Display.getWidth()) {
					xVelocity -= 8;
					state = State.LEFT;
				}

				break;

			case LEFT:

				hitbox.translate(-4, 0);

				if (hitbox.getX() <= 0) {
					state = State.RIGHT;
					xVelocity += 8;
				}

				break;
			}
		}

	}

	public void updateProjectile(float delta) {
		for (Entity y : projectiles) {
			y.update(projSpeed);
		}
	}

	public void updateProjSpeed(double mult) {
		projSpeed = (float) (projSpeed * mult);
	}

	public void drawProjectile() {
		for (Entity o : projectiles) {
			o.draw();
		}
	}

	public void draw() {

		if (health >= 0) {
			float x = (float) hitbox.getX();
			float y = (float) hitbox.getY();
			float w = (float) hitbox.getWidth();
			float h = (float) hitbox.getHeight();

			// GL11.glPushMatrix();

			// draw this rectangle using the loaded sprite

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, sprite.getTextureID());
			GL11.glColor3f(1, 1, 1);

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

			// GL11.glPopMatrix();
		}
	}

}

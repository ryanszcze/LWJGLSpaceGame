import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.lwjgl.util.vector.Vector2f;

public class Projectile extends Entity {

	private Vector2f vel;
	private Rectangle hitbox;
	private boolean hasHit;
	private float colorR, colorG, colorB;

	public Projectile(int x, int y, Vector2f vel, float colorR, float colorG, float colorB) {
		hasHit = false;
		hitbox = new Rectangle(x, y, 10, 10);
		this.vel = new Vector2f();
		vel.normalise(this.vel);
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
	}

	public Rectangle getHitbox() {
		return hitbox;
	}

	public boolean getHasHit() {
		return hasHit;
	}

	public void setHasHit(boolean hasHit) {
		this.hasHit = hasHit;
	}

	public void update(float delta) {
		hitbox.translate((int) (delta * 8 * vel.getX()), (int) ((delta * 8 * vel.getY())));

		// Vector2f.add(vel, new Vector2f(0, .1f), vel);
	}

	public void draw() {
		if (!hasHit) {

			float x = (float) hitbox.getX();
			float y = (float) hitbox.getY();
			float w = (float) hitbox.getWidth();
			float h = (float) hitbox.getWidth();

			// draw the square

			GL11.glColor3f(colorR, colorG, colorB);
			GL11.glBegin(GL11.GL_QUADS);

			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x + w, y);
			GL11.glVertex2f(x + w, y + w);
			GL11.glVertex2f(x, y + w);

			GL11.glEnd();
		}
	}

}
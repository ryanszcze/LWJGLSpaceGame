
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
import java.io.IOException;
import org.lwjgl.opengl.GL11;

public class Entity {

	protected Rectangle hitbox;
	protected Texture texture = null;
	protected float wr, hr;
	protected float r = 1, g = 1, b = 1;
	private boolean active = true;

	public Entity() {
		hitbox = new Rectangle(); // empty rectangle
	}

	public void setRGB(int rIn, int gIn, int bIn) {
		r = rIn;
		g = gIn;
		b = bIn;

	}

	public Entity(int x, int y, int w, int h) {
		hitbox = new Rectangle(x, y, w, h); // non-empty rectangle
	}

	public Entity(String texturePath, int width, int x, int y) {
		loadTexture(texturePath);
		hitbox = new Rectangle(x, y, width, (int) (width * texture.getImageHeight() * 1.0f / texture.getImageWidth()));

	}

	public boolean getHasHit() {
		return false;

	}

	public void setHasHit(boolean hasHit) {

	}

	public void loadTexture(String path) {
		try {

			// load texture as png from res/ directory (this can throw IOException)
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path));

			// textures come in as a power of 2. use these ratios to
			// calculate texture offsets for sprite based on box size
			wr = (1.0f) * texture.getImageWidth() / texture.getTextureWidth();
			hr = (1.0f) * texture.getImageHeight() / texture.getTextureHeight();
		} catch (IOException e) {
			throw new RuntimeException("failed to load Texture");
		}
	}

	public void setTexture(Texture t) {
		texture = t;
		this.wr = (1.0f) * texture.getImageWidth() / texture.getTextureWidth();
		this.hr = (1.0f) * texture.getImageHeight() / texture.getTextureHeight();
	}

	public void setRGB(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void init() {
	}

	public void destroy() {
	}

	public void update(float delta) {
	}

	public void draw() {
		float x = (float) hitbox.getX();
		float y = (float) hitbox.getY();
		float w = (float) hitbox.getWidth();
		float h = (float) hitbox.getHeight();

		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		GL11.glColor3f(r, g, b);

		if (texture == null) {
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x + w, y);
			GL11.glVertex2f(x + w, y + h);
			GL11.glVertex2f(x, y + h);
			GL11.glEnd();
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());

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

		}

	}

	public boolean intersects(Entity other) {
		return hitbox.intersects(other.hitbox);
	}

	public Rectangle intersection(Entity other) {
		Rectangle rval = new Rectangle();
		return hitbox.intersection(other.hitbox, rval);
	}

	public Rectangle getHitbox() {
		return hitbox;
	}

	public boolean testCollision(Entity other) {
		System.out.println("Entering collision loop");
		if (hitbox.intersects(other.hitbox)) {
			onCollision(other);
			return true;
		} else {
			return false;
		}
	}

	public void onCollision(Entity other) {
	}

	public boolean isActive() {
		return active;
	}

	protected void deactivate() {
		active = false;
	}

}
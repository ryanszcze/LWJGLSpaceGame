
import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Crosshair {

	private Rectangle backBox;
	private Texture crosshair;
	private float wr, hr;

	public Crosshair(float width) {
		try {
			crosshair = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/crosshair.png"));
			backBox = new Rectangle(0, 0, (int) width,
					(int) (width * crosshair.getImageHeight() / crosshair.getImageWidth()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		wr = 1.0f * crosshair.getImageWidth() / crosshair.getTextureWidth();
		hr = 1.0f * crosshair.getImageHeight() / crosshair.getTextureHeight();
	}

	public void draw() {
		int bx = backBox.getX() + backBox.getWidth() / 2;
		int by = backBox.getY() + backBox.getHeight() / 2;
		float mouseX = Mouse.getX() - bx;

		float mouseY = (Display.getHeight() - Mouse.getY()) - by;
		float x = mouseX;
		float y = mouseY;
		float w = (float) backBox.getWidth();
		float h = (float) backBox.getHeight();

		// GL11.glPushMatrix();

		// draw this rectangle using the loaded sprite

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, crosshair.getTextureID());
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

	}
}

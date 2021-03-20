
import java.io.IOException;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Background2 {

	private Rectangle backBox;
	private Texture background;
	private float wr, hr;

	public Background2(float width) {
		try {
			background = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/level1.jpg"));
			backBox = new Rectangle(0, 0, (int) width,
					(int) (width * background.getImageHeight() / background.getImageWidth()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		wr = 1.0f * background.getImageWidth() / background.getTextureWidth();
		hr = 1.0f * background.getImageHeight() / background.getTextureHeight();
	}

	public void draw() {
		float x = (float) backBox.getX();
		float y = (float) backBox.getY();
		float w = (float) backBox.getWidth();
		float h = (float) backBox.getHeight();

		// GL11.glPushMatrix();

		// draw this rectangle using the loaded sprite

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, background.getTextureID());
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
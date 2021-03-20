
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.openal.AudioLoader;

public abstract class Scene {
	private boolean doExit = false;
	private int targetFPS = 60;

	// private MouseSprite e;
	// return false if the game should be quit
	public abstract boolean drawFrame(float delta);

	// null typically means Game should load menu
	protected Scene nextScene() {
		return null;
	}

	protected void exit() {
		doExit = true;
	};

	protected void setFPS(int fps) {
		targetFPS = fps;
	}

	// returns false when game should be exited
	public boolean go() {
		long lastloop = (Sys.getTime() * 1000 / Sys.getTimerResolution());

		boolean keepGoing = true;
		do {
			Display.sync(targetFPS); // 60 FPS
			long now = (Sys.getTime() * 1000 / Sys.getTimerResolution());
			long delta = now - lastloop;
			lastloop = now;

			keepGoing = drawFrame(delta);

			// UPDATE DISPLAY
			Display.update();
			// AudioManager.getInstance().update();

			if (Display.isCloseRequested() || doExit) {
				return false;
			}

		} while (keepGoing);

		return true;
	}
}
package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A utility class for creating the static sky background object.
 */
public class Sky {
	private static final Color SKY_COLOR = Color.decode("#80C6E5");
	private static final String SKY_TAG = "sky";

	/**
	 * Creates the sky GameObject that fills the background.
	 *
	 * @param windowDimensions The dimensions of the game window.
	 * @return A GameObject representing the sky.
	 */
	public static GameObject create(Vector2 windowDimensions) {
		GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
				new RectangleRenderable(SKY_COLOR));
		sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sky.setTag(SKY_TAG);
		return sky;
	}
}

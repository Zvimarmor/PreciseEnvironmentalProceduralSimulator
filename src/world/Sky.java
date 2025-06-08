package world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Utility class for creating the sky background in the game.
 */
public class Sky {

	private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

	/**
	 * Creates a sky object that fills the entire window.
	 *
	 * @param windowDimensions The dimensions of the game window.
	 * @return A GameObject representing the sky.
	 */
	public static GameObject create(Vector2 windowDimensions) {
		GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
				new RectangleRenderable(BASIC_SKY_COLOR));
		sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sky.setTag("sky");

		return sky;
	}
}

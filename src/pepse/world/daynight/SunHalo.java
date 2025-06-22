package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A utility class for creating a glowing halo that surrounds the sun and follows its movement.
 */
public class SunHalo {
	private static final String SUN_HALO_TAG = "sunHalo";
	private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
	private static final float HALO_DIAMETER = 250f;

	/**
	 * Creates a halo GameObject that tracks the sun.
	 *
	 * @param sun The sun GameObject to follow.
	 * @return A GameObject representing the sun's halo.
	 */
	public static GameObject create(GameObject sun) {
		Renderable renderable = new OvalRenderable(HALO_COLOR);
		GameObject halo = new GameObject(Vector2.ZERO,
				new Vector2(HALO_DIAMETER, HALO_DIAMETER),
				renderable);

		halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		halo.setTag(SUN_HALO_TAG);
		halo.setCenter(sun.getCenter());

		// Continuously update halo position to follow the sun
		halo.addComponent(deltaTime -> halo.setCenter(sun.getCenter()));

		return halo;
	}
}

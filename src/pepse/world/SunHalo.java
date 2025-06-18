package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A glowing halo that surrounds the sun and follows its movement.
 */
public class SunHalo {

	private static final String SUN_HALO_TAG = "sunHalo";
	private static final Color HALO_COLOR = new Color(255, 255, 0, 20); // semi-transparent yellow
	private static final float HALO_DIAMETER = 150f;

	/**
	 * Creates a halo GameObject that tracks and surrounds the sun.
	 *
	 * @param sun The GameObject representing the sun.
	 * @return A GameObject representing the sun's halo.
	 */
	public static GameObject create(GameObject sun) {
		Renderable haloRenderable = new OvalRenderable(HALO_COLOR);
		GameObject sunHalo = new GameObject(Vector2.ZERO,
				new Vector2(HALO_DIAMETER, HALO_DIAMETER), haloRenderable);

		sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sunHalo.setTag(SUN_HALO_TAG);
		sunHalo.setCenter(sun.getCenter());

		// Synchronize halo position with sun position each frame
		sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));

		return sunHalo;
	}
}

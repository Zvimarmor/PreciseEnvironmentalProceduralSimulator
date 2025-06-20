package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Utility class to create a night overlay object that simulates
 * day-night cycle using a transition in opacity.
 */
public class Night {

	private static final float MIDNIGHT_OPACITY = 0.5f;
	private static final String NIGHT_TAG = "night";

	/**
	 * Creates a GameObject that darkens the screen cyclically.
	 *
	 * @param windowDimensions The size of the window.
	 * @param cycleLength      Duration of full day-night cycle (in seconds).
	 * @return A GameObject representing the night overlay.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		Renderable renderable = new RectangleRenderable(Color.BLACK);
		GameObject night = new GameObject(Vector2.ZERO, windowDimensions, renderable);
		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		night.setTag(NIGHT_TAG);

		new Transition<>(
				night,
				night.renderer()::setOpaqueness,
				0f,
				MIDNIGHT_OPACITY,
				Transition.CUBIC_INTERPOLATOR_FLOAT,
				cycleLength / 2,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);

		return night;
	}
}

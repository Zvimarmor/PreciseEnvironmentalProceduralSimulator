package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Creates a dark transparent overlay that simulates a day-night cycle
 * by changing its opacity periodically.
 */
public class Night {

	private static final float MIDNIGHT_OPACITY = 0.5f; // Max darkness level
	private static final String NIGHT_TAG = "night";
	private static final Color NIGHT_COLOR = Color.BLACK;
	private static final float INITIAL_OPACITY = 0f;

	/**
	 * Creates a GameObject representing the night overlay.
	 *
	 * @param windowDimensions Dimensions of the window in pixels.
	 * @param cycleLength The duration (in seconds) of a full day-night cycle.
	 * @return A GameObject that changes opacity over time to simulate night.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		Renderable nightRenderable = new RectangleRenderable(NIGHT_COLOR);
		GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightRenderable);
		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		night.setTag(NIGHT_TAG);

		// Transition controls the opacity between day and night
		new Transition<>(
				night,
				night.renderer()::setOpaqueness,
				INITIAL_OPACITY,
				MIDNIGHT_OPACITY,
				Transition.CUBIC_INTERPOLATOR_FLOAT,
				cycleLength / 2f,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);

		return night;
	}
}

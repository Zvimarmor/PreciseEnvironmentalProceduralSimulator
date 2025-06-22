package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A utility class for creating the sun GameObject, which moves in a circular path
 * to simulate a day-night cycle.
 */
public class Sun {
	private static final String SUN_TAG = "sun";
	private static final float SUN_DIAMETER = 100f;
	private static final float ORBIT_RADIUS = 300f;
	private static final Color SUN_COLOR = Color.YELLOW;

	private static final float CENTER_X_RATIO = 0.5f;
	private static final float CENTER_Y_RATIO = 1f / 3f;
	private static final float SUN_INITIAL_ANGLE = 0f;
	private static final float SUN_FULL_ROTATION_DEGREES = 360f;

	/**
	 * Creates a GameObject representing the sun.
	 *
	 * @param windowDimensions The dimensions of the game window.
	 * @param cycleLength      The time (in seconds) of a full sun rotation cycle.
	 * @return A GameObject representing the sun.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		Vector2 center = new Vector2(
				windowDimensions.x() * CENTER_X_RATIO,
				windowDimensions.y() * CENTER_Y_RATIO
		);
		Vector2 startPoint = center.subtract(new Vector2(0, ORBIT_RADIUS));

		Renderable renderable = new OvalRenderable(SUN_COLOR);
		GameObject sun = new GameObject(Vector2.ZERO,
				new Vector2(SUN_DIAMETER, SUN_DIAMETER),
				renderable);

		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sun.setTag(SUN_TAG);
		sun.setCenter(startPoint);

		new Transition<>(
				sun,
				angle -> sun.setCenter(startPoint.subtract(center)
						.rotated(angle).add(center)),
				SUN_INITIAL_ANGLE,
				SUN_FULL_ROTATION_DEGREES,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				cycleLength,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);

		return sun;
	}
}

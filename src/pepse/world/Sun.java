package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * This class creates a sun object that rotates in the sky in a circular path over time.
 */
public class Sun {

	private static final String SUN_TAG = "sun";
	private static final float SUN_DIAMETER = 70f;
	private static final Color SUN_COLOR = Color.YELLOW;
	private static final float SUN_ORBIT_RADIUS = 360f;
	private static final float INITIAL_ANGLE = 0f;
	private static final float FINAL_ANGLE = 360f;

	/**
	 * Creates a sun GameObject that moves in a circular path to simulate a day-night cycle.
	 *
	 * @param windowDimensions The dimensions of the window.
	 * @param cycleLength      Total duration (in seconds) of a full sun rotation.
	 * @return A sun GameObject.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		Vector2 sunOrbitCenter = new Vector2(
				windowDimensions.x() / 2f,
				windowDimensions.y() / 3f
		);
		Vector2 initialSunCenter = new Vector2(
				sunOrbitCenter.x(),
				sunOrbitCenter.y() - SUN_ORBIT_RADIUS
		);

		Renderable sunRenderable = new OvalRenderable(SUN_COLOR);
		GameObject sun = new GameObject(Vector2.ZERO,
				new Vector2(SUN_DIAMETER, SUN_DIAMETER), sunRenderable);
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sun.setTag(SUN_TAG);
		sun.setCenter(initialSunCenter);

		new Transition<>(
				sun,
				angle -> sun.setCenter(
						initialSunCenter
								.subtract(sunOrbitCenter)
								.rotated(angle)
								.add(sunOrbitCenter)
				),
				INITIAL_ANGLE,
				FINAL_ANGLE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				cycleLength,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);

		return sun;
	}
}

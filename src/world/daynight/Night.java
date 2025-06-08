package world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * This class creates a black transparent square that darkens the screen
 * based on a day-night cycle using a transition.
 */
public class Night {

	private static final float MIDNIGHT_OPACITY = 0.5f;
	private static final String NIGHT_TAG = "night";
	private static final Color NIGHT_COLOR = Color.BLACK;
	private static final float INITIAL_OPACITY = 0f;

	/**
	 * Creates a night GameObject which darkens the screen during nighttime.
	 *
	 * @param windowDimensions Vector2 representing the dimensions of the screen.
	 * @param cycleLength      The total time (in seconds) of a full day-night cycle.
	 * @return GameObject that simulates nighttime.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		Renderable nightRenderable = new RectangleRenderable(NIGHT_COLOR);
		GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightRenderable);
		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		night.setTag(NIGHT_TAG);

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

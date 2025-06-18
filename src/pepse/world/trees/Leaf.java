package pepse.world.trees;

import danogl.GameObject;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import danogl.components.ScheduledTask;


import java.awt.*;
import java.util.Random;

/**
 * Represents a single leaf in the Pepse tree system. The leaf sways with the wind.
 */
public class Leaf extends GameObject {
	private static final Color LEAF_COLOR = new Color(50, 200, 30);
	private static final Vector2 LEAF_SIZE = new Vector2(25, 25);
	private static final float MIN_ANGLE = -5f;
	private static final float MAX_ANGLE = 5f;
	private static final float MIN_WIDTH_FACTOR = 0.8f;
	private static final float MAX_WIDTH_FACTOR = 1.2f;
	private static final float DURATION = 0.5f;

	private final Random random = new Random();

	/**
	 * Constructs a new LeafComponent.
	 *
	 * @param position the position of the leaf in the world
	 */
	public Leaf(Vector2 position) {
		super(position, LEAF_SIZE, new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
		new ScheduledTask(
				this,
				random.nextFloat(),
				false,
				this::initializeWindTransitions
		);
	}

	/**
	 * Adds transitions to simulate wind swaying and leaf width changes.
	 */
	private void initializeWindTransitions() {
		// Swaying angle
		new Transition<Float>(
				this,
				angle -> this.renderer().setRenderableAngle(angle),
				MIN_ANGLE,
				MAX_ANGLE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				DURATION,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);

		// Width flutter
		new Transition<Float>(
				this,
				scale -> this.setDimensions(new Vector2(LEAF_SIZE.x() * scale, LEAF_SIZE.y())),
				MIN_WIDTH_FACTOR,
				MAX_WIDTH_FACTOR,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				DURATION,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);
	}
}

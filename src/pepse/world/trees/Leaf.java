package pepse.world.trees;

import danogl.GameObject;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.Color;
import java.util.Random;

/**
 * Represents a single leaf in the Pepse tree system. The leaf sways with the wind.
 */
public class Leaf extends GameObject {
	private static final Color LEAF_COLOR = new Color(100, 50, 20);
	private static final Vector2 LEAF_SIZE = new Vector2(10, 20);
	private static final float MIN_ANGLE = -10f;
	private static final float MAX_ANGLE = 10f;
	private static final float ANGLE_CHANGE_INTERVAL = 1.0f; // seconds
	private static final float ANGLE_CHANGE_DURATION = 0.5f; // seconds

	private final Random random = new Random();

	/**
	 * Constructs a new LeafComponent.
	 *
	 * @param position the position of the leaf in the world
	 */
	public Leaf(Vector2 position) {
		super(position, LEAF_SIZE, new RectangleRenderable( LEAF_COLOR));
//		initializeWindSway();
	}

//	/**
//	 * Adds a swaying motion to the leaf using an angle transition.
//	 */
//	private void initializeWindSway() {
//		new Transition<Float>(
//				this.renderer()::setRenderableAngle,
//				MIN_ANGLE,
//				MAX_ANGLE,
//				ANGLE_CHANGE_DURATION,
//				Transition.TransitionType.BACK_AND_FORTH,
//				random.nextFloat() * ANGLE_CHANGE_INTERVAL
//		);
//	}
}
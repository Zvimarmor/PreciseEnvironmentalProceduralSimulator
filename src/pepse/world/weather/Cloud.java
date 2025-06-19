package pepse.world.weather;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cloud made of block-shaped pixels that moves across the screen.
 */
public class Cloud {
	private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
	private static final int CLOUD_SIZE = 30;
	private static final float CLOUD_SPEED = 0.1f;

	private final List<GameObject> cloudBlocks;
	private final Vector2 initialTopLeft;

	/**
	 * Constructs a cloud object and adds it to the game.
	 *
	 * @param topLeftCorner  The initial top-left corner where the cloud will appear.
	 * @param gameObjects    The collection to which the cloud blocks will be added.
	 * @param layer          The rendering layer for the cloud.
	 */
	public Cloud(Vector2 topLeftCorner, GameObjectCollection gameObjects, int layer) {
		this.initialTopLeft = topLeftCorner;
		this.cloudBlocks = new ArrayList<>();

		List<List<Integer>> cloudMatrix = List.of(
				List.of(0, 1, 1, 0, 0, 0),
				List.of(1, 1, 1, 1, 1, 1),
				List.of(1, 1, 1, 1, 1, 1),
				List.of(0, 1, 1, 1, 0, 0),
				List.of(0, 0, 0, 0, 0, 0)
		);

		// Create cloud pixels based on matrix definition
		for (int row = 0; row < cloudMatrix.size(); row++) {
			for (int col = 0; col < cloudMatrix.get(row).size(); col++) {
				if (cloudMatrix.get(row).get(col) == 1) {
					Vector2 blockPos = topLeftCorner.add(new Vector2(col * CLOUD_SIZE, row * CLOUD_SIZE));
					GameObject block = new GameObject(
							blockPos,
							Vector2.ONES.mult(CLOUD_SIZE),
							new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR)));
					block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
					gameObjects.addGameObject(block, layer);
					cloudBlocks.add(block);
				}
			}
		}

		float startX = topLeftCorner.x();
		float endX = startX + 800;
		float duration = (endX - startX) / CLOUD_SPEED;

		GameObject scheduler = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		scheduler.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects.addGameObject(scheduler, Layer.BACKGROUND);

		// Move cloud blocks horizontally using Transition
		new Transition<>(
				scheduler,
				x -> {
					float deltaX = x - startX;
					for (GameObject block : cloudBlocks) {
						block.setTopLeftCorner(new Vector2(
								block.getTopLeftCorner().x() + deltaX,
								block.getTopLeftCorner().y()
						));
					}
				},
				startX,
				endX,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				duration,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);
	}

	/**
	 * Returns the current center of the cloud.
	 *
	 * @return A Vector2 representing the center of the cloud.
	 */
	public Vector2 getCenter() {
		if (!cloudBlocks.isEmpty()) {
			return cloudBlocks.get(3).getCenter(); // Arbitrary block
		}
		return initialTopLeft;
	}

	/**
	 * Returns the current bottom-center position of the cloud.
	 * Used for spawning rain drops.
	 *
	 * @return Vector2 representing the bottom center of the cloud.
	 */
	public Vector2 getCurrentBottomCenter() {
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;

		// Determine bounding box for the cloud
		for (GameObject block : cloudBlocks) {
			Vector2 topLeft = block.getTopLeftCorner();
			Vector2 dimensions = block.getDimensions();
			float blockMinX = topLeft.x();
			float blockMaxX = topLeft.x() + dimensions.x();
			float blockBottomY = topLeft.y() + dimensions.y();

			minX = Math.min(minX, blockMinX);
			maxX = Math.max(maxX, blockMaxX);
			maxY = Math.max(maxY, blockBottomY);
		}

		float centerX = (minX + maxX) / 2f;
		return new Vector2(centerX, maxY);
	}
}

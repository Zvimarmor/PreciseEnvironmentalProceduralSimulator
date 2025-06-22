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
	private static final int BLOCK_SIZE = 30;
	private static final float CLOUD_SPEED_PX_PER_SEC = 0.1f;
	private static final float CLOUD_TRAVEL_DISTANCE = 800f;

	private static final List<List<Integer>> CLOUD_MATRIX = List.of(
			List.of(0, 1, 1, 0, 0, 0),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(0, 1, 1, 1, 0, 0),
			List.of(0, 0, 0, 0, 0, 0)
	);

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

		createCloudBlocks(topLeftCorner, gameObjects, layer);
		animateCloudMovement(topLeftCorner.x(), gameObjects);
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

		for (GameObject block : cloudBlocks) {
			Vector2 topLeft = block.getTopLeftCorner();
			Vector2 size = block.getDimensions();

			float blockMinX = topLeft.x();
			float blockMaxX = blockMinX + size.x();
			float blockBottomY = topLeft.y() + size.y();

			minX = Math.min(minX, blockMinX);
			maxX = Math.max(maxX, blockMaxX);
			maxY = Math.max(maxY, blockBottomY);
		}

		float centerX = (minX + maxX) / 2f;
		return new Vector2(centerX, maxY);
	}

	// Adds all cloud pixels to the game world based on the matrix
	private void createCloudBlocks(Vector2 topLeftCorner, GameObjectCollection gameObjects, int layer) {
		for (int row = 0; row < CLOUD_MATRIX.size(); row++) {
			for (int col = 0; col < CLOUD_MATRIX.get(row).size(); col++) {
				if (CLOUD_MATRIX.get(row).get(col) == 1) {
					Vector2 blockPos = topLeftCorner.add(
							new Vector2(col * BLOCK_SIZE, row * BLOCK_SIZE));
					GameObject block = new GameObject(
							blockPos,
							Vector2.ONES.mult(BLOCK_SIZE),
							new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR)));

					block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
					gameObjects.addGameObject(block, layer);
					cloudBlocks.add(block);
				}
			}
		}
	}

	// Schedules continuous movement of the cloud blocks to the right
	private void animateCloudMovement(float startX, GameObjectCollection gameObjects) {
		float endX = startX + CLOUD_TRAVEL_DISTANCE;
		float duration = (endX - startX) / CLOUD_SPEED_PX_PER_SEC;

		GameObject scheduler = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		scheduler.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects.addGameObject(scheduler, Layer.BACKGROUND);

		new Transition<>(
				scheduler,
				x -> {
					float deltaX = x - startX;
					for (GameObject block : cloudBlocks) {
						Vector2 oldPos = block.getTopLeftCorner();
						block.setTopLeftCorner(new Vector2(oldPos.x() + deltaX, oldPos.y()));
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
}

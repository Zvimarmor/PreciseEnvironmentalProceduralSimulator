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
 * A class responsible for creating clouds in the sky using a pixel-style block pattern.
 */
public class Cloud {
	private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
	private static final int CLOUD_SIZE = 30;
	private static final float CLOUD_SPEED = 0.25f;

	/**
	 * Create a cloud at a given position using a fixed shape matrix.
	 *
	 * @param topLeftCorner The top-left position to begin cloud.
	 * @param gameObjects The game object collection.
	 * @param layer The layer to draw cloud blocks on.
	 */
	public static void createCloud(Vector2 topLeftCorner,
								   GameObjectCollection gameObjects,
								   int layer) {
		List<List<Integer>> cloudMatrix = List.of(
				List.of(0, 1, 1, 0, 0, 0),
				List.of(1, 1, 1, 1, 1, 1),
				List.of(1, 1, 1, 1, 1, 1),
				List.of(0, 1, 1, 1, 0, 0),
				List.of(0, 0, 0, 0, 0, 0)
		);

		List<GameObject> cloudBlocks = new ArrayList<>();
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

		new Transition<>(
				scheduler,
				x -> {
					for (GameObject block : cloudBlocks) {
						float deltaX = x - startX;
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
}

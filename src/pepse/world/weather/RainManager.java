package pepse.world.weather;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RainManager {
	private static final int RAIN_DROP_WIDTH = 3;
	private static final int RAIN_DROP_HEIGHT = 20;
	private static final Color DROP_COLOR = new Color(180, 180, 255);
	private static final float DROP_SPEED = 300f;
	private static final int DROP_COUNT_PER_CLOUD = 20;
	private static final float RAIN_DURATION = 5f;

	private static boolean raining = false;

	/**
	 * Trigger rain from the given cloud positions.
	 *
	 * @param cloudCenters List of cloud center positions.
	 * @param gameObjects Game object collection to add rain drops into.
	 */
	public static void triggerRain(List<Vector2> cloudCenters,
											 GameObjectCollection gameObjects) {
		raining = true;
		List<GameObject> drops = new ArrayList<>();
		Random rand = new Random();

		for (Vector2 cloudCenter : cloudCenters) {
			for (int i = 0; i < DROP_COUNT_PER_CLOUD; i++) {
				float x = cloudCenter.x() - 40 + rand.nextFloat() * 80; // drop within a range of cloud
				float y = cloudCenter.y() + rand.nextFloat() * 10 - 5;  // small jitter near cloud base

				GameObject drop = new GameObject(
						new Vector2(x, y),
						new Vector2(RAIN_DROP_WIDTH, RAIN_DROP_HEIGHT),
						new RectangleRenderable(DROP_COLOR)
				);
				drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
				gameObjects.addGameObject(drop);

				new Transition<Float>(
						drop,
						dy -> drop.setCenter(new Vector2(drop.getCenter().x(), dy)),
						y,
						y + 600,
						Transition.LINEAR_INTERPOLATOR_FLOAT,
						RAIN_DURATION,
						Transition.TransitionType.TRANSITION_ONCE,
						() -> gameObjects.removeGameObject(drop)
				);

				drops.add(drop);
			}
		}

		// After rain ends, reset flag
		if (!drops.isEmpty()) {
			new ScheduledTask(
					drops.get(0),
					RAIN_DURATION,
					false,
					() -> raining = false
			);
		}
	}

	public static boolean isRaining() {
		return raining;
	}
}
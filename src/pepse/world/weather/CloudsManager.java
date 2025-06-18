package pepse.world.weather;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.util.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for managing cloud spawning and triggering rain from cloud positions.
 */
public class CloudsManager {
	private static final float CLOUD_INTERVAL = 10f;
	private static final float CLOUD_MIN_X = -350f;
	private static final float CLOUD_MAX_X_OFFSET = 300f;
	private static final float CLOUD_Y_HEIGHT = 100f;

	private final GameObjectCollection gameObjects;
	private final Vector2 windowDimensions;
	private final int layer;
	private final List<Vector2> cloudCenters = new ArrayList<>();
	private final Random random = new Random();

	public CloudsManager(GameObjectCollection gameObjects, Vector2 windowDimensions, int layer) {
		this.gameObjects = gameObjects;
		this.windowDimensions = windowDimensions;
		this.layer = layer;
	}

	public void startSpawningClouds() {
		GameObject scheduler = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		scheduler.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects.addGameObject(scheduler, layer);

		new ScheduledTask(
				scheduler,
				CLOUD_INTERVAL,
				true,
				() -> {
					Vector2 center = randomCloudPosition();
					Cloud.createCloud(center, gameObjects, layer);
					cloudCenters.add(center);
				}
		);
	}

	public void triggerRainFromClouds() {
		RainManager.triggerRain(cloudCenters, gameObjects);
	}

	private Vector2 randomCloudPosition() {
		float x = CLOUD_MIN_X + random.nextFloat() * CLOUD_MAX_X_OFFSET;
		return new Vector2(x, CLOUD_Y_HEIGHT);
	}
}

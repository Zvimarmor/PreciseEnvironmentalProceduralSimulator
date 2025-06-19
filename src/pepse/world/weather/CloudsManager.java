package pepse.world.weather;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.JumpObserver;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Responsible for managing cloud spawning and triggering continuous rain from cloud positions.
 */
public class CloudsManager implements JumpObserver {
	private static final float CLOUD_INTERVAL = 10f;
	private static final float CLOUD_MIN_HEIGHT = 20f;
	private static final float CLOUD_MAX_HEIGHT = 300f;

	private static final int RAIN_DROP_WIDTH = 3;
	private static final int RAIN_DROP_HEIGHT = 7;
	private static final Color DROP_COLOR = new Color(45, 74, 214);
	private static final float DROP_FALL_DISTANCE = 800f;
	private static final float DROP_DURATION = 5f;
	private static final float DROP_INTERVAL = 0.1f;
	private static final float SPREAD_RADIUS = 80f;

	private final GameObjectCollection gameObjects;
	private final Vector2 windowDimensions;
	private final int layer;
	private final List<Cloud> clouds = new ArrayList<>();
	private final Random random = new Random();
	private boolean raining = false;
	private GameObject rainScheduler;

	/**
	 * Constructs the CloudsManager responsible for spawning and animating clouds and rain.
	 *
	 * @param gameObjects The global game object collection.
	 * @param windowDimensions Dimensions of the window.
	 * @param layer The layer on which clouds and rain will be drawn.
	 */
	public CloudsManager(GameObjectCollection gameObjects, Vector2 windowDimensions, int layer) {
		this.gameObjects = gameObjects;
		this.windowDimensions = windowDimensions;
		this.layer = layer;
	}

	/**
	 * Starts spawning clouds periodically across the screen.
	 */
	public void startSpawningClouds() {
		GameObject scheduler = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		scheduler.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects.addGameObject(scheduler, layer);

		new ScheduledTask(
				scheduler,
				CLOUD_INTERVAL,
				true,
				() -> {
					float cloudHeight = CLOUD_MIN_HEIGHT + (CLOUD_MAX_HEIGHT - CLOUD_MIN_HEIGHT) * random.nextFloat();
					Vector2 center = new Vector2(-500, cloudHeight); // Starts offscreen
					Cloud newCloud = new Cloud(center, gameObjects, layer);
					clouds.add(newCloud);
				}
		);
	}

	/**
	 * When avatar jumps, initiates a short rain burst from all clouds.
	 */
	@Override
	public void onJump() {
		if (raining) return;
		raining = true;

		rainScheduler = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		rainScheduler.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects.addGameObject(rainScheduler);

		new ScheduledTask(
				rainScheduler,
				DROP_INTERVAL,
				true,
				() -> {
					for (Cloud cloud : clouds) {
						Vector2 pos = cloud.getCurrentBottomCenter();
						spawnSingleDrop(pos);
					}
				}
		);

		new ScheduledTask(
				rainScheduler,
				2f,
				false,
				() -> {
					gameObjects.removeGameObject(rainScheduler);
					raining = false;
				}
		);
	}

	/**
	 * Spawns a single raindrop with slight horizontal randomness.
	 *
	 * @param baseCenter Position from which the drop will start falling.
	 */
	private void spawnSingleDrop(Vector2 baseCenter) {
		float offsetX = (random.nextFloat() - 0.5f) * 2 * SPREAD_RADIUS;
		float x = baseCenter.x() + offsetX;
		float y = baseCenter.y();

		GameObject drop = new GameObject(
				new Vector2(x, y),
				new Vector2(RAIN_DROP_WIDTH, RAIN_DROP_HEIGHT),
				new RectangleRenderable(DROP_COLOR)
		);
		drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects.addGameObject(drop, Layer.BACKGROUND);

		// Animate vertical descent and remove drop at the end
		new danogl.components.Transition<>(
				drop,
				dy -> drop.setCenter(new Vector2(drop.getCenter().x(), dy)),
				y,
				y + DROP_FALL_DISTANCE,
				danogl.components.Transition.LINEAR_INTERPOLATOR_FLOAT,
				DROP_DURATION,
				danogl.components.Transition.TransitionType.TRANSITION_ONCE,
				() -> gameObjects.removeGameObject(drop, Layer.BACKGROUND)
		);
	}
}

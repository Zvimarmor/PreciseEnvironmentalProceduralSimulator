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
	private static final String RAIN_TAG = "rain";

	private static final float CLOUD_INTERVAL_SEC = 10f;
	private static final float CLOUD_MIN_HEIGHT_PX = 20f;
	private static final float CLOUD_MAX_HEIGHT_PX = 300f;

	private static final int DROP_WIDTH_PX = 3;
	private static final int DROP_HEIGHT_PX = 7;
	private static final Color DROP_COLOR = new Color(45, 74, 214);
	private static final float DROP_FALL_DISTANCE_PX = 800f;
	private static final float DROP_DURATION_SEC = 5f;
	private static final float DROP_INTERVAL_SEC = 0.05f;
	private static final float DROP_SPREAD_RADIUS_PX = 80f;
	private static final float RAIN_DURATION_SEC = 2f;
	private static final float OFFSCREEN_SPAWN_X = -500f;

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
	 * @param gameObjects       The global game object collection.
	 * @param windowDimensions  Dimensions of the window.
	 * @param layer             The layer on which clouds and rain will be drawn.
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
				CLOUD_INTERVAL_SEC,
				true,
				() -> {
					float cloudHeight = CLOUD_MIN_HEIGHT_PX +
							(CLOUD_MAX_HEIGHT_PX - CLOUD_MIN_HEIGHT_PX) * random.nextFloat();
					Vector2 center = new Vector2(OFFSCREEN_SPAWN_X, cloudHeight);
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

		// Schedule continuous drop spawning during rain
		new ScheduledTask(
				rainScheduler,
				DROP_INTERVAL_SEC,
				true,
				() -> {
					for (Cloud cloud : clouds) {
						Vector2 pos = cloud.getCurrentBottomCenter();
						spawnSingleDrop(pos);
					}
				}
		);

		// End rain after fixed time
		new ScheduledTask(
				rainScheduler,
				RAIN_DURATION_SEC,
				false,
				() -> {
					gameObjects.removeGameObject(rainScheduler);
					raining = false;
				}
		);
	}

	// Spawns a single drop from a given cloud base center, including fall and fade-out animation
	private void spawnSingleDrop(Vector2 baseCenter) {
		float offsetX = (random.nextFloat() - 0.5f) * 2 * DROP_SPREAD_RADIUS_PX;
		float x = baseCenter.x() + offsetX;
		float y = baseCenter.y();

		RectangleRenderable renderable = new RectangleRenderable(DROP_COLOR);
		GameObject drop = new GameObject(
				new Vector2(x, y),
				new Vector2(DROP_WIDTH_PX, DROP_HEIGHT_PX),
				renderable
		);
		drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects.addGameObject(drop, Layer.BACKGROUND);

		// Animate drop falling
		new danogl.components.Transition<>(
				drop,
				dy -> drop.setCenter(new Vector2(drop.getCenter().x(), dy)),
				y,
				y + DROP_FALL_DISTANCE_PX,
				danogl.components.Transition.LINEAR_INTERPOLATOR_FLOAT,
				DROP_DURATION_SEC,
				danogl.components.Transition.TransitionType.TRANSITION_ONCE,
				null
		);

		// Animate drop transparency fading out
		new danogl.components.Transition<>(
				drop,
				alpha -> drop.renderer().setOpaqueness(alpha),
				1f,
				0f,
				danogl.components.Transition.LINEAR_INTERPOLATOR_FLOAT,
				DROP_DURATION_SEC,
				danogl.components.Transition.TransitionType.TRANSITION_ONCE,
				() -> gameObjects.removeGameObject(drop, Layer.BACKGROUND)
		);
	}
}

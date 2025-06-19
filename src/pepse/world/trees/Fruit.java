package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;

/**
 * Represents a floating fruit that grants energy and regenerates after consumption.
 */
public class Fruit extends GameObject {
	private static final float FRUIT_DIAMETER = 25f;
	private static final Color[] FRUIT_COLORS = {
			new Color(255, 100, 100),
			new Color(255, 165, 0)
	};
	private static final int ENERGY_GAIN = 10;
	private static final float RESPAWN_TIME = 30f;
	public static final String FRUIT_TAG = "fruit";

	private final GameObjectCollection gameObjects;
	private final Vector2 spawnPosition;
	private final GameObject fruitRestorer;

	/**
	 * Constructs a fruit at the specified position.
	 *
	 * @param position    Center position of the fruit.
	 * @param gameObjects The collection to manage fruit lifecycle.
	 */
	public Fruit(Vector2 position, GameObjectCollection gameObjects) {
		super(position,
				new Vector2(FRUIT_DIAMETER, FRUIT_DIAMETER),
				new OvalRenderable(ColorSupplier.approximateColor(
						FRUIT_COLORS[new Random().nextInt(FRUIT_COLORS.length)])
				));
		this.gameObjects = gameObjects;
		this.spawnPosition = new Vector2(position.x() + FRUIT_DIAMETER / 2f,
				position.y() + FRUIT_DIAMETER / 2f);
		this.setTag(FRUIT_TAG);
		this.fruitRestorer = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		gameObjects.addGameObject(this.fruitRestorer);
		initializeFloatingEffect(); // Add bouncing animation
	}

	/**
	 * Returns the amount of energy gained from consuming the fruit.
	 *
	 * @return Energy units granted by the fruit.
	 */
	public int getEnergyGain() {
		return ENERGY_GAIN;
	}

	/**
	 * Triggers the fruit's removal and schedules its reappearance.
	 */
	public void consume() {
		gameObjects.removeGameObject(this, Layer.STATIC_OBJECTS);
		new ScheduledTask(fruitRestorer, RESPAWN_TIME, false, this::respawn);
	}

	/**
	 * Repositions and re-adds the fruit after a delay.
	 */
	private void respawn() {
		this.setCenter(spawnPosition);
		gameObjects.addGameObject(this, Layer.STATIC_OBJECTS);
	}

	/**
	 * Applies a vertical floating animation for visual interest.
	 */
	private void initializeFloatingEffect() {
		new Transition<>(
				this,
				yOffset -> this.setCenter(new Vector2(spawnPosition.x(), spawnPosition.y() + yOffset)),
				-3f,
				3f,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				2f,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);
	}
}

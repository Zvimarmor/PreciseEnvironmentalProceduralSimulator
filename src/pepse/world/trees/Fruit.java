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
 * Represents a fruit that appears on or near trees and can be collected by the avatar.
 */
public class Fruit extends GameObject {
	private static final float FRUIT_DIAMETER = 25f;
	private static final Color FRUIT_COLOR_RED = new Color(255, 100, 100);
	private static final Color FRUIT_COLOR_ORANGE = new Color(255, 165, 0);
	private static final Color[] FRUIT_COLORS = {FRUIT_COLOR_RED, FRUIT_COLOR_ORANGE};
	//Amount of energy given to avatar when consuming the fruit.
	private static final int ENERGY_GAIN = 10;
	//Time before fruit respawns after consumption.
	private static final float RESPAWN_TIME_SECONDS = 30f;

	private static final String FRUIT_TAG = "fruit";
	//vertical floating animation
	private static final float FLOAT_AMPLITUDE = 3f;
	private static final float FLOAT_CYCLE_DURATION = 2f;

	private final GameObjectCollection gameObjects;
	private final Vector2 spawnPosition;
	private final GameObject fruitRestorer;

	/**
	 * Constructs a fruit object.
	 *
	 * @param position     Position where fruit will appear.
	 * @param gameObjects  Game object collection (for adding/removing).
	 */
	public Fruit(Vector2 position, GameObjectCollection gameObjects) {
		super(position,
				new Vector2(FRUIT_DIAMETER, FRUIT_DIAMETER),
				new OvalRenderable(ColorSupplier.approximateColor(
						FRUIT_COLORS[new Random().nextInt(FRUIT_COLORS.length)])));

		this.gameObjects = gameObjects;
		this.spawnPosition = new Vector2(
				position.x() + FRUIT_DIAMETER / 2f,
				position.y() + FRUIT_DIAMETER / 2f);

		setTag(FRUIT_TAG);

		this.fruitRestorer = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		gameObjects.addGameObject(this.fruitRestorer);

		initializeFloatingEffect();
	}

	/**
	 * @return The amount of energy gained when consuming the fruit.
	 */
	public int getEnergyGain() {
		return ENERGY_GAIN;
	}

	/**
	 * Called when the avatar collects the fruit. Schedules respawn.
	 */
	public void consume() {
		gameObjects.removeGameObject(this, Layer.STATIC_OBJECTS);

		new ScheduledTask(
				fruitRestorer,
				RESPAWN_TIME_SECONDS,
				false,
				this::respawn
		);
	}

	/**
	 * Respawn fruit at its original position.
	 */
	private void respawn() {
		setCenter(spawnPosition);
		gameObjects.addGameObject(this, Layer.STATIC_OBJECTS);
	}

	/**
	 * Makes fruit float up and down in a loop for visual flair.
	 */
	private void initializeFloatingEffect() {
		new Transition<>(
				this,
				yOffset -> setCenter(new Vector2(
						spawnPosition.x(),
						spawnPosition.y() + yOffset)),
				-FLOAT_AMPLITUDE,
				FLOAT_AMPLITUDE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				FLOAT_CYCLE_DURATION,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);
	}
}

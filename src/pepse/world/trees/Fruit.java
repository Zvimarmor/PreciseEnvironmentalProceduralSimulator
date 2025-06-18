package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
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
	private static final Color[] FRUIT_COLORS = {new Color(255, 100, 100), new Color(255, 165, 0)};
	private static final int ENERGY_GAIN = 10;
	private static final float RESPAWN_TIME = 30f;
	public static final String FRUIT_TAG = "fruit";

	private final GameObjectCollection gameObjects;
	private final Vector2 spawnPosition;
	private GameObject fruitRestorer;


	/**
	 * Constructs a fruit object.
	 *
	 * @param position Position where fruit will appear.
	 * @param gameObjects Game object collection (for adding/removing).
	 */
	public Fruit(Vector2 position, GameObjectCollection gameObjects) {
		super(position, new Vector2(FRUIT_DIAMETER, FRUIT_DIAMETER),
				new OvalRenderable(ColorSupplier.approximateColor(
						FRUIT_COLORS[new Random().nextInt(FRUIT_COLORS.length)])));
		this.gameObjects = gameObjects;
		this.spawnPosition = new Vector2(position.x() + FRUIT_DIAMETER/2, position.y() + FRUIT_DIAMETER/2);
		this.setTag(FRUIT_TAG);
		this.fruitRestorer = new GameObject(Vector2.ZERO, Vector2.ZERO, null);
		gameObjects.addGameObject(this.fruitRestorer);

		// add bouncing transition (for the style of course)
		initializeFloatingEffect();
	}

	/**
	 * Called when the avatar collects the fruit.
	 */
	public int getEnergyGain() {
		return ENERGY_GAIN;
	}

	public void consume() {
		gameObjects.removeGameObject(this, Layer.STATIC_OBJECTS);
		// Schedule it to reappear

		new ScheduledTask(
				fruitRestorer,
				RESPAWN_TIME,
				false,
				() -> respawn()
		);
	}

	/**
	 * Respawn fruit at its original position.
	 */
	private void respawn() {
		this.setCenter(spawnPosition);
		gameObjects.addGameObject(this, Layer.STATIC_OBJECTS);
	}

	/**
	 * Makes fruit float up and down in a loop for style.
	 */
	private void initializeFloatingEffect() {
		new Transition<Float>(
				this,
				yOffset -> this.setCenter(new Vector2(
						spawnPosition.x(),
						spawnPosition.y() + yOffset)),
				-3f,
				3f,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				2f,
				Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
				null
		);
	}
}
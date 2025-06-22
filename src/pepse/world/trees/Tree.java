package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Terrain;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Generates a single tree consisting of a trunk, leaves, and optional fruit.
 */
public class Tree {
	private static final Color TRUNK_COLOR = new Color(100, 50, 20);
	private static final int TRUNK_WIDTH = 30;
	private static final int LEAF_SIZE = 25;
	private static final int MIN_TRUNK_HEIGHT = 150;
	private static final int MAX_TRUNK_HEIGHT = 200;
	private static final Vector2 LEAF_SPREAD_DIMENSION = new Vector2(150, 200);
	/** Probability to generate a leaf in a given position. */
	private static final float LEAF_PROBABILITY = 0.9f;
	/** Probability to generate a fruit in a given position. */
	private static final float FRUIT_PROBABILITY = 0.2f;

	/**
	 * Create a tree at the specified x-coordinate.
	 *
	 * @param x           X-position to plant the tree.
	 * @param terrain     Terrain used to find ground height.
	 * @param gameObjects The collection to place trunk, leaves, and fruits.
	 * @param layer       Rendering layer to use.
	 */
	public static void create(float x, Terrain terrain,
							  GameObjectCollection gameObjects,
							  int layer) {
		Random rand = new Random();

		float trunkHeight = rand.nextInt(MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
		float groundHeight = terrain.groundHeightAt(x);
		float trunkTop = groundHeight - trunkHeight;

		// Create the trunk and place it
		GameObject trunk = new GameObject(
				new Vector2(x, trunkTop),
				new Vector2(TRUNK_WIDTH, trunkHeight),
				new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR))
		);
		trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
		gameObjects.addGameObject(trunk, layer);

		// Generate grid of possible positions for leaves and fruit
		int cols = (int) (LEAF_SPREAD_DIMENSION.x() / LEAF_SIZE);
		int rows = (int) (LEAF_SPREAD_DIMENSION.y() / LEAF_SIZE);
		float startX = x - LEAF_SPREAD_DIMENSION.x() / 2;
		float startY = trunkTop - LEAF_SPREAD_DIMENSION.y() / 2;

		ArrayList<Vector2> possibleLeafPositions = new ArrayList<>();
		ArrayList<Vector2> possibleFruitPositions = new ArrayList<>();

		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				float px = startX + col * LEAF_SIZE;
				float py = startY + row * LEAF_SIZE;
				Vector2 pos = new Vector2(px, py);
				if (rand.nextBoolean()) {
					possibleLeafPositions.add(pos);
				} else {
					possibleFruitPositions.add(pos);
				}
			}
		}

		// Add leaves
		Collections.shuffle(possibleLeafPositions, rand);
		int numLeaves = (int) (LEAF_PROBABILITY * possibleLeafPositions.size());
		for (int i = 0; i < numLeaves; i++) {
			Leaf leaf = new Leaf(possibleLeafPositions.get(i));
			gameObjects.addGameObject(leaf, layer);
		}

		// Add fruits
		Collections.shuffle(possibleFruitPositions, rand);
		int numFruits = (int) (FRUIT_PROBABILITY * possibleFruitPositions.size());
		for (int i = 0; i < numFruits; i++) {
			Fruit fruit = new Fruit(possibleFruitPositions.get(i), gameObjects);
			gameObjects.addGameObject(fruit, Layer.STATIC_OBJECTS);
		}
	}
}

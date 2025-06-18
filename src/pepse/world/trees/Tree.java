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
 * A class responsible for generating a single tree with a trunk and several leaves.
 */
public class Tree {
	private static final Color TRUNK_COLOR = new Color(100, 50, 20);
	private static final int TRUNK_WIDTH = 30;
	private static final int LEAF_SIZE = 25;
	private static final int MIN_TRUNK_HEIGHT = 150;
	private static final int MAX_TRUNK_HEIGHT = 200;
	private static final Vector2 LEAF_SPREAD_DIMENSION = new Vector2(150, 200);
	private static final float LEAF_PROBABILITY = 0.8f;
	private static final float FRUIT_PROBABILITY = 0.2f;

	/**
	 * Create a tree at a given x-coordinate based on terrain height.
	 *
	 * @param x The x position to plant the tree.
	 * @param terrain The terrain object used to determine ground height.
	 * @param gameObjects The collection to which all parts of the tree are added.
	 * @param layer The layer to draw the tree on.
	 */
	public static void create(float x, Terrain terrain,
							  GameObjectCollection gameObjects,
							  int layer) {
		Random rand = new Random();

		float trunkHeight = rand.nextInt(MIN_TRUNK_HEIGHT, MAX_TRUNK_HEIGHT);
		float groundHeight = terrain.groundHeightAt(x);
		float trunkTop = groundHeight - trunkHeight;

		// Create and add the trunk
		GameObject trunk = new GameObject(new Vector2(x, trunkTop),
				new Vector2(TRUNK_WIDTH, trunkHeight),
				new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR)));
		trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
		gameObjects.addGameObject(trunk, layer);

		// Create list of all valid leaf positions (excluding trunk area)
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

		// Shuffle and pick leaf positions
		Collections.shuffle(possibleLeafPositions, rand);
		int numLeaves = (int) (LEAF_PROBABILITY * possibleLeafPositions.size());
		for (int i = 0; i < numLeaves; i++) {
			Vector2 pos = possibleLeafPositions.get(i);
			Leaf leaf = new Leaf(pos);
			gameObjects.addGameObject(leaf, layer);
		}

		// Shuffle and pick fruit positions
		Collections.shuffle(possibleFruitPositions, rand);
		int numFruits = (int) (FRUIT_PROBABILITY * possibleFruitPositions.size());
		for (int i = 0; i < numFruits; i++) {
			Vector2 pos = possibleFruitPositions.get(i);
			Fruit fruit = new Fruit(pos, gameObjects);
			gameObjects.addGameObject(fruit, Layer.STATIC_OBJECTS);
		}

	}

}
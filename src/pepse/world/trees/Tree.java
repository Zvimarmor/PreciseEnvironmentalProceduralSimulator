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
	private static final int MIN_TRUNK_HEIGHT = 200;
	private static final int MAX_TRUNK_HEIGHT = 300;
	private static final Vector2 LEAF_SPREAD_DIMENSION = new Vector2(150, 200);
	private static final float LEAF_PROBABILITY = 0.7f;
	private static final float FRUIT_PROBABILITY = 0.1f;

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

		int trunkHeight = MIN_TRUNK_HEIGHT + rand.nextInt(MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT + 1);
		float groundHeight = terrain.groundHeightAt(x);
		Vector2 trunkTop = new Vector2(x, groundHeight - trunkHeight);

		// Create and add the trunk
		GameObject trunk = new GameObject(new Vector2(x, trunkTop.y()),
				new Vector2(TRUNK_WIDTH, trunkHeight),
				new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR)));
		trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		trunk.physics().preventIntersectionsFromDirection(Vector2.UP);
		gameObjects.addGameObject(trunk, layer);

		// Create list of all valid leaf positions (excluding trunk area)
		int cols = (int) (LEAF_SPREAD_DIMENSION.x() / LEAF_SIZE);
		int rows = (int) (LEAF_SPREAD_DIMENSION.y() / LEAF_SIZE);
		float startX = x - LEAF_SPREAD_DIMENSION.x() / 2;
		float startY = trunkTop.y() - LEAF_SPREAD_DIMENSION.y() / 2;

		ArrayList<Vector2> possibleLeafPositions = new ArrayList<>();
		System.out.println( "rows:" + rows + " cols:" + cols);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				float leafX = startX + col * LEAF_SIZE;
				float leafY = startY + row * LEAF_SIZE;

				// Skip positions overlapping the trunk
				if (leafX + LEAF_SIZE > x && leafX < x + LEAF_SIZE) {
					continue;
				}
				possibleLeafPositions.add(new Vector2(leafX, leafY));
			}
		}

		// Shuffle and select a subset according to probability
		Collections.shuffle(possibleLeafPositions, rand);
		int totalPossible = possibleLeafPositions.size();
		int numLeaves = (int) (LEAF_PROBABILITY * totalPossible);

		for (int i = 0; i < numLeaves; i++) {
			Vector2 pos = possibleLeafPositions.get(i);
			Leaf leaf = new Leaf(pos);
			gameObjects.addGameObject(leaf, layer);
			if (rand.nextFloat() < FRUIT_PROBABILITY) {
				Fruit fruit = new Fruit(pos, gameObjects);
				gameObjects.addGameObject(fruit, Layer.STATIC_OBJECTS);
			}
		}
	}
}

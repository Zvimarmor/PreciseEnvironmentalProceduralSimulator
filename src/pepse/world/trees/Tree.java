package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;

/**
 * A class responsible for generating a single tree with a trunk and several leaves.
 */
public class Tree {
	private static final Color TRUNK_COLOR = new Color(100, 50, 20);
	private static final int TRUNK_WIDTH = 10;
	private static final int TRUNK_HEIGHT_RANGE = 100;
	private static final int NUM_LEAVES = 5;
	private static final float LEAF_SPREAD_RADIUS = 30f;

	/**
	 * Create a tree at a given x-coordinate based on terrain height.
	 *
	 * @param x The x position to plant the tree.
	 * @param terrain The terrain object used to determine ground height.
	 * @param gameObjects The collection to which all parts of the tree are added.
	 * @param layer The layer to draw the tree on.
	 * @param seed A seed for deterministic leaf placement.
	 */
	public static void create(float x, Terrain terrain,
							  GameObjectCollection gameObjects,
							  int layer, int seed) {
		Random rand = new Random(seed);

		int TRUNK_HEIGHT = rand.nextInt(TRUNK_HEIGHT_RANGE);

		float groundHeight = terrain.groundHeightAt(x);
		Vector2 trunkTop = new Vector2(x, groundHeight - TRUNK_HEIGHT);

		// Create and add the trunk
		GameObject trunk = new GameObject(new Vector2(x,terrain.groundHeightAt(x) - TRUNK_HEIGHT),
				new Vector2(TRUNK_WIDTH,TRUNK_HEIGHT),
				new RectangleRenderable(TRUNK_COLOR));
		gameObjects.addGameObject(trunk, layer);

		// Create and add leaves around the top of the trunk
		for (int i = 0; i < NUM_LEAVES; i++) {
			float offsetX = (rand.nextFloat() - 0.5f) * LEAF_SPREAD_RADIUS * 2;
			float offsetY = -rand.nextFloat() * LEAF_SPREAD_RADIUS;
			Vector2 leafPos = trunkTop.add(new Vector2(offsetX, offsetY));

			Leaf leaf = new Leaf(leafPos);
			gameObjects.addGameObject(leaf, Layer.BACKGROUND);
		}
	}
}

package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import pepse.world.Terrain;

import java.util.Random;

/**
 * A utility class responsible for planting trees in a forest-like pattern.
 */
public class Forest {
	private static final float TREE_PLANT_PROBABILITY = 0.1f; // 10% chance to place a tree
	private static final int TREE_DISTANCE = 30; // Space between tree attempts

	/**
	 * Plants a forest in the range [minX, maxX) based on a fixed spacing and random probability.
	 *
	 * @param minX Start x-coordinate of forest.
	 * @param maxX End x-coordinate (exclusive).
	 * @param terrain Terrain to determine the ground height at each x.
	 * @param gameObjects Where to place the tree game objects.
	 * @param layer The rendering layer to use for the trees.
	 */
	public static void createForest(int minX, int maxX, Terrain terrain,
									GameObjectCollection gameObjects, int layer) {
		Random rand = new Random();
		for (int x = minX; x < maxX; x += TREE_DISTANCE) {
			if (rand.nextFloat() < TREE_PLANT_PROBABILITY) {
				Tree.create(x, terrain, gameObjects, layer);
			}
		}
	}
}

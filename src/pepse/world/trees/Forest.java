package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import pepse.world.Terrain;

import java.util.Random;

/**
 * A utility class to generate a forest of trees across the game world.
 */
public class Forest {
	private static final float TREE_PLANT_PROBABILITY = 0.1f; // 10% chance
	private static final int TREE_DISTANCE = 5;

	/**
	 * Creates trees across the given range of x values, using deterministic seeding.
	 *
	 * @param minX        The starting x value.
	 * @param maxX        The ending x value (exclusive).
	 * @param terrain     The terrain object to determine ground height.
	 * @param gameObjects The game object collection to add the trees into.
	 */
	public static void createForest(int minX, int maxX, Terrain terrain,
									GameObjectCollection gameObjects) {
		Random rand = new Random();
		int count = 0;
		for (int x = minX; x < maxX; x += TREE_DISTANCE) {
			float chance = rand.nextFloat();
			if (chance < TREE_PLANT_PROBABILITY) {
				int treeSeed = rand.nextInt();
				Tree.create(x, terrain, gameObjects, Layer.STATIC_OBJECTS, treeSeed);
			}
		}
	}
}
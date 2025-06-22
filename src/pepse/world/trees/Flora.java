package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import pepse.world.Terrain;

import java.util.Random;

/**
 * A class responsible for planting trees in a forest-like pattern across a horizontal range.
 */
public class Flora {
	/** Probability for planting a tree at each interval. */
	private static final float TREE_PLANT_PROBABILITY = 0.1f;

	/** Distance between tree planting positions (in pixels). */
	private static final int TREE_DISTANCE = 30;

	private final Terrain terrain;
	private final GameObjectCollection gameObjects;
	private final int layer;
	private final Random rand;

	/**
	 * Constructs a Forest instance that can plant trees using the given dependencies.
	 *
	 * @param terrain Terrain object to determine tree height.
	 * @param gameObjects Game object collection to add trees to.
	 * @param layer Layer on which to place the trees.
	 */
	public Flora(Terrain terrain, GameObjectCollection gameObjects, int layer) {
		this.terrain = terrain;
		this.gameObjects = gameObjects;
		this.layer = layer;
		this.rand = new Random();
	}

	/**
	 * Plants trees in the range [minX, maxX) using fixed intervals and probability.
	 *
	 * @param minX Start of the horizontal range (inclusive).
	 * @param maxX End of the horizontal range (exclusive).
	 */
	public void createInRange(int minX, int maxX) {
		for (int x = minX; x < maxX; x += TREE_DISTANCE) {
			if (rand.nextFloat() < TREE_PLANT_PROBABILITY) {
				Tree.create(x, terrain, gameObjects, layer);
			}
		}
	}
}

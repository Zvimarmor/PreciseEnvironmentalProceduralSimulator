package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for generating terrain blocks in a procedurally defined landscape.
 */
public class Terrain {
	/** Width and height of a single terrain block. */
	public static final int BLOCK_SIZE = 30;

	private static final int TERRAIN_DEPTH = 20;
	private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
	private static final String GROUND_TAG = "ground";

	private final NoiseGenerator noise;
	private final Vector2 windowDimensions;
	private final float groundHeightAtX0;

	/**
	 * Constructs a Terrain instance with the given screen size and seed.
	 *
	 * @param windowDimensions Dimensions of the game window.
	 * @param seed             Seed for terrain noise generation.
	 */
	public Terrain(Vector2 windowDimensions, int seed) {
		this.windowDimensions = windowDimensions;
		this.groundHeightAtX0 = windowDimensions.y() * 2 / 3f;
		this.noise = new NoiseGenerator((double) seed, (int) groundHeightAtX0);
	}

	/**
	 * Computes terrain height (y value) at a specific x-coordinate.
	 *
	 * @param x The x-coordinate.
	 * @return The corresponding ground height.
	 */
	public float groundHeightAt(float x) {
		return groundHeightAtX0 - (float) noise.noise(x, BLOCK_SIZE * 7);
	}

	/**
	 * Generates a list of terrain blocks in a horizontal range.
	 *
	 * @param minX The minimum x-coordinate (inclusive).
	 * @param maxX The maximum x-coordinate (inclusive).
	 * @return A list of Block objects representing terrain in the given range.
	 */
	public List<Block> createInRange(int minX, int maxX) {
		List<Block> blocks = new ArrayList<>();

		int alignedMinX = (int) Math.floor((float) minX / BLOCK_SIZE) * BLOCK_SIZE;
		int alignedMaxX = (int) Math.ceil((float) maxX / BLOCK_SIZE) * BLOCK_SIZE;

		for (int x = alignedMinX; x <= alignedMaxX; x += BLOCK_SIZE) {
			float groundHeight = (float)
					(Math.floor(groundHeightAt(x) / BLOCK_SIZE) * BLOCK_SIZE);

			for (int i = 0; i < TERRAIN_DEPTH; i++) {
				float y = groundHeight + i * BLOCK_SIZE;
				Vector2 blockTopLeftCorner = new Vector2(x, y);
				Renderable blockRenderable =
						new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
				Block block = new Block(blockTopLeftCorner, blockRenderable);
				block.setTag(GROUND_TAG);
				blocks.add(block);
			}
		}

		return blocks;
	}
}

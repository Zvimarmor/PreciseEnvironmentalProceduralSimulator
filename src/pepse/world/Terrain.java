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
 * Represents the terrain system, generating procedural ground blocks.
 */
public class Terrain {

	public static final int BLOCK_SIZE = 30;
	private static final int TERRAIN_DEPTH = 20;
	private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
	private static final String GROUND_TAG = "ground";
	private static final float NOISE_FACTOR = BLOCK_SIZE * 7;

	private final NoiseGenerator noiseGenerator;
	private final Vector2 windowDimensions;
	private final float groundHeightAtX0;

	/**
	 * Constructs a Terrain instance, initializing the NoiseGenerator and base terrain height.
	 *
	 * @param windowDimensions The dimensions of the game window.
	 * @param seed             Seed value for deterministic terrain generation.
	 */
	public Terrain(Vector2 windowDimensions, int seed) {
		this.windowDimensions = windowDimensions;
		this.groundHeightAtX0 = windowDimensions.y() * 2 / 3;
		this.noiseGenerator = new NoiseGenerator((double) seed, (int) groundHeightAtX0);
	}

	/**
	 * Returns the terrain height at a given x-coordinate.
	 *
	 * @param x The x-coordinate to query.
	 * @return The y-coordinate of the terrain at x.
	 */
	public float groundHeightAt(float x) {
		float noise = (float) noiseGenerator.noise(x, BLOCK_SIZE *7);
		return groundHeightAtX0 + noise;
	}

	/**
	 * Generates ground blocks in a specified horizontal range, based on terrain height at each point.
	 *
	 * @param minX The minimum x-coordinate (inclusive).
	 * @param maxX The maximum x-coordinate (inclusive).
	 * @return A list of Block objects representing the terrain in the specified range.
	 */
	public List<Block> createInRange(int minX, int maxX) {
		List<Block> blocks = new ArrayList<>();

		int alignedMinX = (int) (Math.floor((float) minX / BLOCK_SIZE) * BLOCK_SIZE);
		int alignedMaxX = (int) (Math.ceil((float) maxX / BLOCK_SIZE) * BLOCK_SIZE);

		for (int x = alignedMinX; x <= alignedMaxX; x += BLOCK_SIZE) {
			float groundHeight = (float) (Math.floor(groundHeightAt(x) / BLOCK_SIZE) * BLOCK_SIZE);

			for (int i = 0; i < TERRAIN_DEPTH; i++) {
				float y = groundHeight + i * BLOCK_SIZE;
				Vector2 blockTopLeftCorner = new Vector2(x, y);
				Renderable blockRenderable = new RectangleRenderable(
						ColorSupplier.approximateColor(BASE_GROUND_COLOR));
				Block block = new Block(blockTopLeftCorner, blockRenderable);
				block.setTag(GROUND_TAG);
				blocks.add(block);
			}
		}

		return blocks;
	}
}

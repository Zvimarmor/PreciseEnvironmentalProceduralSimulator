package world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Terrain {
	public static final double BLOCK_SIZE = 30;

	public static final int groundHeightAtX0 = 1000000;
	private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
	private static final int TERRAIN_DEPTH = 20;

	private final NoiseGenerator noise;
	private final Vector2 windowDimensions;

	public Terrain(Vector2 windowDimensions, int seed){
		this.windowDimensions = windowDimensions;
		this.noise = new NoiseGenerator(seed, Terrain.groundHeightAtX0);
	}

	public double groundHeightAt(double x) {
		return noise.noise(x, BLOCK_SIZE * 7);
	}

	public List<Block> createInRange(int minX, int maxX){
		List<Block> blocks = new ArrayList<>();
		Renderable renderable = new RectangleRenderable(
				ColorSupplier.approximateColor(BASE_GROUND_COLOR));

		int alignedMinX = (int)(Math.floor(minX / BLOCK_SIZE) * BLOCK_SIZE);
		int alignedMaxX = (int)(Math.ceil(maxX / BLOCK_SIZE) * BLOCK_SIZE);

		for (int x = alignedMinX; x <= alignedMaxX; x += BLOCK_SIZE) {
			double groundHeight = Math.floor(groundHeightAt(x) / BLOCK_SIZE) * BLOCK_SIZE;

			for (int i = 0; i < TERRAIN_DEPTH; i++) {
				float y = (float)(groundHeight + i * BLOCK_SIZE);
				Vector2 blockTopLeftCorner = new Vector2(x, y);
				Block block = new Block(blockTopLeftCorner, renderable);
				block.setTag("ground");
				blocks.add(block);
			}
		}

		return blocks;
	}
}

package world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a single block in the terrain.
 * Blocks are immovable game objects with fixed size and rendering.
 */
public class Block extends GameObject {

	/** Fixed block size in pixels. */
	public static final int SIZE = 30;

	/**
	 * Constructs a block at the specified top-left corner with the given renderable.
	 *
	 * @param topLeftCorner The top-left corner of the block.
	 * @param renderable    The visual appearance of the block.
	 */
	public Block(Vector2 topLeftCorner, Renderable renderable) {
		super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
	}
}

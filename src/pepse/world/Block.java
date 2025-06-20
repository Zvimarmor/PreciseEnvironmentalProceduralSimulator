package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a single terrain block in the game world.
 * Each block is a square with fixed size and immovable physics.
 */
public class Block extends GameObject {
	/** The fixed size of each block (in pixels). */
	public static final int BLOCK_SIZE = 30;

	/**
	 * Constructs a new block at the given top-left corner, with the specified renderable.
	 *
	 * @param topLeftCorner The top-left position of the block.
	 * @param renderable    The visual representation of the block.
	 */
	public Block(Vector2 topLeftCorner, Renderable renderable) {
		super(topLeftCorner, Vector2.ONES.mult(BLOCK_SIZE), renderable);
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
	}
}

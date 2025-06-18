package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.world.trees.Fruit;

import java.awt.event.KeyEvent;

/**
 * Avatar character with energy-based movement, gravity, and animation.
 */
public class Avatar extends GameObject {
	private static final float AVATAR_SIZE = 50;

	// Movement constants
	private static final float MOVE_SPEED = 400;
	private static final float JUMP_SPEED = -500;
	private static final float GRAVITY = 500;

	// Energy system
	private static final float MAX_ENERGY = 100;
	private static final float JUMP_ENERGY_COST = 10;
	private static final float MOVE_ENERGY_COST = 0.5f;

	private float energy = MAX_ENERGY;

	private final UserInputListener inputListener;
	private final AnimationRenderable idleAnimation;
	private final AnimationRenderable jumpAnimation;
	private final AnimationRenderable runAnimation;

	private boolean isFacingLeft = false;
	private Runnable onJumpCallback = null;

	/**
	 * Constructor for the Avatar
	 * @param bottomLeftCorner initial position of the avatar
	 * @param inputListener listener for user keyboard input
	 * @param imageReader reader for avatar image
	 */
	public Avatar(Vector2 bottomLeftCorner,
				  UserInputListener inputListener,
				  ImageReader imageReader) {
		super(new Vector2(bottomLeftCorner.x(), bottomLeftCorner.y() - AVATAR_SIZE),
				Vector2.ONES.mult(AVATAR_SIZE),
				imageReader.readImage("assets/idle_0.png", true));

		this.inputListener = inputListener;

		// Prevent overlaps and apply gravity
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		transform().setAccelerationY(GRAVITY);

		// Load animations from assets
		idleAnimation = new AnimationRenderable(
				new ImageRenderable[] {
						imageReader.readImage("assets/idle_0.png", true),
						imageReader.readImage("assets/idle_1.png", true),
						imageReader.readImage("assets/idle_2.png", true),
						imageReader.readImage("assets/idle_3.png", true)
				},
				0.2f);

		jumpAnimation = new AnimationRenderable(
				new ImageRenderable[] {
						imageReader.readImage("assets/jump_0.png", true),
						imageReader.readImage("assets/jump_1.png", true),
						imageReader.readImage("assets/jump_2.png", true),
						imageReader.readImage("assets/jump_3.png", true)
				},
				0.2f);

		runAnimation = new AnimationRenderable(
				new ImageRenderable[] {
						imageReader.readImage("assets/run_0.png", true),
						imageReader.readImage("assets/run_1.png", true),
						imageReader.readImage("assets/run_2.png", true),
						imageReader.readImage("assets/run_3.png", true),
						imageReader.readImage("assets/run_4.png", true),
						imageReader.readImage("assets/run_5.png", true)
				},
				0.1f);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		float xVel = 0;
		boolean isMoving = false;

		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) && energy >= MOVE_ENERGY_COST) {
			xVel -= MOVE_SPEED;
			energy -= MOVE_ENERGY_COST;
			isFacingLeft = true;
			isMoving = true;
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && energy >= MOVE_ENERGY_COST) {
			xVel += MOVE_SPEED;
			energy -= MOVE_ENERGY_COST;
			isFacingLeft = false;
			isMoving = true;
		}

		transform().setVelocityX(xVel);

		if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)
				&& getVelocity().y() == 0
				&& energy >= JUMP_ENERGY_COST) {
			transform().setVelocityY(JUMP_SPEED);
			energy -= JUMP_ENERGY_COST;

			if (onJumpCallback != null) {
				onJumpCallback.run();
			}
		}

		// Update energy if idle
		if (!isMoving && getVelocity().y() == 0) {
			energy = Math.min(MAX_ENERGY, energy + 1);
		}

		// Update animation
		if (getVelocity().y() != 0) {
			renderer().setRenderable(jumpAnimation);
		} else if (xVel != 0) {
			renderer().setRenderable(runAnimation);
		} else {
			renderer().setRenderable(idleAnimation);
		}

		// Flip direction
		renderer().setIsFlippedHorizontally(isFacingLeft);
	}

	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);

		if (other.getTag().equals("ground")) {
		this.transform().setVelocityY(0);
	}

    if (other.getTag().equals("fruit")) {
		Fruit fruit = (Fruit) other;
		energy += fruit.getEnergyGain();
		if (energy >= MAX_ENERGY) {
			energy = MAX_ENERGY;
		}
		((Fruit) other).consume();
	}
}


/**
	 * Restore avatar energy (e.g. from idle state or pickup)
	 * @param amount amount of energy to restore
	 */
	public void restoreEnergy(float amount) {
		energy = Math.min(MAX_ENERGY, energy + amount);
	}

	/**
	 * Add energy to the energy of the avatar.
	 * @param amount amount of energy to add.
	 */
	public void addEnergy(float amount) {
		energy += amount;
	}

	/**
	 * Get current energy level
	 * @return energy in range [0, MAX_ENERGY]
	 */
	public float getEnergy() {
		return energy;
	}


	public void setOnJumpCallback(Runnable callback) {
		this.onJumpCallback = callback;
	}
}
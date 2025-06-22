package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.util.Vector2;
import pepse.world.trees.Fruit;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The player's avatar. Handles movement, jumping, gravity, energy system,
 * and animation transitions based on input.
 */
public class Avatar extends GameObject {
	private static final float AVATAR_SIZE = 50f;
	private static final float MOVE_SPEED = 400f;
	private static final float JUMP_SPEED = -500f;
	private static final float GRAVITY = 500f;
	private static final float MAX_ENERGY = 100f;
	private static final float JUMP_ENERGY_COST = 10f;
	private static final float MOVE_ENERGY_COST = 0.5f;
	private static final float IDLE_ENERGY_RECOVERY = 1f;
	private static final float IDLE_FRAME_DURATION = 0.2f;
	private static final float JUMP_FRAME_DURATION = 0.2f;
	private static final float RUN_FRAME_DURATION = 0.1f;
	private static final String[] IDLE_SPRITES = {
			"assets/idle_0.png", "assets/idle_1.png",
			"assets/idle_2.png", "assets/idle_3.png"
	};
	private static final String[] JUMP_SPRITES = {
			"assets/jump_0.png", "assets/jump_1.png",
			"assets/jump_2.png", "assets/jump_3.png"
	};
	private static final String[] RUN_SPRITES = {
			"assets/run_0.png", "assets/run_1.png", "assets/run_2.png",
			"assets/run_3.png", "assets/run_4.png", "assets/run_5.png"
	};

	private float energy = MAX_ENERGY;
	private final UserInputListener inputListener;
	private final AnimationRenderable idleAnimation;
	private final AnimationRenderable jumpAnimation;
	private final AnimationRenderable runAnimation;
	private boolean isFacingLeft = false;
	private final List<JumpObserver> observers = new ArrayList<>();

	/**
	 * Constructs the avatar object.
	 *
	 * @param bottomLeftCorner Initial position of the avatar.
	 * @param inputListener    Handles user keyboard input.
	 * @param imageReader      Loads avatar sprite images.
	 */
	public Avatar(Vector2 bottomLeftCorner,
				  UserInputListener inputListener,
				  ImageReader imageReader) {
		super(new Vector2(bottomLeftCorner.x(), bottomLeftCorner.y() - AVATAR_SIZE),
				Vector2.ONES.mult(AVATAR_SIZE),
				imageReader.readImage(IDLE_SPRITES[0], true));
		this.inputListener = inputListener;
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		transform().setAccelerationY(GRAVITY);
		idleAnimation = new AnimationRenderable(loadFrames(IDLE_SPRITES, imageReader), IDLE_FRAME_DURATION);
		jumpAnimation = new AnimationRenderable(loadFrames(JUMP_SPRITES, imageReader), JUMP_FRAME_DURATION);
		runAnimation = new AnimationRenderable(loadFrames(RUN_SPRITES, imageReader), RUN_FRAME_DURATION);
	}

	/**
	 * Loads images from given paths into an array of ImageRenderable.
	 *
	 * @param paths        Paths to sprite images.
	 * @param imageReader  Reader used to load images.
	 * @return Array of loaded renderables.
	 */
	private ImageRenderable[] loadFrames(String[] paths, ImageReader imageReader) {
		return Arrays.stream(paths)
				.map(path -> imageReader.readImage(path, true))
				.toArray(ImageRenderable[]::new);
	}

	/**
	 * Updates avatar movement, energy, and animation based on input and physics.
	 *
	 * @param deltaTime Time since last update.
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		float xVel = 0;
		boolean isMoving = false;

		// Handle horizontal movement and energy consumption
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

		// Handle jump if on ground and has enough energy
		if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
				getVelocity().y() == 0 && energy >= JUMP_ENERGY_COST) {
			transform().setVelocityY(JUMP_SPEED);
			energy -= JUMP_ENERGY_COST;
			for (JumpObserver observer : observers) {
				observer.onJump();
			}
		}

		// Recover energy when idle
		if (!isMoving && getVelocity().y() == 0) {
			energy = Math.min(MAX_ENERGY, energy + IDLE_ENERGY_RECOVERY);
		}

		// Update animation state
		if (getVelocity().y() != 0) {
			renderer().setRenderable(jumpAnimation);
		} else if (xVel != 0) {
			renderer().setRenderable(runAnimation);
		} else {
			renderer().setRenderable(idleAnimation);
		}
		renderer().setIsFlippedHorizontally(isFacingLeft);
	}

	/**
	 * Handles collision logic: stops falling when touching ground,
	 * or increases energy when collecting fruit.
	 *
	 * @param other     The other GameObject involved in the collision.
	 * @param collision Collision info.
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (other.getTag().equals("ground")) {
			transform().setVelocityY(0);
		}
		if (other.getTag().equals("fruit")) {
			Fruit fruit = (Fruit) other;
			energy += fruit.getEnergyGain();
			if (energy > MAX_ENERGY) {
				energy = MAX_ENERGY;
			}
			fruit.consume();
		}
	}

	/**
	 * Adds energy to the avatar.
	 *
	 * @param amount Energy to add.
	 */
	public void addEnergy(float amount) {
		energy += amount;
	}

	/**
	 * Returns the current energy level of the avatar.
	 *
	 * @return Current energy between 0 and MAX_ENERGY.
	 */
	public float getEnergy() {
		return energy;
	}

	/**
	 * Registers an observer to be notified when the avatar jumps.
	 *
	 * @param observer Observer to add.
	 */
	public void addJumpObserver(JumpObserver observer) {
		observers.add(observer);
	}
}
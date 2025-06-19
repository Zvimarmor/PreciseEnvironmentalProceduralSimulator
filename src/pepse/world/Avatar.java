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
import java.util.List;

/**
 * The player's avatar. Handles movement, jumping, gravity, energy system,
 * and animation transitions based on input.
 */
public class Avatar extends GameObject {
	private static final float AVATAR_SIZE = 50;

	private static final float MOVE_SPEED = 400;
	private static final float JUMP_SPEED = -500;
	private static final float GRAVITY = 500;

	private static final float MAX_ENERGY = 100;
	private static final float JUMP_ENERGY_COST = 10;
	private static final float MOVE_ENERGY_COST = 0.5f;

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
				imageReader.readImage("assets/idle_0.png", true));

		this.inputListener = inputListener;
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		transform().setAccelerationY(GRAVITY);

		idleAnimation = new AnimationRenderable(new ImageRenderable[] {
				imageReader.readImage("assets/idle_0.png", true),
				imageReader.readImage("assets/idle_1.png", true),
				imageReader.readImage("assets/idle_2.png", true),
				imageReader.readImage("assets/idle_3.png", true)
		}, 0.2f);

		jumpAnimation = new AnimationRenderable(new ImageRenderable[] {
				imageReader.readImage("assets/jump_0.png", true),
				imageReader.readImage("assets/jump_1.png", true),
				imageReader.readImage("assets/jump_2.png", true),
				imageReader.readImage("assets/jump_3.png", true)
		}, 0.2f);

		runAnimation = new AnimationRenderable(new ImageRenderable[] {
				imageReader.readImage("assets/run_0.png", true),
				imageReader.readImage("assets/run_1.png", true),
				imageReader.readImage("assets/run_2.png", true),
				imageReader.readImage("assets/run_3.png", true),
				imageReader.readImage("assets/run_4.png", true),
				imageReader.readImage("assets/run_5.png", true)
		}, 0.1f);
	}

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
				getVelocity().y() == 0 &&
				energy >= JUMP_ENERGY_COST) {
			transform().setVelocityY(JUMP_SPEED);
			energy -= JUMP_ENERGY_COST;
			for (JumpObserver observer : observers) {
				observer.onJump();
			}
		}

		// Recover energy when idle
		if (!isMoving && getVelocity().y() == 0) {
			energy = Math.min(MAX_ENERGY, energy + 1);
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

	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);

		// Cancel vertical velocity when touching ground
		if (other.getTag().equals("ground")) {
			this.transform().setVelocityY(0);
		}

		// Handle fruit pickup for energy gain
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
	 * Restores energy by a specific amount (e.g. from resting or powerup).
	 *
	 * @param amount The amount of energy to restore.
	 */
	public void restoreEnergy(float amount) {
		energy = Math.min(MAX_ENERGY, energy + amount);
	}

	/**
	 * Adds energy without capping at MAX_ENERGY.
	 *
	 * @param amount The amount of energy to add.
	 */
	public void addEnergy(float amount) {
		energy += amount;
	}

	/**
	 * Gets the current energy level of the avatar.
	 *
	 * @return Current energy value between 0 and MAX_ENERGY.
	 */
	public float getEnergy() {
		return energy;
	}

	/**
	 * Registers a JumpObserver to be notified when the avatar jumps.
	 *
	 * @param observer The observer to add.
	 */
	public void addJumpObserver(JumpObserver observer) {
		observers.add(observer);
	}
}

package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;

import world.*;
import world.daynight.Night;

import java.util.List;
import java.util.Random;

/**
 * Entry point for the Pepse game.
 * Initializes the background, terrain, sun, and day-night elements of the world.
 */
public class PepseGameManager extends GameManager {

	/** The seed used for deterministic terrain generation */
	private static int terrainSeed;

	/** Duration (in seconds) of a full day-night cycle */
	private static final float CYCLE_LENGTH = 30f;

	/**
	 * Initializes the Pepse game. Sets up sky, terrain, sun, halo, and night overlay.
	 *
	 * @param imageReader     Utility to read image files.
	 * @param soundReader     Utility to read sound files.
	 * @param inputListener   Utility to handle user input.
	 * @param windowController Manages the game window and its properties.
	 */
	@Override
	public void initializeGame(
			ImageReader imageReader,
			SoundReader soundReader,
			UserInputListener inputListener,
			WindowController windowController) {

		// Generate a random seed for terrain generation
		terrainSeed = new Random().nextInt();

		// Initialize the game engine
		super.initializeGame(imageReader, soundReader, inputListener, windowController);

		Vector2 windowDimensions = windowController.getWindowDimensions();

		// Create and add the sky
		GameObject sky = Sky.create(windowDimensions);
		gameObjects().addGameObject(sky, Layer.BACKGROUND);

		// Create and add the terrain
		Terrain terrain = new Terrain(windowDimensions, terrainSeed);
		List<Block> groundBlocks = terrain.createInRange(0, (int) windowDimensions.x());
		for (Block block : groundBlocks) {
			gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
		}

		// Create and add the day-night overlay
		GameObject night = Night.create(windowDimensions, CYCLE_LENGTH);
		gameObjects().addGameObject(night, Layer.FOREGROUND);

		// Create and add the sun
		GameObject sun = Sun.create(windowDimensions, CYCLE_LENGTH);
		gameObjects().addGameObject(sun, Layer.BACKGROUND);

		// Create and add the sun halo
		GameObject sunHalo = SunHalo.create(sun);
		gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
	}

	/**
	 * Launches the game.
	 *
	 * @param args Command-line arguments (unused).
	 */
	public static void main(String[] args) {
		new PepseGameManager().run();
	}
}

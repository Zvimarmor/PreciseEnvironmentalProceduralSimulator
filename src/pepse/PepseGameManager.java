package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;

import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.trees.Forest;

import java.util.List;

/**
 * Entry point for the Pepse game.
 * Initializes the background, terrain, sun, and day-night elements of the pepse.world.
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
//		terrainSeed = new Random().nextInt();
		terrainSeed = 42;

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

		// Create Avatar instance
		float groundHeight = terrain.groundHeightAt(windowDimensions.x() / 2f);
		Vector2 avatarInitialLocation = new Vector2(windowDimensions.x() / 2f, groundHeight);
		Avatar avatar = new Avatar(avatarInitialLocation, inputListener, imageReader);
		gameObjects().addGameObject(avatar, Layer.DEFAULT);

		// Create Energy Panel
		pepse.ui.EnergyPanel energyPanel = new pepse.ui.EnergyPanel(gameObjects(), avatar::getEnergy);
		gameObjects().addGameObject(energyPanel, Layer.UI);

		// Create Forest of trees
		Forest.createForest(0, (int) windowDimensions.x(),
				terrain, gameObjects());


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

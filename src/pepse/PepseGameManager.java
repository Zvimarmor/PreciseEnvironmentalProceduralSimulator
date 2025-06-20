package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;

import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.trees.Flora;
import pepse.world.weather.CloudsManager;

import java.util.List;
import java.util.Random;

/**
 * Entry point for the Pepse game.
 * Initializes the background, terrain, sun, sky effects, avatar, UI, and weather.
 */
public class PepseGameManager extends GameManager {

	/** The seed used for deterministic terrain generation */
	private static int terrainSeed;

	/** Duration (in seconds) of a full day-night cycle */
	private static final float CYCLE_LENGTH = 30f;

	/**
	 * Initializes all major game components.
	 *
	 * @param imageReader      Utility for reading images from assets.
	 * @param soundReader      Utility for reading sounds (not used in this example).
	 * @param inputListener    Captures user input for avatar movement.
	 * @param windowController Manages the game window.
	 */
	@Override
	public void initializeGame(
			ImageReader imageReader,
			SoundReader soundReader,
			UserInputListener inputListener,
			WindowController windowController) {

		// Generate a new seed for noise-based terrain generation
		terrainSeed = new Random().nextInt();

		// Standard engine init
		super.initializeGame(imageReader, soundReader, inputListener, windowController);

		Vector2 windowDimensions = windowController.getWindowDimensions();

		// ---------- Sky ----------
		GameObject sky = Sky.create(windowDimensions);
		sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(sky, Layer.BACKGROUND);

		// ---------- Terrain ----------
		Terrain terrain = new Terrain(windowDimensions, terrainSeed);
		List<Block> groundBlocks = terrain.createInRange(0, (int) windowDimensions.x());
		for (Block block : groundBlocks) {
			gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
		}

		// ---------- Night overlay ----------
		GameObject night = Night.create(windowDimensions, CYCLE_LENGTH);
		night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(night, Layer.FOREGROUND);

		// ---------- Sun ----------
		GameObject sun = Sun.create(windowDimensions, CYCLE_LENGTH);
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(sun, Layer.BACKGROUND);

		// ---------- Sun halo ----------
		GameObject sunHalo = SunHalo.create(sun);
		sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);

		// ---------- Avatar ----------
		float groundHeight = terrain.groundHeightAt(windowDimensions.x() / 2f);
		Vector2 avatarInitialLocation = new Vector2(windowDimensions.x() / 2f, groundHeight);
		Avatar avatar = new Avatar(avatarInitialLocation, inputListener, imageReader);
		gameObjects().addGameObject(avatar, Layer.DEFAULT);

		// ---------- Energy Panel UI ----------
		EnergyPanel energyPanel = new EnergyPanel(gameObjects(), avatar::getEnergy);
		gameObjects().addGameObject(energyPanel, Layer.UI);

		// ---------- Flora ----------
		Flora flora = new Flora(terrain, gameObjects(), Layer.STATIC_OBJECTS);
		flora.createInRange(0, (int) windowDimensions.x());

		// ---------- Clouds + Rain ----------
		CloudsManager cloudsManager = new CloudsManager(
				gameObjects(),
				windowDimensions,
				Layer.BACKGROUND
		);
		cloudsManager.startSpawningClouds();
		avatar.addJumpObserver(cloudsManager); // attach cloud rain to jump event
	}

	/**
	 * Launches the game via the game manager.
	 *
	 * @param args Command-line arguments (not used).
	 */
	public static void main(String[] args) {
		new PepseGameManager().run();
	}
}

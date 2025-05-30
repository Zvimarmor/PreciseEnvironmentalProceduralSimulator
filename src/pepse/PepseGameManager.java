package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;

import world.Block;
import world.Sky;
import world.Terrain;

import java.util.List;

public class PepseGameManager extends GameManager {

	@Override
	public void initializeGame(
			ImageReader imageReader,
			SoundReader soundReader,
			UserInputListener inputListener,
			WindowController windowController) {
		super.initializeGame(imageReader, soundReader, inputListener, windowController);
		GameObject sky = Sky.create(windowController.getWindowDimensions());
		gameObjects().addGameObject(sky, Layer.BACKGROUND);
		List<Block> ground = new Terrain(windowController.getWindowDimensions(),0).
				createInRange(0,1000);
		for (Block block : ground) {
			gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
		}
	}

	public static void main(String[] args) {
		new PepseGameManager().run();
	}
}

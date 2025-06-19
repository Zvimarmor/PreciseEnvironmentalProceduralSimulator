package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Supplier;

/**
 * A UI panel displaying the current energy level of the avatar in percentage.
 * Updates its display every frame.
 */
public class EnergyPanel extends GameObject {
	private static final Vector2 PANEL_TOP_LEFT = new Vector2(15, 15);
	private static final Vector2 PANEL_SIZE = new Vector2(100, 30);
	private static final Color ENERGY_COLOR = Color.BLACK;

	private final TextRenderable energyTextRenderable;
	private final GameObject textObject;
	private final Supplier<Float> energySupplier;

	/**
	 * Constructs the energy panel UI.
	 *
	 * @param gameObjects    The game's object collection to which this panel will be added.
	 * @param energySupplier A function that supplies the current energy level.
	 */
	public EnergyPanel(GameObjectCollection gameObjects, Supplier<Float> energySupplier) {
		super(PANEL_TOP_LEFT, PANEL_SIZE, null);
		this.energySupplier = energySupplier;

		energyTextRenderable = new TextRenderable("Energy: 100%");
		energyTextRenderable.setColor(ENERGY_COLOR);

		textObject = new GameObject(PANEL_TOP_LEFT, PANEL_SIZE, energyTextRenderable);

		gameObjects.addGameObject(this, Layer.UI);
		gameObjects.addGameObject(textObject, Layer.UI);
	}

	/**
	 * Updates the displayed energy value each frame.
	 *
	 * @param deltaTime Time passed since last frame (unused).
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		float currentEnergy = energySupplier.get();
		energyTextRenderable.setString("Energy: " + (int) currentEnergy + "%");
	}
}

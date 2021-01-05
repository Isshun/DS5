package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.manager.input.InputManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.module.CharacterClientModule;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class CursorLayer extends BaseLayer {

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private CharacterClientModule characterClientModule;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private GameManager gameManager;

    @Inject
    private InputManager inputManager;

    @Inject
    private GameActionManager gameActionManager;

    @Inject
    private WorldModule worldModule;

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (gameActionManager.getMode() != GameActionMode.NONE) {
            renderer.drawTextUI(inputManager.getMouseX() + 2, inputManager.getMouseY() + 2, 22, Color.BLACK, gameActionManager.getActionLabel());
            renderer.drawTextUI(inputManager.getMouseX() + 1, inputManager.getMouseY() + 1, 22, Color.BLACK, gameActionManager.getActionLabel());
            renderer.drawTextUI(inputManager.getMouseX(), inputManager.getMouseY(), 22, gameActionManager.getActionColor(), gameActionManager.getActionLabel());
        }

        if (gameActionManager.getMode() == GameActionMode.BUILD) {
            ParcelModel parcel = worldModule.getParcel(
                    viewport.getWorldPosX(inputManager.getMouseX()),
                    viewport.getWorldPosY(inputManager.getMouseY()),
                    viewport.getFloor()
            );
            if (parcel != null) {
                renderer.drawRectangleOnMap(parcel.x, parcel.y, Constant.TILE_WIDTH, Constant.TILE_HEIGHT, Color.BLUE, true, 0, 0);
            }
        }
    }

}
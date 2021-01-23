package org.smallbox.faraway.client.render.layer.ui;

import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.manager.input.InputManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class CursorLayer extends BaseLayer {
    @Inject private SpriteManager spriteManager;
    @Inject private CharacterModule characterModule;
    @Inject private GameManager gameManager;
    @Inject private InputManager inputManager;
    @Inject private GameActionManager gameActionManager;
    @Inject private WorldModule worldModule;

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (gameActionManager.getMode() != GameActionMode.NONE) {
            renderer.drawRectangleOnMap(
                    viewport.getWorldPosX(inputManager.getMouseX()),
                    viewport.getWorldPosY(inputManager.getMouseY()),
                    Constant.TILE_SIZE - 8, Constant.TILE_SIZE - 8, gameActionManager.getActionColor(), false, 4, 4);
            renderer.drawTextUI(inputManager.getMouseX() + 10, inputManager.getMouseY() - 10, 22, gameActionManager.getActionColor(), gameActionManager.getActionLabel(), "sui", 2);
        }
    }

}
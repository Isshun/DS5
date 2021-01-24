package org.smallbox.faraway.client.render.layer.ui;

import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.manager.input.InputManager;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.util.Constant;

/**
 * Draw on parcel right under the cursor
 */
@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class CursorProjectionLayer extends BaseMapLayer {
    @Inject private GameActionManager gameActionManager;
    @Inject private InputManager inputManager;

    @Override
    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        if (gameActionManager.getMode() != GameActionMode.NONE) {
            renderer.drawCadreOnMap(
                    viewport.getWorldPosX(inputManager.getMouseX()),
                    viewport.getWorldPosY(inputManager.getMouseY()),
                    Constant.TILE_SIZE - 8, Constant.TILE_SIZE - 8, gameActionManager.getActionColor(), 4, 4, 4);
        }
    }

}
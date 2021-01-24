package org.smallbox.faraway.client.render.layer.ui;

import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.manager.input.InputManager;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

/**
 * Draw aside the cursor
 */
@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class CursorLayer extends BaseLayer {
    @Inject private GameActionManager gameActionManager;
    @Inject private InputManager inputManager;

    @Override
    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        if (gameActionManager.getMode() != GameActionMode.NONE) {
            renderer.drawText(inputManager.getMouseX() + 10, inputManager.getMouseY() - 10, gameActionManager.getActionLabel(), gameActionManager.getActionColor(), 22, false, "sui", 2);
        }
    }

}
package org.smallbox.faraway.client.render.layer.ui;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.GameEventManager;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.util.Constant;

/**
 * Draw selection on map, according to the status of GameEventManager and GameActionManager
 */
@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class SelectionProjectionLayer extends BaseMapLayer {
    @Inject private GameEventManager gameEventManager;
    @Inject private GameActionManager gameActionManager;
    @Inject private Viewport viewport;

    @Override
    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        if (gameEventManager.isMousePressed()) {
            drawSelection(renderer,
                    gameEventManager.getMouseDownX(), gameEventManager.getMouseDownY(),
                    gameEventManager.getMouseOffsetX(), gameEventManager.getMouseOffsetY(),
                    gameActionManager.hasAction() ? gameActionManager.getActionColor() : Colors.COLOR_SELECTION);
        }
    }

    private void drawSelection(GDXRendererBase renderer, int fromX, int fromY, int width, int height, Color color) {
        int fromMapX = Math.min(viewport.getWorldPosX(fromX), viewport.getWorldPosX(fromX + width));
        int fromMapY = Math.min(viewport.getWorldPosY(fromY), viewport.getWorldPosY(fromY + height));
        int toMapX = Math.max(viewport.getWorldPosX(fromX), viewport.getWorldPosX(fromX + width));
        int toMapY = Math.max(viewport.getWorldPosY(fromY), viewport.getWorldPosY(fromY + height));

        for (int x = fromMapX; x <= toMapX; x++) {
            for (int y = fromMapY; y <= toMapY; y++) {
                renderer.drawCadreOnMap(x, y, Constant.TILE_SIZE - 8, Constant.TILE_SIZE - 8, color, 4, 4, 4);
            }
        }
    }

}

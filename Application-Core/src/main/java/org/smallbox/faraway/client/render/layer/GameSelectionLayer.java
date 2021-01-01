package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.GameEventManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;

@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class GameSelectionLayer extends BaseLayer {

    @Inject
    private GameEventManager gameEventManager;

    @Inject
    private GameActionManager gameActionManager;

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (gameEventManager.isMousePressed()) {
            int fromX = gameEventManager.getMouseDownX();
            int fromY = gameEventManager.getMouseDownY();
            int width = gameEventManager.getMouseX() - gameEventManager.getMouseDownX();
            int height = gameEventManager.getMouseY() - gameEventManager.getMouseDownY();
            drawSelection(renderer, fromX, fromY, width, height, gameActionManager.hasAction() ? gameActionManager.getActionColor() : Color.WHITE);
        }
    }

    private void drawSelection(GDXRenderer renderer, int fromX, int fromY, int width, int height, Color color) {
        for (int i = 0; i < 4; i++) {
            renderer.drawRectangle(fromX + i, fromY + i, width, height, color, false);
        }
    }

}

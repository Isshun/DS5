package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.GameActionManager;
import org.smallbox.faraway.client.GameEventManager;
import org.smallbox.faraway.client.module.CursorSelection.CursorSelectionModule;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.Game;

@GameObject
@GameLayer(level = LayerManager.TOP, visible = true)
public class SelectorLayer extends BaseLayer {

    private static final Color COLOR_ROCK = ColorUtils.fromHex(0xff442dff);

    @Inject
    private CursorSelectionModule cursorSelectionModule;

    @Inject
    private GameEventManager gameEventManager;

    @Inject
    private GameActionManager gameActionManager;

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (gameActionManager.getMode() != GameActionManager.Mode.NONE && gameEventManager.isMousePressed()) {
            int fromX = gameEventManager.getMouseDownX();
            int fromY = gameEventManager.getMouseDownY();
            int width = gameEventManager.getMouseX() - gameEventManager.getMouseDownX();
            int height = gameEventManager.getMouseY() - gameEventManager.getMouseDownY();
            for (int i = 0; i < 4; i++) {
                renderer.drawRectangle(fromX + i, fromY + i, width, height, gameActionManager.getActionColor(), false);
            }
        }
    }

}

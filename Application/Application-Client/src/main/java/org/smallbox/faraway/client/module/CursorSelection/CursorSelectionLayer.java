package org.smallbox.faraway.client.module.CursorSelection;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.GameEventManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 13/06/2015.
 */
@GameLayer(level = LayerManager.TOP, visible = true)
public class CursorSelectionLayer extends BaseLayer {

    private static final Color COLOR_ROCK = ColorUtils.fromHex(0xff442dff);

    @BindModule
    private CursorSelectionModule cursorSelectionModule;

    @BindModule
    private GameEventManager gameEventManager;

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
//        if (gameEventManager.isMousePressed()) {
//            renderer.drawRectangle(
//                    gameEventManager.getMouseDownX(),
//                    gameEventManager.getMouseDownY(),
//                    (gameEventManager.getMouseX() - gameEventManager.getMouseDownX()),
//                    (gameEventManager.getMouseY() - gameEventManager.getMouseDownY()),
//                    COLOR_ROCK,
//                    false);
//        }
    }

}

package org.smallbox.faraway.client.gameContextMenu;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.manager.input.InputManager;
import org.smallbox.faraway.client.render.BaseRendererManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

import java.util.Optional;

@GameObject
@GameLayer(level = LayerManager.AREA_LAYER_LEVEL, visible = true)
public class GameContextMenuLayer extends BaseLayer {
    @Inject private GameContextMenuManager gameContextMenuManager;
    @Inject private InputManager inputManager;

    @Override
    public void onDraw(BaseRendererManager renderer, Viewport viewport, double animProgress, int frame) {
        Optional.ofNullable(gameContextMenuManager.getMenu()).ifPresent(menu -> menu.getEntries().forEach(entry -> displayEntry(renderer, entry)));
    }

    private void displayEntry(BaseRendererManager renderer, GameContextMenuEntry entry) {
        Color color = entry.contains(inputManager.getMouseX(), inputManager.getMouseY()) ? Colors.BLUE_LIGHT_2 : Color.WHITE;
        renderer.drawText(entry.getX() + 6, entry.getY() + 6, entry.getLabel(), Color.BLACK, 22);
        renderer.drawText(entry.getX() + 5, entry.getY() + 5, entry.getLabel(), color, 22);
    }

}

package org.smallbox.faraway.client.debug;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModule;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL + 1)
public class DebugItemRenderer extends BaseRenderer {

    @BindModule
    private ItemModule itemModule;

    @BindModule
    private JobModule jobModule;

    @Override
    public void onGameCreate(Game game) {
        setVisibility(true);
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        itemModule.getItems()
                .forEach(item -> {
                    for (int i = 0; i < item.getWidth(); i++) {
                        for (int j = 0; j < item.getHeight(); j++) {
                            renderer.drawOnMap(item.getParcel().x + i, item.getParcel().y + j, Color.CYAN);
                        }
                    }

                    renderer.drawOnMap(item.getParcel().x, item.getParcel().y, "[" + item.getId() + "] " + item.getLabel(), 14, Color.BLACK, 1, 1);
                    renderer.drawOnMap(item.getParcel().x, item.getParcel().y, "[" + item.getId() + "] " + item.getLabel(), 14, Color.WHITE);

                    if (item.getFactory() != null) {
                        renderer.drawOnMap(item.getParcel().x, item.getParcel().y, item.getFactory().getMessage(), 14, Color.BLACK, 1, 17);
                        renderer.drawOnMap(item.getParcel().x, item.getParcel().y, item.getFactory().getMessage(), 14, Color.WHITE, 0, 16);
                    }
                });
    }

    @Override
    public void onKeyEvent(GameEventListener.Action action, GameEventListener.Key key, GameEventListener.Modifier modifier) {
        if (action == GameEventListener.Action.RELEASED && key == GameEventListener.Key.F9) {
            toggleVisibility();
        }
    }

}

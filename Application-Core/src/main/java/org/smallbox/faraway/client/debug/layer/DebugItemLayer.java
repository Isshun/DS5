package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModule;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugItemLayer extends BaseLayer {

    @Inject
    private ItemModule itemModule;

    @Inject
    private JobModule jobModule;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        itemModule.getAll()
                .stream()
                .filter(item -> item.getParcel().z == viewport.getFloor())
                .forEach(item -> {
                    for (int i = 0; i < item.getWidth(); i++) {
                        for (int j = 0; j < item.getHeight(); j++) {
                            renderer.drawOnMap(item.getParcel().x + i, item.getParcel().y + j, Color.CYAN);
                        }
                    }

                    renderer.drawTextOnMap(item.getParcel().x, item.getParcel().y, "[" + item.getId() + "] " + item.getLabel(), 14, Color.BLACK, 1, 1);
                    renderer.drawTextOnMap(item.getParcel().x, item.getParcel().y, "[" + item.getId() + "] " + item.getLabel(), 14, Color.WHITE);

                    if (item.getFactory() != null) {
                        renderer.drawTextOnMap(item.getParcel().x, item.getParcel().y, item.getFactory().getMessage(), 14, Color.BLACK, 1, 17);
                        renderer.drawTextOnMap(item.getParcel().x, item.getParcel().y, item.getFactory().getMessage(), 14, Color.WHITE, 0, 16);
                    }
                });
    }

    @SuppressWarnings("unused")
    @GameShortcut(key = Input.Keys.F8)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}

package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.job.JobModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugItemLayer extends BaseMapLayer {
    @Inject private ItemModule itemModule;
    @Inject private JobModule jobModule;

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        itemModule.getAll()
                .stream()
                .filter(item -> item.getParcel().z == viewport.getFloor())
                .forEach(item -> {
                    for (int i = 0; i < item.getWidth(); i++) {
                        for (int j = 0; j < item.getHeight(); j++) {
                            renderer.drawRectangleOnMap(item.getParcel(), TILE_SIZE, TILE_SIZE, Color.CYAN, i, i);
                        }
                    }

                    renderer.drawTextOnMap(item.getParcel(), "[" + item.getId() + "] " + item.getLabel(), Color.BLACK, 14, 1, 1);
                    renderer.drawTextOnMap(item.getParcel(), "[" + item.getId() + "] " + item.getLabel(), Color.WHITE, 14, 0, 0);

                    if (item.getFactory() != null) {
                        renderer.drawTextOnMap(item.getParcel(), item.getFactory().getMessage(), Color.BLACK, 14, 1, 17);
                        renderer.drawTextOnMap(item.getParcel(), item.getFactory().getMessage(), Color.WHITE, 14, 0, 16);
                    }
                });
    }

    @SuppressWarnings("unused")
    @GameShortcut(key = Input.Keys.F8)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}

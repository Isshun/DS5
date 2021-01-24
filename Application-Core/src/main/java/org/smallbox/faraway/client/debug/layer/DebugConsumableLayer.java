package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugConsumableLayer extends BaseMapLayer {
    @Inject private ConsumableModule consumableModule;
    @Inject private JobModule jobModule;

    public void    onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        consumableModule.getAll()
                .forEach(consumable -> {
                    renderer.drawRectangleOnMap(consumable.getParcel(), TILE_SIZE, TILE_SIZE, consumableModule.hasLock(consumable) ? Color.CORAL : Color.CYAN, 0, 0);

                    renderer.drawTextOnMap(consumable.getParcel(), consumable.getLabel(), Color.BLACK, 14, 1, 1);
                    renderer.drawTextOnMap(consumable.getParcel(), consumable.getLabel(), Color.WHITE, 14, 0, 0);

                    renderer.drawTextOnMap(consumable.getParcel(), "x" + consumable.getFreeQuantity(), Color.BLACK, 14, 1, 17);
                    renderer.drawTextOnMap(consumable.getParcel(), "x" + consumable.getFreeQuantity(), Color.WHITE, 14, 0, 16);
                });
    }

    @GameShortcut(key = Input.Keys.F10)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}

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

@GameObject
@GameLayer(level = LayerManager.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugConsumableLayer extends BaseMapLayer {
    @Inject private ConsumableModule consumableModule;
    @Inject private JobModule jobModule;

    public void    onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        consumableModule.getAll()
                .forEach(consumable -> {
                    renderer.drawPixelOnMap(consumable.getParcel().x, consumable.getParcel().y, consumableModule.hasLock(consumable) ? Color.CORAL : Color.CYAN);

                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, consumable.getLabel(), 14, Color.BLACK, 1, 1);
                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, consumable.getLabel(), 14, Color.WHITE);

                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getFreeQuantity(), 14, Color.BLACK, 1, 17);
                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getFreeQuantity(), 14, Color.WHITE, 0, 16);
                });
    }

    @GameShortcut(key = Input.Keys.F10)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}

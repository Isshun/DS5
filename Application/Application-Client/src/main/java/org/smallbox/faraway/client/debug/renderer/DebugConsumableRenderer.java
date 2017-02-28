package org.smallbox.faraway.client.debug.renderer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModule;

/**
 * Created by Alex on 31/07/2016.
 */
@GameRenderer(level = MainRenderer.CONSUMABLE_RENDERER_LEVEL + 1, visible = false)
public class DebugConsumableRenderer extends BaseRenderer {

    @BindModule
    private ConsumableModule consumableModule;

    @BindModule
    private JobModule jobModule;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        consumableModule.getConsumables()
                .forEach(consumable -> {
                    renderer.drawOnMap(consumable.getParcel().x, consumable.getParcel().y, consumableModule.hasLock(consumable) ? Color.CORAL : Color.CYAN);

                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, consumable.getLabel(), 14, Color.BLACK, 1, 1);
                    renderer.drawOnMap(consumable.getParcel().x, consumable.getParcel().y, consumable.getLabel(), 14, Color.WHITE);

                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getQuantity(), 14, Color.BLACK, 1, 17);
                    renderer.drawTextOnMap(consumable.getParcel().x, consumable.getParcel().y, "x" + consumable.getQuantity(), 14, Color.WHITE, 0, 16);
                });
    }

    @GameShortcut(key = GameEventListener.Key.F10)
    public void onToggleVisibility() {
        toggleVisibility();
    }

}

package org.smallbox.faraway.module.consumable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.MainRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.module.world.model.ConsumableModel;

/**
 * Created by Alex on 31/07/2016.
 */
public class ConsumableRenderer extends BaseRenderer {

    @BindModule
    private ConsumableModule        _consumableModule;

    private int                     _frame;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        _consumableModule.getConsumables().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(consumable -> renderer.drawOnMap(consumable.getParcel(), getItemSprite(consumable)));
    }

    private Sprite getItemSprite(ConsumableModel consumable) {
        return ApplicationClient.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    public int getLevel() {
        return MainRenderer.CHARACTER_RENDERER_LEVEL;
    }
}

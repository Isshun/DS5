package org.smallbox.faraway.module.consumable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.MainRenderer;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;

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
        return Application.spriteManager.getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    public int getLevel() {
        return MainRenderer.CHARACTER_RENDERER_LEVEL;
    }
}

package org.smallbox.faraway.module.consumable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.engine.renderer.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;

/**
 * Created by Alex on 31/07/2016.
 */
public class ConsumableRenderer extends BaseRenderer {
    private final ConsumableModule  _consumableModule;
    private int                     _frame;

    public ConsumableRenderer(ConsumableModule consumableModule) {
        _consumableModule = consumableModule;
    }

    @Override
    protected void onLoad(Game game) {
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        _consumableModule.getConsumables().stream()
                .filter(item -> parcelInViewport(item.getParcel()))
                .forEach(consumable -> renderer.drawOnMap(consumable.getParcel(), getItemSprite(consumable)));
    }

    private Sprite getItemSprite(ConsumableModel consumable) {
        return SpriteManager.getInstance().getSprite(consumable.getInfo(), consumable.getGraphic(), consumable.getInfo().height, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    public int getLevel() {
        return MainRenderer.CHARACTER_RENDERER_LEVEL;
    }
}

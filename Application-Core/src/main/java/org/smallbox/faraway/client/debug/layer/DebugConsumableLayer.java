package org.smallbox.faraway.client.debug.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.LayerLevel;
import org.smallbox.faraway.client.layer.BaseMapLayer;
import org.smallbox.faraway.client.layer.GameLayer;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobModule;

import static org.smallbox.faraway.util.Constant.TILE_SIZE;

@GameObject
@GameLayer(level = LayerLevel.CONSUMABLE_LAYER_LEVEL + 1, visible = false)
public class DebugConsumableLayer extends BaseMapLayer {
    @Inject private ConsumableModule consumableModule;
    @Inject private JobModule jobModule;

    public void    onDraw(BaseRenderer renderer, Viewport viewport, double animProgress, int frame) {
        consumableModule.getAll()
                .forEach(consumable -> {
                    renderer.drawRectangleOnMap(consumable.getParcel(), TILE_SIZE, TILE_SIZE, consumableModule.hasLock(consumable) ? Color.CORAL : Color.CYAN, 0, 0);

                    renderer.drawTextOnMap(consumable.getParcel(), consumable.getLabel(), Color.BLACK, 14, 1, 1);
                    renderer.drawTextOnMap(consumable.getParcel(), consumable.getLabel(), Color.WHITE, 14, 0, 0);

                    renderer.drawTextOnMap(consumable.getParcel(), "x" + consumable.getActualQuantity(), Color.BLACK, 14, 1, 17);
                    renderer.drawTextOnMap(consumable.getParcel(), "x" + consumable.getActualQuantity(), Color.WHITE, 14, 0, 16);
                });
    }

}

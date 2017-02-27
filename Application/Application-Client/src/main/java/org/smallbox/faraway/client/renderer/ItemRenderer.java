package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.item.factory.BasicCraftJob;

@GameRenderer(level = MainRenderer.ITEM_RENDERER_LEVEL, visible = true)
public class ItemRenderer extends BaseRenderer {

    @BindModule
    private ItemModule itemModule;

    @BindComponent
    private SpriteManager spriteManager;

    private int                 _frame;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        itemModule.getItems().stream()
//                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(item -> {
                    ParcelModel parcel = item.getParcel();
//                    item.getFactory().getRunningReceipt().getCostRemaining()
                    renderer.drawOnMap(item.getParcel(), getItemSprite(item));

                    if (item.getFactory() != null && item.getFactory().getCraftJob() != null) {
                        long tick = Application.gameManager.getGame().getTick();
                        long startTick = ((BasicCraftJob)item.getFactory().getCraftJob()).getStartTick();
                        long endTick = ((BasicCraftJob)item.getFactory().getCraftJob()).getEndTick();

                        long tickFromStart = tick - startTick;
                        long tickTotal = endTick - startTick;
                        double progress = tickTotal > 0 ? (tickFromStart + 1) / (double)tickTotal : 0;

                        renderer.drawRectangleOnMap(parcel.x, parcel.y, (int) (100 * progress), 6, Color.BLUE, true, 0, 0);
                        renderer.drawRectangleOnMap(parcel.x, parcel.y, 100, 6, Color.CHARTREUSE, false, 0, 0);
                    }

                });
    }

    private Sprite getItemSprite(UsableItem item) {
        return spriteManager.getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? item.getInfo().height : 0, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }
}
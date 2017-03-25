package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;

@GameLayer(level = LayerManager.ITEM_LAYER_LEVEL, visible = true)
public class ItemLayer extends BaseLayer {

    @BindModule
    private ItemModule itemModule;

    @BindComponent
    private SpriteManager spriteManager;

    private int _frame;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        itemModule.getItems().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(item -> {
                    ParcelModel parcel = item.getParcel();
//                    item.getFactory().getRunningReceipt().getCostRemaining()
                    renderer.drawOnMap(item.getParcel(), getItemSprite(item));

                    if (item.getFactory() != null && item.getFactory().getCraftJob() != null) {
                        renderer.drawRectangleOnMap(parcel.x, parcel.y, (int) (32 * item.getFactory().getCraftJob().getProgress()), 6, Color.BLUE, true, 0, 0);
                        renderer.drawRectangleOnMap(parcel.x, parcel.y, 32, 6, Color.CHARTREUSE, false, 0, 0);
                    }

                    if (item.getHealth() < item.getMaxHealth()) {
                        renderer.drawTextOnMap(parcel.x, parcel.y, item.getHealth() + "/" + item.getMaxHealth(), 14, Color.CHARTREUSE, 0, 0);
                    }

                    if (!item.isBuildComplete()) {
                        renderer.drawTextOnMap(parcel.x, parcel.y, "to build", 14, Color.CHARTREUSE, 0, 0);
                    }

                    drawSelectionOnMap(renderer, spriteManager, viewport, item, item.getParcel().x, item.getParcel().y);
                });
    }

    private Sprite getItemSprite(UsableItem item) {
        return spriteManager.getSprite(item.getInfo(), item.getGraphic(), item.isBuildComplete() ? item.getInfo().height : 0, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }
}
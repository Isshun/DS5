package org.smallbox.faraway.client.render.layer.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseMapLayer;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;

@GameObject
@GameLayer(level = LayerManager.ITEM_LAYER_LEVEL, visible = true)
public class ItemLayer extends BaseMapLayer {
    @Inject private ItemModule itemModule;
    @Inject private SpriteManager spriteManager;

    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress, int frame) {
        itemModule.getAll().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(item -> {
                    Parcel parcel = item.getParcel();
                    renderer.drawSpriteOnMap(getItemSprite(item), item.getParcel());

                    if (item.getFactory() != null && item.getFactory().getCraftJob() != null) {
                        renderer.drawRectangleOnMap(parcel, (int) (32 * item.getFactory().getCraftJob().getProgress()), 6, Color.BLUE, 0, 0);
                        renderer.drawCadreOnMap(parcel, 32, 6, Color.CHARTREUSE, 4, 0, 0);
                    }

                    if (item.getHealth() < item.getMaxHealth()) {
                        renderer.drawTextOnMap(parcel, item.getHealth() + "/" + item.getMaxHealth(), Color.CHARTREUSE, 14, 0, 0);
                    }

                    if (!item.isComplete() && item.isComplete()) {
                        renderer.drawTextOnMap(parcel, "to build", Color.CHARTREUSE, 14, 0, 0);
                    }

                    if (!item.isComplete()) {
                        renderer.drawTextOnMap(parcel, "to build", Color.CHARTREUSE, 14, 0, 0);
                    }

                    drawSelectionOnMap(renderer, spriteManager, viewport, item, item.getParcel().x, item.getParcel().y, item.getWidth() * 32, item.getHeight() * 32, 0, 0);
                });

    }

    private Sprite getItemSprite(UsableItem item) {
        return spriteManager.getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? item.getInfo().height : 0, 0, 255, false);
    }

}
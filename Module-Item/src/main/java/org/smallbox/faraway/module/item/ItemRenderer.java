package org.smallbox.faraway.module.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.BindManager;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.renderer.*;
import org.smallbox.faraway.module.item.item.ItemModel;

public class ItemRenderer extends BaseRenderer {

    @BindModule
    private ItemModule itemModule;

    @BindManager
    private SpriteManager spriteManager;

    private int                 _frame;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        itemModule.getItems().stream()
                .filter(item -> viewport.hasParcel(item.getParcel()))
                .forEach(item -> renderer.drawOnMap(item.getParcel(), getItemSprite(item)));
    }

    private Sprite getItemSprite(ItemModel item) {
        return spriteManager.getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? item.getInfo().height : 0, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    public int getLevel() {
        return MainRenderer.CHARACTER_RENDERER_LEVEL;
    }
}
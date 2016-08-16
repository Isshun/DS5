package org.smallbox.faraway.module.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.renderer.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.module.item.item.ItemModel;

public class ItemRenderer extends BaseRenderer {

    @BindModule
    private ItemModule          _itemModule;

    private int                 _frame;

    @Override
    protected void onGameStart(Game game) {
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        _itemModule.getItems().stream()
                .filter(item -> parcelInViewport(item.getParcel()))
                .forEach(item -> renderer.drawOnMap(item.getParcel(), getItemSprite(item)));
    }

    private Sprite getItemSprite(ItemModel item) {
        return SpriteManager.getInstance().getSprite(item.getInfo(), item.getGraphic(), item.isComplete() ? item.getInfo().height : 0, 0, 255, false);
    }

    public void onRefresh(int frame) {
        _frame = frame;
    }

    public int getLevel() {
        return MainRenderer.CHARACTER_RENDERER_LEVEL;
    }
}
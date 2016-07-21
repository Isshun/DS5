package org.smallbox.faraway.module.item;

import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.core.engine.renderer.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.module.item.item.ItemModel;

public class ItemRenderer extends BaseRenderer {
    private final ItemModule _itemModule;
    private int                 _floor;
    private int                 _width;
    private int                 _height;
    private int                 _frame;

    public ItemRenderer(ItemModule itemModule) {
        _itemModule = itemModule;
    }

    @Override
    protected void onLoad(Game game) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
    }

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        int fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        int toX = Math.min(_width, fromX + 50);
        int toY = Math.min(_height, fromY + 40);

        int viewportX = viewport.getPosX();
        int viewportY = viewport.getPosY();

        _itemModule.getItems().stream()
                .filter(item -> item.getParcel() != null
                        && item.getParcel().z == _floor
                        && item.getParcel().x >= fromX && item.getParcel().x <= toX
                        && item.getParcel().y >= fromY && item.getParcel().y <= toY)
                .forEach(item -> renderer.draw(getItemSprite(item), (item.getParcel().x * Constant.TILE_WIDTH) + viewportX, (item.getParcel().y * Constant.TILE_HEIGHT) + viewportY));
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

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }
}
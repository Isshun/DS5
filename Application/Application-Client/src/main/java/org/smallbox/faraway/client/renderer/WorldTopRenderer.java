package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.core.GameRenderer;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.*;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.Constant;

@GameRenderer(level = MainRenderer.WORLD_TOP_RENDERER_LEVEL, visible = true)
public class WorldTopRenderer extends BaseRenderer {

    @BindModule
    private WorldModule _worldModule;

    @BindComponent
    protected SpriteManager _spriteManager;

    protected MapObjectModel    _itemSelected;
    private int                 _floor;
    private int                 _width;
    private int                 _height;

    @Override
    public void onGameStart(Game game) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
    }

    @Override
    protected void onGameUpdate() {
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        int fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        int fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        int toX = Math.min(_width, fromX + 50);
        int toY = Math.min(_height, fromY + 40);

        int viewportX = viewport.getPosX();
        int viewportY = viewport.getPosY();

        ParcelModel[][][] parcels = _worldModule.getParcels();
        for (int x = toX-1; x >= fromX; x--) {
            for (int y = toY-1; y >= fromY; y--) {
                ParcelModel parcel = parcels[x][y][_floor];
                if (parcel.hasPlant()) {
                    renderer.draw((x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY, _spriteManager.getItem(parcel.getPlant().getGraphic(), parcel.getTile(), parcel.getPlant().getTile()));
                }
                if (parcel.hasItem(StructureItem.class)) {
                    renderer.draw((x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY, _spriteManager.getItem(parcel.getItem(StructureItem.class)));
                }
                if (parcel.getNetworkObjects() != null) {
                    for (NetworkItem networkObject: parcel.getNetworkObjects()) {
                        renderer.draw((x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY, _spriteManager.getItem(networkObject));
                    }
                }
                if (parcel.hasItem(ConsumableItem.class)) {
                    renderer.draw((x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY, _spriteManager.getItem(parcel.getItem(ConsumableItem.class)));
                }
            }
        }
    }

    public void onDrawSelected(GDXRenderer renderer, Viewport viewport, double animProgress) {
        if (_itemSelected != null) {
            int offset = 0;
//        switch (frame / 10 % 5) {
//            case 1: offset = 1; break;
//            case 2: offset = 2; break;
//            case 3: offset = 3; break;
//            case 4: offset = 2; break;
//            case 5: offset = 1; break;
//        }

            int fromX = 0;
            int fromY = 0;
            int toX = 32;
            int toY = 32;
            if (_itemSelected.getGraphic() != null && _itemSelected.getGraphic().textureRect != null) {
                Rectangle rect = _itemSelected.getGraphic().textureRect;
                fromX = (int)(rect.getX()/2);
                fromY = (int)(rect.getY()/2);
                toX = (int)(rect.getX() + rect.getWidth()) - 8;
                toY = (int)(rect.getY() + rect.getHeight()) - 8;
            }

            int x = _itemSelected.getParcel().x * Constant.TILE_WIDTH + viewport.getPosX();
            int y = _itemSelected.getParcel().y * Constant.TILE_HEIGHT + viewport.getPosY();
            renderer.draw(x - offset + fromX, y - offset + fromY, _spriteManager.getSelectorCorner(0));
            renderer.draw(x + offset + toX, y - offset + fromY, _spriteManager.getSelectorCorner(1));
            renderer.draw(x - offset + fromX, y + offset + toY, _spriteManager.getSelectorCorner(2));
            renderer.draw(x + offset + toX, y + offset + toY, _spriteManager.getSelectorCorner(3));

            if (_itemSelected.getInfo().slots != null) {
                for (int[] slot: _itemSelected.getInfo().slots) {
                    renderer.draw((_itemSelected.getParcel().x + slot[0])* Constant.TILE_WIDTH + viewport.getPosX(), (_itemSelected.getParcel().y + slot[1])* Constant.TILE_HEIGHT + viewport.getPosY(), _spriteManager.getIcon("data/res/ic_slot.png")
                    );
                }
            }
        }
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

//    @Override
//    public void onSelectItem(UsableItem item) {
//        _itemSelected = item;
//    }
//
//    @Override
//    public void onSelectPlant(PlantItem resource) {
//        _itemSelected = resource;
//    }
//
//    @Override
//    public void onSelectConsumable(ConsumableItem consumable) {
//        _itemSelected = consumable;
//    }
//
//    @Override
//    public void onSelectStructure(StructureItem structure) {
//        _itemSelected = structure;
//    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}
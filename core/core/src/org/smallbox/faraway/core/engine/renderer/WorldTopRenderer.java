package org.smallbox.faraway.core.engine.renderer;

import com.badlogic.gdx.math.Rectangle;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.*;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.Constant;

public class WorldTopRenderer extends BaseRenderer {
    protected SpriteManager     _spriteManager;
    protected MapObjectModel    _itemSelected;
    private int                 _floor;
    private int                 _width;
    private int                 _height;

    @Override
    protected void onLoad(Game game) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _spriteManager = SpriteManager.getInstance();
    }

    @Override
    public int getLevel() {
        return MainRenderer.WORLD_TOP_RENDERER_LEVEL;
    }

    @Override
    protected void onUpdate() {
        ModuleHelper.getWorldModule().getStructures().forEach(structure -> {
            ParcelModel parcel = structure.getParcel();

            if ((structure.isWall() || structure.isDoor()) && structure.getInfo().graphics != null) {
                int tile = 0;
                if (WorldHelper.hasRock(parcel.x - 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y - 1, parcel.z)) { tile |= 0b10000000; }
                if (WorldHelper.hasRock(parcel.x,     parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y - 1, parcel.z)) { tile |= 0b01000000; }
                if (WorldHelper.hasRock(parcel.x + 1, parcel.y - 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y - 1, parcel.z)) { tile |= 0b00100000; }
                if (WorldHelper.hasRock(parcel.x - 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y,     parcel.z)) { tile |= 0b00010000; }
                if (WorldHelper.hasRock(parcel.x + 1, parcel.y,     parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y,     parcel.z)) { tile |= 0b00001000; }
                if (WorldHelper.hasRock(parcel.x - 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x - 1, parcel.y + 1, parcel.z)) { tile |= 0b00000100; }
                if (WorldHelper.hasRock(parcel.x,     parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x,     parcel.y + 1, parcel.z)) { tile |= 0b00000010; }
                if (WorldHelper.hasRock(parcel.x + 1, parcel.y + 1, parcel.z) || WorldHelper.hasWallOrDoor(parcel.x + 1, parcel.y + 1, parcel.z)) { tile |= 0b00000001; }
                parcel.setTile(tile);
            }
        });

        ModuleHelper.getWorldModule().getPlants().forEach(plant -> {
            if (plant.getInfo().graphics != null) {
                ParcelModel parcel = plant.getParcel();
                ItemInfo plantInfo = plant.getInfo();

                int tile = 0;
                if (WorldHelper.getPlantInfo(parcel.x - 1, parcel.y - 1, parcel.z) == plantInfo) { tile |= 0b10000000; }
                if (WorldHelper.getPlantInfo(parcel.x,     parcel.y - 1, parcel.z) == plantInfo) { tile |= 0b01000000; }
                if (WorldHelper.getPlantInfo(parcel.x + 1, parcel.y - 1, parcel.z) == plantInfo) { tile |= 0b00100000; }
                if (WorldHelper.getPlantInfo(parcel.x - 1, parcel.y,     parcel.z) == plantInfo) { tile |= 0b00010000; }
                if (WorldHelper.getPlantInfo(parcel.x + 1, parcel.y,     parcel.z) == plantInfo) { tile |= 0b00001000; }
                if (WorldHelper.getPlantInfo(parcel.x - 1, parcel.y + 1, parcel.z) == plantInfo) { tile |= 0b00000100; }
                if (WorldHelper.getPlantInfo(parcel.x,     parcel.y + 1, parcel.z) == plantInfo) { tile |= 0b00000010; }
                if (WorldHelper.getPlantInfo(parcel.x + 1, parcel.y + 1, parcel.z) == plantInfo) { tile |= 0b00000001; }
                parcel.setTile(tile);
            }
        });
    }

    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        int fromX = (int) Math.max(0, (-viewport.getPosX() / Constant.TILE_WIDTH) * viewport.getScale());
        int fromY = (int) Math.max(0, (-viewport.getPosY() / Constant.TILE_HEIGHT) * viewport.getScale());
        int toX = Math.min(_width, fromX + 50);
        int toY = Math.min(_height, fromY + 40);

        int viewportX = viewport.getPosX();
        int viewportY = viewport.getPosY();

        ParcelModel[][][] parcels = ModuleHelper.getWorldModule().getParcels();
        for (int x = toX-1; x >= fromX; x--) {
            for (int y = toY-1; y >= fromY; y--) {
                ParcelModel parcel = parcels[x][y][_floor];
                if (parcel.hasPlant()) {
                    renderer.draw(_spriteManager.getItem(parcel.getPlant().getGraphic(), parcel.getTile(), parcel.getPlant().getTile()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
                if (parcel.getStructure() != null) {
                    renderer.draw(_spriteManager.getItem(parcel.getStructure()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
                if (parcel.getNetworkObjects() != null) {
                    for (NetworkObjectModel networkObject: parcel.getNetworkObjects()) {
                        renderer.draw(_spriteManager.getItem(networkObject), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                    }
                }
                if (parcel.getItem() != null && parcel == parcel.getItem().getParcel()) {
                    renderer.draw(_spriteManager.getItem(parcel.getItem()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
                if (parcel.getConsumable() != null) {
                    renderer.draw(_spriteManager.getItem(parcel.getConsumable()), (x * Constant.TILE_WIDTH) + viewportX, (y * Constant.TILE_HEIGHT) + viewportY);
                }
            }
        }
    }

    @Override
    public void onRefresh(int frame) {
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
            renderer.draw(_spriteManager.getSelectorCorner(0), x - offset + fromX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(1), x + offset + toX, y - offset + fromY);
            renderer.draw(_spriteManager.getSelectorCorner(2), x - offset + fromX, y + offset + toY);
            renderer.draw(_spriteManager.getSelectorCorner(3), x + offset + toX, y + offset + toY);

            if (_itemSelected.getInfo().slots != null) {
                for (int[] slot: _itemSelected.getInfo().slots) {
                    renderer.draw(_spriteManager.getIcon("data/res/ic_slot.png"),
                            (_itemSelected.getParcel().x + slot[0])* Constant.TILE_WIDTH + viewport.getPosX(),
                            (_itemSelected.getParcel().y + slot[1])* Constant.TILE_HEIGHT + viewport.getPosY());
                }
            }
        }
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

    @Override
    public void onSelectItem(ItemModel item) {
        _itemSelected = item;
    }

    @Override
    public void onSelectPlant(PlantModel resource) {
        _itemSelected = resource;
    }

    @Override
    public void onSelectConsumable(ConsumableModel consumable) {
        _itemSelected = consumable;
    }

    @Override
    public void onSelectStructure(StructureModel structure) {
        _itemSelected = structure;
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}
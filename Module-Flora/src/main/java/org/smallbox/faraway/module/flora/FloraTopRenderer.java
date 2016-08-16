package org.smallbox.faraway.module.flora;

import org.smallbox.faraway.core.engine.renderer.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

public class FloraTopRenderer extends BaseRenderer {
    private final FloraModule _floraModule;
    protected SpriteManager _spriteManager;
    protected MapObjectModel    _itemSelected;
    private int                 _floor;
    private int                 _width;
    private int                 _height;

    public FloraTopRenderer(FloraModule floraModule) {
        _floraModule = floraModule;
    }

    @Override
    protected void onGameStart(Game game) {
        _width = game.getInfo().worldWidth;
        _height = game.getInfo().worldHeight;
        _spriteManager = SpriteManager.getInstance();
    }

    @Override
    public int getLevel() {
        return MainRenderer.WORLD_TOP_RENDERER_LEVEL;
    }

    @Override
    protected void onGameUpdate() {
        _floraModule.getPlants().forEach(plant -> {
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

    @Override
    public void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public void onFloorChange(int floor) {
        _floor = floor;
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}
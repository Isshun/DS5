package org.smallbox.faraway.module.flora;

import org.smallbox.faraway.core.dependencyInjector.BindManager;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.client.renderer.*;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public class FloraTopRenderer extends BaseRenderer {

    @BindModule
    private FloraModule floraModule;

    @BindManager
    protected SpriteManager spriteManager;

    protected MapObjectModel    _itemSelected;

    @Override
    public int getLevel() {
        return MainRenderer.WORLD_TOP_RENDERER_LEVEL;
    }

    @Override
    protected void onGameUpdate() {
        floraModule.getPlants().forEach(plant -> {
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
    public void onDraw(GDXRendererBase renderer, Viewport viewport, double animProgress) {
    }

    @Override
    public void onRefresh(int frame) {
    }

    @Override
    public void onDeselect() {
        _itemSelected = null;
    }
}
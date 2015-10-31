package org.smallbox.faraway.core.game.module.area.model;

import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.world.model.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;

import java.util.Map;

/**
 * Created by Alex on 03/07/2015.
 */
public class GardenAreaModel extends AreaModel {
    private ItemInfo    _resourceInfo;

    public GardenAreaModel() {
        super(AreaType.GARDEN);

        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isResource && itemInfo.actions != null && !itemInfo.actions.isEmpty() && "gather".equals(itemInfo.actions.get(0).type)) {
                setAccept(itemInfo, false);
            }
        }
    }

    private void resetFields() {
        for (ParcelModel parcel: _parcels) {
            ModuleHelper.getWorldModule().putObject(parcel, _resourceInfo, 0);
        }
    }

    @Override
    public void addParcel(ParcelModel parcel) {
        super.addParcel(parcel);

        if (_resourceInfo != null && (parcel.getResource() == null || parcel.getResource().getInfo() != _resourceInfo)) {
            ModuleHelper.getWorldModule().putObject(parcel, _resourceInfo, 0);
        }
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        for (Map.Entry<ItemInfo, Boolean> entry: _items.entrySet()) {
            _items.put(entry.getKey(), false);
        }
        _items.put(itemInfo, true);
        _resourceInfo = itemInfo;
        resetFields();
    }

    @Override
    public String getName() {
        return _resourceInfo != null ? _resourceInfo.label + " garden" : "Garden";
    }

}

package org.smallbox.faraway.game.model.area;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;

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
            Game.getWorldManager().putObject(_resourceInfo, parcel.getX(), parcel.getY(), parcel.getZ(), 0);
        }
    }

    @Override
    public void addParcel(ParcelModel parcel) {
        super.addParcel(parcel);

        if (_resourceInfo != null && (parcel.getResource() == null || parcel.getResource().getInfo() != _resourceInfo)) {
            Game.getWorldManager().putObject(_resourceInfo, parcel.getX(), parcel.getY(), parcel.getZ(), 0);
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

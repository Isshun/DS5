package org.smallbox.faraway.ui;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModel {
    private Map<ItemInfo, Boolean>      _items;
    protected final Set<ParcelModel>    _parcels = new HashSet<>();
    private final AreaType              _type;

    public AreaModel(AreaType type) {
        _type = type;
        _items = new HashMap<>();

        for (ItemInfo itemInfo: GameData.getData().items) {
            if (itemInfo.isConsumable || itemInfo.isResource) {
                _items.put(itemInfo, false);
            }
        }
    }

    public void addParcel(ParcelModel parcel) {
        if (!_parcels.contains(parcel)) {
            _parcels.add(parcel);
            parcel.setArea(this);
        }
    }

    public boolean contains(int x, int y) {
        for (ParcelModel parcel: _parcels) {
            if (parcel.getX() == x && parcel.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public Collection<ParcelModel> getParcels() {
        return _parcels;
    }

    public String getName() {
        return "Storage Area #n";
    }

    public boolean isStorage() {
        return false;
    }

    public AreaType getType() {
        return _type;
    }

    public boolean accept(ItemInfo itemInfo) {
        return _items.containsKey(itemInfo) && _items.get(itemInfo);
    }

    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
        _items.put(itemInfo, isAccepted);
    }

    public Map<ItemInfo, Boolean> getItemsAccepts() {
        return _items;
    }
}

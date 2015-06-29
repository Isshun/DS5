package org.smallbox.faraway.ui;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;

import java.util.*;

/**
 * Created by Alex on 13/06/2015.
 */
public class AreaModel {
    private Map<ItemInfo, Boolean>      _items;
    protected final Set<ParcelModel>    _parcels = new HashSet<>();
    private final AreaType              _type;
    private int                         _x;
    private int                         _y;

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
            _x = parcel.getX();
            _y = parcel.getY();
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
        return "Area #n";
    }

    public boolean isStorage() {
        return false;
    }

    public boolean isHome() {
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

    public void removeParcel(ParcelModel parcel) {
        _parcels.remove(parcel);
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }
}

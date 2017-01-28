package org.smallbox.faraway.core.module.world.model;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableItem extends MapObjectModel {
    private int             _quantity = 1;
    private int             _slots = 0;
    private JobModel        _job;

    public ConsumableItem(ItemInfo info) {
        super(info);
    }

    public ConsumableItem(ItemInfo info, int quantity) {
        super(info);
        _quantity = quantity;
    }

    public void addQuantity(int quantity) {
        _quantity += quantity;
        _needRefresh = true;
    }

    public int getQuantity() {
        return _quantity;
    }

    public void setQuantity(int quantity) {
        _quantity = quantity;
        _needRefresh = true;
    }

    @Override
    public boolean matchFilter(ItemFilter filter) {
        if (_info.consume != null && _info.consume.effects != null) {
            if (filter.effectDrink && _info.consume.effects.drink == 0) return false;
            if (filter.effectFood && _info.consume.effects.food == 0) return false;
            if (filter.effectEnergy && _info.consume.effects.energy == 0) return false;
            if (filter.effectEntertainment && _info.consume.effects.entertainment == 0) return false;
            if (filter.effectHappiness && _info.consume.effects.happiness == 0) return false;
            if (filter.effectHealth && _info.consume.effects.health == 0) return false;
            if (filter.effectRelation && _info.consume.effects.relation == 0) return false;
            return true;
        }
        return false;
    }

    public String getFullLabel() { return getLabel() + " (" + _quantity + ")"; }
    public boolean isEmpty() { return _quantity <= 0; }
    public void setJob(JobModel job) { _job = job; }
    public JobModel getJob() { return _job; }
    public boolean inValidStorage() { return _parcel.getArea() != null && _parcel.getArea().accept(_info); }
    public boolean hasFreeSlot() { return _slots < _quantity; }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            ParcelModel parcel = WorldHelper.getNearestFreeParcel(_parcel, true, false);
            if (parcel != null) {
                _parcel = parcel;
            }
        }
    }

    public StorageAreaModel getStorage() {
        return _parcel != null && _parcel.getArea() != null && _parcel.getArea().isStorage() ? (StorageAreaModel) _parcel.getArea() : null;
    }

//    public void setStoreJob(StoreJob job) { _storeJob = job; }
//    public StoreJob getStoreJob() { return _storeJob; }

    public void consume(CharacterModel character, int durationLeft) {
        // Add buffEffect on characters
        character.apply(_info.consume);
    }

    @Override
    public String toString() { return _info.name + " at " + _parcel; }
}

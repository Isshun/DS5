package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.area.model.StorageAreaModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.StoreJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableModel extends MapObjectModel {
    private int             _quantity = 1;
    private int             _slots = 0;
    private JobModel        _lock;
    private StoreJob        _storeJob;

    public ConsumableModel(ItemInfo info) {
        super(info);
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
    public void lock(JobModel lock) { _lock = lock; }
    public JobModel getLock() { return _lock; }
    public boolean inValidStorage() { return _parcel.getArea() != null && _parcel.getArea().accept(_info); }
    public boolean hasFreeSlot() { return _slots < _quantity; }

    public void fixPosition() {
        if (_parcel != null && !_parcel.isWalkable()) {
            ParcelModel parcel = WorldHelper.getNearestFreeParcel(_parcel.x, _parcel.y, true, false);
            if (parcel != null) {
                _parcel = parcel;
            }
        }
    }

    public StorageAreaModel getStorage() {
        return _parcel != null && _parcel.getArea() != null && _parcel.getArea().isStorage() ? (StorageAreaModel) _parcel.getArea() : null;
    }

    public void setStoreJob(StoreJob job) { _storeJob = job; }
    public StoreJob getStoreJob() { return _storeJob; }

    public void consume(CharacterModel character, int durationLeft) {
        // Add buffEffect on characters
        character.getNeeds().use(this, _info.consume.effects, _info.consume.cost);
    }
}

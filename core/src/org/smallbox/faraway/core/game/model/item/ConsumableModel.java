package org.smallbox.faraway.core.game.model.item;

import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.area.StorageAreaModel;
import org.smallbox.faraway.core.game.model.job.abs.JobModel;
import org.smallbox.faraway.core.game.model.job.StoreJob;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableModel extends MapObjectModel {
    private int             _quantity = 1;
    private int             _slots = 0;
    private JobModel _lock;
    private StoreJob _storeJob;

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
                _x = parcel.x;
                _y = parcel.y;
            }
        }
    }

    public StorageAreaModel getStorage() {
        return _parcel != null && _parcel.getArea() != null && _parcel.getArea().isStorage() ? (StorageAreaModel) _parcel.getArea() : null;
    }

    public void setStoreJob(StoreJob job) { _storeJob = job; }
    public StoreJob getStoreJob() { return _storeJob; }
}

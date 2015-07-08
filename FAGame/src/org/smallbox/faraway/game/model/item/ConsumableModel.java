package org.smallbox.faraway.game.model.item;

import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobHaul;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableModel extends MapObjectModel {
    private int             _quantity = 1;
    private int             _slots = 0;
    private JobHaul         _job;
    private BaseJobModel    _lock;

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

    public String getFullLabel() {
        return getLabel() + " (" + _quantity + ")";
    }

    public JobHaul getHaul() {
        return _job;
    }

    public void setHaul(JobHaul job) {
        _job = job;
    }

    public boolean isEmpty() { return _quantity <= 0; }

    public void lock(BaseJobModel lock) {
        _lock = lock;
    }

    public BaseJobModel getLock() {
        return _lock;
    }

    public boolean inValidStorage() {
        return _parcel.getArea() != null && _parcel.getArea().accept(_info);
    }

    public boolean hasFreeSlot() {
        return _slots < _quantity;
    }
}

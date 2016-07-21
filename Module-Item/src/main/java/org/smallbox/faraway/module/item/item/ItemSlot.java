package org.smallbox.faraway.module.item.item;

import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

public class ItemSlot {
    private final ParcelModel _parcel;
    private final ItemModel     _item;
    private JobModel            _job;

    public ItemSlot(ItemModel item, ParcelModel parcel) {
        _item = item;
        _parcel = parcel;
    }

    public boolean      isFree() { return _job == null; }
    public ParcelModel  getParcel() { return _parcel; }
    public JobModel     getJob() { return _job; }
    public ItemModel    getItem() { return _item; }
    public void         take(JobModel job) { _job = job; }
    public void         free() { _job = null; }
}
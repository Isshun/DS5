package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public class ItemSlot {
    private final ParcelModel _parcel;
    private final UsableItem _item;
    private JobModel            _job;

    public ItemSlot(UsableItem item, ParcelModel parcel) {
        _item = item;
        _parcel = parcel;
    }

    public boolean      isFree() { return _job == null; }
    public ParcelModel  getParcel() { return _parcel; }
    public JobModel     getJob() { return _job; }
    public UsableItem getItem() { return _item; }
    public void         take(JobModel job) { _job = job; }
    public void         free() { _job = null; }
}
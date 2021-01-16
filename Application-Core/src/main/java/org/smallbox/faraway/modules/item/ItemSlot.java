package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.job.JobModel;

public class ItemSlot {
    private final Parcel _parcel;
    private final UsableItem _item;
    private JobModel            _job;

    public ItemSlot(UsableItem item, Parcel parcel) {
        _item = item;
        _parcel = parcel;
    }

    public boolean      isFree() { return _job == null; }
    public Parcel getParcel() { return _parcel; }
    public JobModel     getJob() { return _job; }
    public UsableItem getItem() { return _item; }
    public void         take(JobModel job) { _job = job; }
    public void         free() { _job = null; }
}
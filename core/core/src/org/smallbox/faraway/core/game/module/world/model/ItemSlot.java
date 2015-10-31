package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

public class ItemSlot {
    private JobModel _job;
    private ItemModel       _item;
    private int             _relX;
    private int             _relY;

    public ItemSlot(ItemModel item, int x, int y) {
        _item = item;
        _relX = x;
        _relY = y;
    }

    public boolean      isFree() { return _job == null; }
    public int          getX() { return _item.getX() + _relX; }
    public int          getY() { return _item.getY() + _relY; }
    public JobModel getJob() { return _job; }
    public ItemModel getItem() { return _item; }
    public void         take(JobModel job) { _job = job; }
    public void         free() { _job = null; }
}
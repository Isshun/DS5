package org.smallbox.faraway.game.model.item;

import org.smallbox.faraway.game.model.job.BaseJobModel;

public class ItemSlot {
    private BaseJobModel    _job;
    private ItemModel       _item;
    private int			    _relX;
    private int			    _relY;

    public ItemSlot(ItemModel item, int x, int y) {
        _item = item;
        _relX = x;
        _relY = y;
    }

    public boolean      isFree() { return _job == null; }
    public int          getX() { return _item.getX() + _relX; }
    public int          getY() { return _item.getY() + _relY; }
    public BaseJobModel getJob() { return _job; }
    public ItemModel    getItem() { return _item; }
    public void         take(BaseJobModel job) { _job = job; }
    public void         free() { _job = null; }
}
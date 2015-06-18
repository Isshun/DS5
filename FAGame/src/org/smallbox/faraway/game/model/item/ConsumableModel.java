package org.smallbox.faraway.game.model.item;

import org.smallbox.faraway.game.model.job.JobHaul;
import org.smallbox.faraway.game.model.job.JobModel;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableModel extends MapObjectModel {
    private ItemSlot    _slot;
    private int         _quantity = 1;
    private int         _slots = 1;
    private JobHaul     _job;

    public ConsumableModel(ItemInfo info) {
        super(info);

        _slot = new ItemSlot(this, 0, 0);
    }

    public void addQuantity(int quantity) {
        _quantity += quantity;
        _slots += quantity;
        _needRefresh = true;
    }

    @Override
    public int getQuantity() {
        return _quantity;
    }

    public void setQuantity(int quantity) {
        _quantity = quantity;
        _slots = quantity;
        _needRefresh = true;
    }

    @Override
    public boolean 			hasFreeSlot() { return _slots > 0; }

    @Override
    public ItemSlot takeSlot(JobModel job) {
        if (_slots > 0) {
            _slots--;
            return _slot;
        }
        return null;
    }

    @Override
    public void releaseSlot(ItemSlot slot) {
        _slots++;
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
}

package org.smallbox.faraway.model.item;

import org.smallbox.faraway.model.job.BaseJob;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableItem extends ItemBase {
    private ItemSlot _slot;
    private int _quantity = 1;
    private int _slots = 1;

    public ConsumableItem(ItemInfo info) {
        super(info);

        _slot = new ItemSlot(this, 0, 0);
    }

    public void addQuantity(int quantity) {
        _quantity += quantity;
        _slots += quantity;
    }

    @Override
    public int getQuantity() {
        return _quantity;
    }

    public void setQuantity(int quantity) {
        _quantity = quantity;
        _slots = quantity;
    }

    @Override
    public boolean 			hasFreeSlot() { return _slots > 0; }

    @Override
    public ItemSlot takeSlot(BaseJob job) {
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

}

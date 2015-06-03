package org.smallbox.faraway.model.item;

/**
 * Created by Alex on 03/06/2015.
 */
public class ConsumableItem extends ItemBase {
    private int _quantity = 1;

    public ConsumableItem(ItemInfo info) {
        super(info);
    }

    public void addQuantity(int quantity) {
        _quantity += quantity;
    }

    public int getQuantity() {
        return _quantity;
    }

    public void setQuantity(int quantity) {
        _quantity = quantity;
    }

}

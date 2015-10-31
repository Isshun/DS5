package org.smallbox.faraway.core.game.module.world.model.resource;

/**
 * Created by Alex on 31/10/2015.
 */
public class RockExtra {
    private int     _quantity;

    public void     setQuantity(int quantity) { _quantity = quantity; }
    public int      getQuantity() { return _quantity; }
    public boolean  isDepleted() { return _quantity < 1; }
}

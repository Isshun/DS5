package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.GraphicInfo;
import org.smallbox.faraway.core.game.model.NetworkInfo;
import org.smallbox.faraway.core.game.model.NetworkModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 04/11/2015.
 */
public class NetworkObjectModel {
    private final ItemModel     _item;
    private final NetworkInfo   _info;
    private boolean             _isComplete;
    private ParcelModel         _parcel;
    private int                 _health;
    private NetworkModel        _network;
    private double              _quantity;

    public NetworkObjectModel(NetworkInfo info) {
        _info = info;
        _item = null;
    }

    public NetworkObjectModel(NetworkInfo info, ItemModel item) {
        _info = info;
        _item = item;
    }

    public void         setComplete(boolean complete) { _isComplete = complete; }
    public void         setParcel(ParcelModel parcel) { _parcel = parcel; }
    public void         setNetwork(NetworkModel network) { _network = network; }
    public void         setQuantity(double quantity) { _quantity = quantity; }

    public NetworkModel getNetwork() { return _network; }
    public ParcelModel  getParcel() { return _parcel; }
    public NetworkInfo  getInfo() { return _info; }
    public GraphicInfo  getGraphic() { return _info.graphics; }
    public int          getHealth() { return _health; }
    public int          getMaxHealth() { return _info.health; }
    public double       getQuantity() { return _quantity; }
    public int          getMaxQuantity() { return _info.quantity; }

    public boolean      isComplete() { return _isComplete; }
}
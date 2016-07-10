package org.smallbox.faraway.core.game.module.world.model;

import org.smallbox.faraway.core.game.model.NetworkModel;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 04/11/2015.
 */
public class NetworkObjectModel extends MapObjectModel {
    private final ItemModel         _item;
    private final ItemInfo          _info;
    private final NetworkInfo       _networkInfo;
    private boolean                 _isComplete;
    private ParcelModel             _parcel;
    private int                     _health;
    private NetworkModel            _network;
    private double                  _quantity;

    public NetworkObjectModel(ItemInfo itemInfo, NetworkInfo networkInfo) {
        super(itemInfo);
        _info = itemInfo;
        _networkInfo = networkInfo;
        _item = null;
    }

    public NetworkObjectModel(ItemInfo itemInfo, NetworkInfo networkInfo, ItemModel item) {
        super(itemInfo);
        _info = itemInfo;
        _networkInfo = networkInfo;
        _item = item;
    }

    public void         setComplete(boolean complete) { _isComplete = complete; }
    public void         setParcel(ParcelModel parcel) { _parcel = parcel; }
    public void         setNetwork(NetworkModel network) { _network = network; }
    public void         setQuantity(double quantity) { _quantity = quantity; }

    public NetworkModel getNetwork() { return _network; }
    public ParcelModel  getParcel() { return _parcel; }
    public NetworkInfo  getNetworkInfo() { return _networkInfo; }
    public ItemInfo     getInfo() { return _info; }
    public GraphicInfo  getGraphic() { return _info.graphics != null ? _info.graphics.get(0) : null; }
    public int          getHealth() { return _health; }
    public int          getMaxHealth() { return _info.health; }
    public double       getQuantity() { return _quantity; }
    public int          getMaxQuantity() { return 10; }

    public boolean      isComplete() { return _isComplete; }
}
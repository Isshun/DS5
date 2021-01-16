package org.smallbox.faraway.core.module.world.model;

import org.smallbox.faraway.core.game.model.NetworkModel;
import org.smallbox.faraway.core.game.modelInfo.GraphicInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;

public class NetworkItem extends MapObjectModel {
    private final ItemInfo          _info;
    private final NetworkInfo       _networkInfo;
    private boolean                 _isComplete;
    private Parcel _parcel;
    private int                     _health;
    private NetworkModel            _network;
    private double                  _quantity;

    public NetworkItem(ItemInfo itemInfo, NetworkInfo networkInfo) {
        super(itemInfo);
        _info = itemInfo;
        _networkInfo = networkInfo;
    }

//    public NetworkItem(ItemInfo itemInfo, NetworkInfo networkInfo, UsableItem item) {
//        super(itemInfo);
//        _info = itemInfo;
//        _networkInfo = networkInfo;
//    }

    public void         setComplete(boolean complete) { _isComplete = complete; }
    public void         setParcel(Parcel parcel) { _parcel = parcel; }
    public void         setNetwork(NetworkModel network) { _network = network; }
    public void         setQuantity(double quantity) { _quantity = quantity; }

    public NetworkModel getNetwork() { return _network; }
    public Parcel getParcel() { return _parcel; }
    public NetworkInfo  getNetworkInfo() { return _networkInfo; }
    public ItemInfo     getInfo() { return _info; }
    public GraphicInfo  getGraphic() { return _info.graphics != null ? _info.graphics.get(0) : null; }
    public int          getHealth() { return _health; }
    public int          getMaxHealth() { return _info.health; }
    public double       getQuantity() { return _quantity; }
    public int          getMaxQuantity() { return 10; }

    public boolean      isComplete() { return _isComplete; }
}
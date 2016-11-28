package org.smallbox.faraway.module.item.item;

import org.smallbox.faraway.core.game.model.NetworkModel;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.module.world.model.NetworkObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

/**
 * Created by Alex on 05/11/2015.
 */
public class NetworkConnectionModel {
    private final NetworkInfo   _networkInfo;
    private final int           _distance;
    private NetworkModel        _network;
    private NetworkObjectModel  _object;
    private ParcelModel         _parcel;

    public NetworkConnectionModel(NetworkInfo networkInfo, int distance) {
        _networkInfo = networkInfo;
        _distance = distance;
    }

    public void             setParcel(ParcelModel parcel) { _parcel = parcel; }
    public void             setNetwork(NetworkModel network) { _network = network; }
    public void             setNetworkObject(NetworkObjectModel networkObject) { _object = networkObject; }

    public NetworkInfo      getNetworkInfo() { return _networkInfo; }
    public NetworkModel     getNetwork() { return _network; }
    public ParcelModel      getParcel() { return _parcel; }
    public int              getDistance() { return _distance; }
}

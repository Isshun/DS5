package org.smallbox.faraway.game.item;

import org.smallbox.faraway.core.game.model.NetworkModel;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.world.model.NetworkItem;
import org.smallbox.faraway.game.world.Parcel;

public class NetworkConnectionModel {
    private final NetworkInfo   _networkInfo;
    private final int           _distance;
    private NetworkModel        _network;
    private NetworkItem _object;
    private Parcel _parcel;

    public NetworkConnectionModel(NetworkInfo networkInfo, int distance) {
        _networkInfo = networkInfo;
        _distance = distance;
    }

    public void             setParcel(Parcel parcel) { _parcel = parcel; }
    public void             setNetwork(NetworkModel network) { _network = network; }
    public void             setNetworkObject(NetworkItem networkObject) { _object = networkObject; }

    public NetworkInfo      getNetworkInfo() { return _networkInfo; }
    public NetworkModel     getNetwork() { return _network; }
    public Parcel getParcel() { return _parcel; }
    public int              getDistance() { return _distance; }
}

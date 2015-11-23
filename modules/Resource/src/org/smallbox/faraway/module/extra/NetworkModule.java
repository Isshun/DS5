package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.game.model.NetworkModel;
import org.smallbox.faraway.core.game.module.world.model.NetworkObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.item.NetworkConnectionModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alex on 05/07/2015.
 */
public class NetworkModule extends GameModule {
    private Set<NetworkModel>           _networks = new HashSet<>();
    private Set<NetworkObjectModel>     _networkObjects = new HashSet<>();
    private Set<NetworkConnectionModel> _networkConnections= new HashSet<>();

    @Override
    protected void onLoaded(Game game) {
    }

    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    protected void onUpdate(int tick) {
    }

    public Collection<NetworkModel> getNetworks() { return _networks; }

    @Override
    public void onAddItem(ItemModel item) {
        if (item.getNetworkConnections() != null) {
            _networkConnections.addAll(item.getNetworkConnections());

            // Recreate network for orphan objects
            collectOrphans();
            connectItems();
        }
    }

    @Override
    public void onRemoveItem(ItemModel item) {
        if (item.getNetworkConnections() != null) {
            _networkConnections.removeAll(item.getNetworkConnections());

            // Recreate network for orphan objects
            collectOrphans();
            connectItems();
        }
    }

    @Override
    public void onAddNetworkObject(NetworkObjectModel networkObject) {
        _networkObjects.add(networkObject);

        // Recreate network for orphan objects
        collectOrphans();
        connectItems();
    }

    @Override
    public void onRemoveNetworkObject(NetworkObjectModel networkObject) {
        _networkObjects.remove(networkObject);

        // Recreate network for orphan objects
        collectOrphans();
        connectItems();
    }

    private void collectOrphans() {
        _networks.clear();
        _networkObjects.forEach(networkObject -> networkObject.setNetwork(null));

        // Create new networks for remaining orphans
        boolean networkHasBeenCreated;
        do {
            networkHasBeenCreated = false;
            for (NetworkObjectModel object : _networkObjects) {
                if (object.getNetwork() == null) {
                    NetworkModel network = new NetworkModel(object.getNetworkInfo());
                    explore(network, object.getNetworkInfo(), object.getParcel());
                    _networks.add(network);
                    networkHasBeenCreated = true;
                }
            }
        } while (networkHasBeenCreated);
    }

    private void explore(NetworkModel network, NetworkInfo networkInfo, ParcelModel parcel) {
        NetworkObjectModel object = parcel.getNetworkObject(networkInfo);
        if (object != null && !network.contains(object)) {
            network.addObject(object);

            ParcelModel targetParcel;
            if ((targetParcel = WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z)) != null) explore(network, networkInfo, targetParcel);
            if ((targetParcel = WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z)) != null) explore(network, networkInfo, targetParcel);
            if ((targetParcel = WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z)) != null) explore(network, networkInfo, targetParcel);
            if ((targetParcel = WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z)) != null) explore(network, networkInfo, targetParcel);
            if ((targetParcel = WorldHelper.getParcel(parcel.x, parcel.y, parcel.z + 1)) != null) explore(network, networkInfo, targetParcel);
            if ((targetParcel = WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1)) != null) explore(network, networkInfo, targetParcel);
        }
    }

    private void connectItems() {
        _networkConnections.forEach(connection -> {
            ParcelModel p1 = connection.getParcel();
            int distance = connection.getDistance();
            int bestDistance = -1;
            for (int x = p1.x - distance; x <= p1.x + distance; x++) {
                for (int y = p1.y - distance; y <= p1.y + distance; y++) {
                    for (int z = p1.z - 1; z <= p1.z + 1; z++) {
                        ParcelModel p2 = WorldHelper.getParcel(x, y, z);
                        if (p2 != null && p2.hasNetwork(connection.getNetworkInfo()) && (bestDistance == -1 || WorldHelper.getApproxDistance(p1, p2) < bestDistance)) {
                            NetworkObjectModel networkObject = p2.getNetworkObject(connection.getNetworkInfo());
                            if (networkObject != null && networkObject.getNetwork() != null) {
                                bestDistance = WorldHelper.getApproxDistance(p1, p2);
                                connection.setNetwork(networkObject.getNetwork());
                                connection.setNetworkObject(networkObject);
                            }
                        }
                    }
                }
            }
        });
    }
}
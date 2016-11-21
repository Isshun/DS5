package org.smallbox.faraway.core.game.module.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.Collection;

/**
 * Created by Alex on 07/11/2015.
 */
public class IndexedGraph implements com.badlogic.gdx.ai.pfa.indexed.IndexedGraph<ParcelModel> {
    private final int _nodeCount;

    public IndexedGraph(Collection<ParcelModel> parcels) {
        _nodeCount = parcels.size();
        parcels.forEach(this::resetConnection);
    }

    public void addParcelToConnections(Array<Connection<ParcelModel>> array, ParcelModel parcel, int x, int y, int z) {
        ParcelModel toParcel = WorldHelper.getParcel(x, y, z);
        if (toParcel != null) {
            if (parcel.isWalkable() && toParcel.isWalkable()) {
                if (parcel.z == toParcel.z
                        || (toParcel.z == parcel.z - 1 && (!parcel.hasGround() || parcel.getGroundInfo().isLinkDown) && toParcel.getStructure() != null && toParcel.getStructure().getInfo().isRamp)
                        || (toParcel.z == parcel.z + 1 && (!toParcel.hasGround() || toParcel.getGroundInfo().isLinkDown) && parcel.getStructure() != null && parcel.getStructure().getInfo().isRamp)) {
                    array.add(new ParcelConnection(parcel, toParcel));
                }
            }
        }
    }

    public void resetConnection(ParcelModel parcel) {
        if (parcel != null) {
            Array<Connection<ParcelModel>> connections = new Array<>(6);
            addParcelToConnections(connections, parcel, parcel.x + 1, parcel.y, parcel.z);
            addParcelToConnections(connections, parcel, parcel.x - 1, parcel.y, parcel.z);
            addParcelToConnections(connections, parcel, parcel.x, parcel.y + 1, parcel.z);
            addParcelToConnections(connections, parcel, parcel.x, parcel.y - 1, parcel.z);
            addParcelToConnections(connections, parcel, parcel.x, parcel.y, parcel.z + 1);
            addParcelToConnections(connections, parcel, parcel.x, parcel.y, parcel.z - 1);
            parcel.setConnections(connections);
        }
    }

    @Override
    public Array<Connection<ParcelModel>> getConnections(ParcelModel parcel) {
        return parcel.getConnections();
    }

    @Override
    public int getNodeCount() {
        return _nodeCount;
    }

    public void resetAround(ParcelModel parcel) {
        resetConnection(WorldHelper.getParcel(parcel.x, parcel.y, parcel.z));
        resetConnection(WorldHelper.getParcel(parcel.x + 1, parcel.y, parcel.z));
        resetConnection(WorldHelper.getParcel(parcel.x - 1, parcel.y, parcel.z));
        resetConnection(WorldHelper.getParcel(parcel.x, parcel.y + 1, parcel.z));
        resetConnection(WorldHelper.getParcel(parcel.x, parcel.y - 1, parcel.z));
        resetConnection(WorldHelper.getParcel(parcel.x, parcel.y, parcel.z + 1));
        resetConnection(WorldHelper.getParcel(parcel.x, parcel.y, parcel.z - 1));
    }
}
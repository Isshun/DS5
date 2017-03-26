package org.smallbox.faraway.core.module.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Collection;

/**
 * Created by Alex on 07/11/2015.
 */
public class IndexedGraph implements com.badlogic.gdx.ai.pfa.indexed.IndexedGraph<ParcelModel> {
    private final int _nodeCount;

    public IndexedGraph(Collection<ParcelModel> parcels) {
        _nodeCount = parcels.size();
        parcels.forEach(parcel -> parcel.resetConnection());
    }

    @Override
    public Array<Connection<ParcelModel>> getConnections(ParcelModel parcel) {
        return parcel.getConnections();
    }

    @Override
    public int getNodeCount() {
        return _nodeCount;
    }

}
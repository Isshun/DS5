package org.smallbox.faraway.core.module.path.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.smallbox.faraway.core.game.helper.SurroundedPattern;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.Parcel;

public class ParcelGraph implements IndexedGraph<Parcel> {
    private final ObjectMap<Parcel, Array<Connection<Parcel>>> connections = new ObjectMap<>();
    private final int _nodeCount;

    public ParcelGraph(int nodeCount) {
        _nodeCount = nodeCount;
    }

    @Override
    public Array<Connection<Parcel>> getConnections(Parcel parcel) {
        if (!connections.containsKey(parcel)) {
            connections.put(parcel, new Array<>());
        }
        return connections.get(parcel);
    }

    @Override
    public int getIndex(Parcel node) {
        return node.getId();
    }

    @Override
    public int getNodeCount() {
        return _nodeCount;
    }

    public void refreshConnections(Parcel source) {
        if (!connections.containsKey(source)) {
            connections.put(source, new Array<>());
        }

        connections.get(source).clear();

        if (source.isWalkable()) {
            WorldHelper.getParcelAround(source, SurroundedPattern.X_SQUARE, Parcel::isWalkable, target -> createConnection(source, target));
        }
    }

    public void createConnection(Parcel fromParcel, Parcel toParcel) {
        if (!connections.containsKey(fromParcel)) {
            connections.put(fromParcel, new Array<>());
        }
        connections.get(fromParcel).add(new ParcelConnection(fromParcel, toParcel));
    }
}
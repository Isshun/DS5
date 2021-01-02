package org.smallbox.faraway.core.module.path.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.smallbox.faraway.core.game.helper.SurroundedPattern;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public class ParcelGraph implements IndexedGraph<ParcelModel> {
    private final ObjectMap<ParcelModel, Array<Connection<ParcelModel>>> connections = new ObjectMap<>();
    private final int _nodeCount;

    public ParcelGraph(int nodeCount) {
        _nodeCount = nodeCount;
    }

    @Override
    public Array<Connection<ParcelModel>> getConnections(ParcelModel parcel) {
        if (!connections.containsKey(parcel)) {
            connections.put(parcel, new Array<>());
        }
        return connections.get(parcel);
    }

    @Override
    public int getIndex(ParcelModel node) {
        return node.getId();
    }

    @Override
    public int getNodeCount() {
        return _nodeCount;
    }

    public void refreshConnections(ParcelModel source) {
        if (!connections.containsKey(source)) {
            connections.put(source, new Array<>());
        }

        connections.get(source).clear();

        if (source.isWalkable()) {
            WorldHelper.getParcelAround(source, SurroundedPattern.X_CROSS, ParcelModel::isWalkable, target -> createConnection(source, target));
        }
    }

    public void createConnection(ParcelModel fromParcel, ParcelModel toParcel) {
        if (!connections.containsKey(fromParcel)) {
            connections.put(fromParcel, new Array<>());
        }
        connections.get(fromParcel).add(new ParcelConnection(fromParcel, toParcel));
    }
}
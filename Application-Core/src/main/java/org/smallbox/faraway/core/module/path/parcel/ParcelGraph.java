package org.smallbox.faraway.core.module.path.parcel;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public class ParcelGraph implements IndexedGraph<ParcelModel> {
    private final ParcelHeuristic parcelHeuristic = new ParcelHeuristic();
    private final int _nodeCount;
    private ObjectMap<ParcelModel, Array<Connection<ParcelModel>>> connections = new ObjectMap<>();

    public ParcelGraph(int nodeCount) {
        _nodeCount = nodeCount;
    }

    public void resetConnection() {
//        Array<Connection<ParcelModel>> connections = new Array<>(6);
//        WorldHelper.getParcelArround(this, 1, toParcel -> addParcelToConnections(connections, toParcel));
//        setConnections(connections);
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

    public GraphPath<ParcelModel> findPath(ParcelModel startParcel, ParcelModel goalParcel) {
        GraphPath<ParcelModel> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startParcel, goalParcel, parcelHeuristic, path);
        return path;
    }

    public void refreshConnections(ParcelModel source) {
//        source.getConnections().clear();
//
//        WorldHelper.getParcelArround(source, 1, target -> {
//            if (source.isWalkable()) {
//                target.addConnection(source);
//            } else {
//                target.removeConnection(source);
//            }
//
//            if (target.isWalkable()) {
//                source.addConnection(target);
//            } else {
//                source.removeConnection(target);
//            }
//        });
    }

    public void createConnection(ParcelModel fromParcel, ParcelModel toParcel) {
        if (!connections.containsKey(fromParcel)) {
            connections.put(fromParcel, new Array<>());
        }
        connections.get(fromParcel).add(new ParcelConnection(fromParcel, toParcel));
    }
}
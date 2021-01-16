package org.smallbox.faraway.core.module.path.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.smallbox.faraway.core.game.helper.SurroundedPattern;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.Parcel;

/**
 * This graph contains extra connections for the last parcel to always considerate them as a walkable parcel
 * It's allow to build path to a non-walkable parcel (typically a job's target)
 */
public class TemporaryGraph implements IndexedGraph<Parcel> {

    private final ObjectMap<Parcel, Array<Connection<Parcel>>> connections = new ObjectMap<>();
    private final ParcelHeuristic parcelHeuristic = new ParcelHeuristic();
    private final ParcelGraph parcelGraph;
    private final boolean minusOne;

    public TemporaryGraph(ParcelGraph parcelGraph, Parcel toParcel, boolean minusOne) {
        this.parcelGraph = parcelGraph;
        this.minusOne = minusOne;

        if (minusOne) {
            connections.put(toParcel, new Array<>());
            WorldHelper.getParcelAround(toParcel, SurroundedPattern.CROSS, parcel -> {
                if (parcel.isWalkable()) {
                    connections.put(parcel, new Array<>());
                    connections.get(parcel).add(new ParcelConnection(parcel, toParcel));
                    connections.get(toParcel).add(new ParcelConnection(toParcel, parcel));
                }
            });
        }
    }

    public GraphPath<Parcel> findPath(Parcel startParcel, Parcel goalParcel) {
        GraphPath<Parcel> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startParcel, goalParcel, parcelHeuristic, path);
        return path;
    }

    @Override
    public Array<Connection<Parcel>> getConnections(Parcel fromNode) {
        if (connections.get(fromNode) != null) {
            return connections.get(fromNode);
        }
        return parcelGraph.getConnections(fromNode);
    }

    @Override
    public int getIndex(Parcel node) {
        return parcelGraph.getIndex(node);
    }

    @Override
    public int getNodeCount() {
        return parcelGraph.getNodeCount();
    }

}

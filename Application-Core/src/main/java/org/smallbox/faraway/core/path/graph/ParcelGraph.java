package org.smallbox.faraway.core.path.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Graph containing connections between parcels
 */
public class ParcelGraph implements IndexedGraph<Parcel> {
    private final ObjectMap<Parcel, Array<Connection<Parcel>>> connections = new ObjectMap<>();
    private final int nodeCount;

    public ParcelGraph(Collection<Parcel> parcels) {
        nodeCount = parcels.size();
        parcels.forEach(parcel -> {

            // Create empty connections
            connections.put(parcel, new Array<>());

            // Search and store parcel's neighbors
            List<Parcel> neighbors = new ArrayList<>();
            WorldHelper.getParcelAround(parcel, SurroundedPattern.X_SQUARE_3, neighbors::add);
            parcel.setNeighbors(neighbors);

            // Refresh connections
            refreshConnections(parcel);
        });
    }

    @Override
    public Array<Connection<Parcel>> getConnections(Parcel parcel) {
        return connections.get(parcel);
    }

    @Override
    public int getIndex(Parcel node) {
        return node.getId();
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * Refresh connections for a parcel, based on parcel's neighbors
     */
    public void refreshConnections(Parcel source) {
        connections.get(source).clear();

        if (source.isWalkable()) {
            source.getNeighbors().stream().filter(Parcel::isWalkable).forEach(neighbour -> {

                // Neighbour is on same floor
                if (neighbour.z == source.z) {
                    connections.get(source).add(new ParcelConnection(source, neighbour));
                }

                // Neighbour is bellow but have a ramp / stair
                else if (neighbour.z == source.z - 1 && neighbour.hasRamp()) {
                    connections.get(source).add(new ParcelConnection(source, neighbour));
                }

                // Neighbour is above but source have a ramp / stair
                else if (neighbour.z == source.z + 1 && source.hasRamp()) {
                    connections.get(source).add(new ParcelConnection(source, neighbour));
                }

            });

        }

    }

}
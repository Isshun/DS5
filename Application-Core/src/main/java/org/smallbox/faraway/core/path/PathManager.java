package org.smallbox.faraway.core.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.path.graph.ParcelGraph;
import org.smallbox.faraway.core.path.graph.ParcelHeuristic;
import org.smallbox.faraway.game.character.model.PathModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.Utils;
import org.smallbox.faraway.util.log.Log;

import java.util.Collection;

@GameObject
public class PathManager {
    @Inject private ParcelHeuristic parcelHeuristic;
    @Inject private ParcelGraph parcelGraph;

    public PathModel getPath(Parcel fromParcel, Collection<Parcel> toParcels) {
        for (Parcel toParcel: toParcels) {
            PathModel path = getPath(fromParcel, toParcel);
            if (path != null) {
                return path;
            }
        }
        return null;
    }

    public PathModel getPath(Parcel fromParcel, Parcel toParcel) {
        Utils.requireNonNull(fromParcel, PathManager.class, "fromParcel is null");
        Utils.requireNonNull(toParcel, PathManager.class, "toParcel is null");

        Log.debug(PathManager.class, "GetPath (from: " + fromParcel + " to: " + toParcel + ")");

        // Non walkable source or target
        if (!fromParcel.isWalkable() || !toParcel.isWalkable()) {
            return null;
        }

        // Looking for new path
        try {
            return new PathModel(findPath(fromParcel, toParcel));
        } catch (Exception e) {
            Log.debug(PathManager.class, "Unable to find path");
        }

        return null;
    }

    private GraphPath<Parcel> findPath(Parcel fromParcel, Parcel toParcel) {
        GraphPath<Parcel> path = new DefaultGraphPath<>();
        if (fromParcel == toParcel) {
            path.add(toParcel);
        } else {
            new IndexedAStarPathFinder<>(parcelGraph).searchNodePath(fromParcel, toParcel, parcelHeuristic, path);
        }
        return path;
    }

    public void refreshConnections(Parcel source) {
        WorldHelper.getParcelAround(source, SurroundedPattern.X_SQUARE_3, parcel -> parcelGraph.refreshConnections(parcel));
    }

    public boolean hasConnection(Parcel fromParcel, Parcel toParcel) {
        for (Connection<?> connection : parcelGraph.getConnections(fromParcel)) {
            if (connection.getToNode() == toParcel) {
                return true;
            }
        }
        return false;
    }

}

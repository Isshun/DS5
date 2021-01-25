package org.smallbox.faraway.core.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.core.path.graph.ParcelGraph;
import org.smallbox.faraway.core.path.graph.ParcelHeuristic;
import org.smallbox.faraway.game.character.model.PathModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GameObject
public class PathManager extends SuperGameModule {
    private static final int THREAD_POOL_SIZE = 1;
    @Inject private WorldModule worldModule;
    @Inject private Game game;

    final private ArrayList<Runnable> _runnable;
    final private ExecutorService _threadPool;
    final private ParcelHeuristic parcelHeuristic = new ParcelHeuristic();
    private IndexedAStarPathFinder<Parcel> _finder;
    private Map<Long, PathModel> _cache;
    private ParcelGraph parcelGraph;

    public PathManager() {
        _threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        _runnable = new ArrayList<>();
    }

    public void initParcels() {
        parcelGraph = new ParcelGraph(worldModule.getAll());
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _runnable.forEach(java.lang.Runnable::run);
        _runnable.clear();
    }

    public void close() {
        _threadPool.shutdownNow();
    }

    public PathModel getPath(Parcel fromParcel, Collection<Parcel> toParcels) {
        for (Parcel toParcel: toParcels) {
            PathModel path = getPath(fromParcel, toParcel, false, false, false);
            if (path != null) {
                return path;
            }
        }
        return null;
    }

    public PathModel getPath(Parcel fromParcel, Parcel toParcel, boolean horizontalApprox, boolean verticalApprox, boolean minusOne) {
        return getPath(fromParcel, toParcel, minusOne);
    }

    private PathModel getPath(Parcel fromParcel, Parcel toParcel, boolean minusOne) {
        if (fromParcel == null) {
            throw new GameException(PathManager.class, "fromParcel is null");
        }

        if (toParcel == null) {
            throw new GameException(PathManager.class, "toParcel is null");
        }

        Log.debug(PathManager.class, "GetPath (from: " + fromParcel.x + "x" + fromParcel.y + " to: " + toParcel.x + "x" + toParcel.y + ")");

        // Non walkable origin
        if (!fromParcel.isWalkable()) {
            return null;
        }

        // Empty path
        if (fromParcel == toParcel) {
            DefaultGraphPath<Parcel> nodes = new DefaultGraphPath<>();
            nodes.add(toParcel);
            return PathModel.create(nodes, minusOne);
        }

        // Get path from cache
//        long cacheId = getSum(fromParcel, toParcel);
//        PathModel path = _cache.get(cacheId);
//        if (path != null && path.isValid()) {
//            return path;
//        }

        // Looking for new path
        try {
            PathModel path = PathModel.create(findPath(fromParcel, toParcel), minusOne);
//        _cache.put(cacheId, path);

            // Non walkable last parcel
            if (!path.getLastParcelCharacter().isWalkable()) {
                return null;
            }

            return path;
        } catch (Exception e) {
            Log.warning("Unable to find path");
        }

        return null;
    }

    private GraphPath<Parcel> findPath(Parcel fromParcel, Parcel toParcel) {
        GraphPath<Parcel> path = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(parcelGraph).searchNodePath(fromParcel, toParcel, parcelHeuristic, path);
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

package org.smallbox.faraway.core.module.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.SuperGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.SurroundedPattern;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.path.graph.ParcelGraph;
import org.smallbox.faraway.core.module.path.graph.TemporaryGraph;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.world.WorldModule;
import org.smallbox.faraway.util.log.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@GameObject
public class PathManager extends SuperGameModule {
    private static final int                    THREAD_POOL_SIZE = 1;

    @Inject
    private WorldModule worldModule;

    @Inject
    private Game game;

    final private ArrayList<Runnable>           _runnable;
    final private ExecutorService               _threadPool;
    private IndexedAStarPathFinder<ParcelModel> _finder;
    private Map<Long, PathModel>                _cache;
    private ParcelGraph parcelGraph;

    public PathManager() {
        _threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        _runnable = new ArrayList<>();
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _runnable.forEach(java.lang.Runnable::run);
        _runnable.clear();
    }

    public void close() {
        _threadPool.shutdownNow();
    }

//    public boolean hasPath(ParcelModel fromParcel, ParcelModel toParcel) {
//        return getPath(fromParcel, toParcel) != null;
//    }

//    public boolean hasPath(ParcelModel fromParcel, ParcelModel toParcel, boolean horizontalApprox, boolean verticalApprox) {
//        return getPath(fromParcel, toParcel, horizontalApprox, verticalApprox) != null;
//    }

    public PathModel getPath(ParcelModel fromParcel, ParcelModel toParcel, boolean horizontalApprox, boolean verticalApprox, boolean minusOne) {
        return getPath(fromParcel, toParcel, minusOne);
//        assert fromParcel != null;
//        assert toParcel != null;
//
//        PathModel bestPath = null;
//
//        // Exact parcel
//        if (toParcel.isWalkable()) {
//            PathModel path = getPath(fromParcel, toParcel);
//            if (path != null) {
//                bestPath = path;
//            }
//        }
//
//        // Top / bottom / right / left parcel
//        if (bestPath == null && horizontalApprox) {
//            ParcelModel parcel = null;
//            PathModel path = null;
//            if ((parcel = WorldHelper.getParcel(toParcel.x - 1, toParcel.y, toParcel.z)) != null && parcel.isWalkable())
//                if ((path = getPath(fromParcel, parcel)) != null)
//                    bestPath = path;
//            if ((parcel = WorldHelper.getParcel(toParcel.x + 1, toParcel.y, toParcel.z)) != null && parcel.isWalkable())
//                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
//                    bestPath = path;
//            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y - 1, toParcel.z)) != null && parcel.isWalkable())
//                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
//                    bestPath = path;
//            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y + 1, toParcel.z)) != null && parcel.isWalkable())
//                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
//                    bestPath = path;
//        }
//
//        // Upper / lower parcel
//        if (bestPath == null && verticalApprox) {
//            ParcelModel parcel = null;
//            PathModel path = null;
//            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y, toParcel.z - 1)) != null && parcel.isWalkable())
//                if ((path = getPath(fromParcel, parcel)) != null)
//                    bestPath = path;
//            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y, toParcel.z + 1)) != null && parcel.isWalkable())
//                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
//                    bestPath = path;
//        }
//
//        return bestPath;
    }

    private PathModel getPath(ParcelModel fromParcel, ParcelModel toParcel, boolean minusOne) {
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
            DefaultGraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
            nodes.add(toParcel);
            return PathModel.create(nodes, minusOne);
        }

        // Get path from cache
        long cacheId = getSum(fromParcel, toParcel);
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

    public GraphPath<ParcelModel> findPath(ParcelModel fromParcel, ParcelModel toParcel) {
        assert fromParcel != null;
        assert toParcel != null;

        return new TemporaryGraph(parcelGraph, toParcel).findPath(fromParcel, toParcel);
//
//        if (_finder == null) {
//            parcelGraph = new IndexedGraph(worldModule.getAll());
//            _finder = new IndexedAStarPathFinder<>(parcelGraph);
//            _heuristic = (node, endNode) -> 10 * (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y));
//
//            // Create cache
//            _cache = new HashMap<>();
//        }
//
//        Log.debug(PathManager.class, "Find path from " + fromParcel + " to " + toParcel);
//
//        long time = System.currentTimeMillis();
//
//        // Check if target parcel is not surrounded by non-walkable org.smallbox.faraway.core.module.room.model
//        if (WorldHelper.isSurroundedByBlocked(toParcel)) {
//            Log.debug(PathManager.class, "Path resolved in " + (System.currentTimeMillis() - time) + "ms (surrounded)");
//            return null;
//        }
//
//        // Find path to target parcel
//        try {
//            SmoothableGraphPath<ParcelModel, Vector2> nodes = new MyGraphPath();
////            GraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
//            if (_finder.searchNodePath(fromParcel, toParcel, _heuristic, nodes)) {
//                Log.debug(PathManager.class, "Path resolved in " + (System.currentTimeMillis() - time) + "ms (success)");
//
////                new PathSmoother(new RaycastCollisionDetector() {
////                    @Override
////                    public boolean collides(Ray ray) {
////                        return false;
////                    }
////
////                    @Override
////                    public boolean findCollision(Collision outputCollision, Ray inputRay) {
////                        return false;
////                    }
////                }).smoothPath(nodes);
//
//                return nodes;
//            }
//        } catch (Exception e) {
//            Log.warning(PathManager.class, "Error during path resolve");
//        }
//
//        // No path found
//        Log.debug(PathManager.class, "Path resolved in " + (System.currentTimeMillis() - time) + "ms (fail)");
//        return null;
    }

    private long getSum(ParcelModel fromParcel, ParcelModel toParcel) {
        assert fromParcel.x < 256;
        assert fromParcel.y < 256;
        assert fromParcel.z < 64;
        assert toParcel.x < 256;
        assert toParcel.y < 256;
        assert toParcel.z < 64;

        long sum = 0;
        sum = (sum << 8) + fromParcel.x;
        sum = (sum << 8) + fromParcel.y;
        sum = (sum << 6) + fromParcel.z;
        sum = (sum << 8) + toParcel.x;
        sum = (sum << 8) + toParcel.y;
        sum = (sum << 6) + toParcel.z;

        return sum;
    }

    // TODO
    public int getDistance(ParcelModel p1, ParcelModel p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y) + Math.abs(p1.z - p2.z);
    }

    public void refreshConnections(ParcelModel source) {
        WorldHelper.getParcelAround(source, SurroundedPattern.X_CROSS, parcel -> parcelGraph.refreshConnections(parcel));
    }

    public boolean hasConnection(ParcelModel fromParcel, ParcelModel toParcel) {
        for (Connection<?> connection: parcelGraph.getConnections(fromParcel)) {
            if (connection.getToNode() == toParcel) {
                return true;
            }
        }
        return false;
    }

    public void initParcels() {
        parcelGraph = new ParcelGraph(worldModule.getWidth() * worldModule.getHeight() * worldModule.getFloors());
        worldModule.getAll().forEach(fromParcel ->
                WorldHelper.getParcelAround(
                        fromParcel,
                        SurroundedPattern.X_CROSS,
                        ParcelModel::isWalkable,
                        toParcel -> parcelGraph.createConnection(fromParcel, toParcel)
                )
        );
    }

}

package org.smallbox.faraway.core.game.module.path;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathManager extends GameModule {
    private static final int                    THREAD_POOL_SIZE = 1;

    private static PathManager                  _self;
    final private ArrayList<Runnable>           _runnable;
    final private ExecutorService               _threadPool;
    private IndexedAStarPathFinder<ParcelModel> _finder;
    private Heuristic<ParcelModel>              _heuristic;
    private Map<Long, PathModel>                _cache;
    private IndexedGraph                        _graph;

    public PathManager() {
        _self = this;
        _threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        _runnable = new ArrayList<>();
    }

    public void init(Collection<ParcelModel> parcels) {
        _graph = new IndexedGraph(parcels);
        _finder = new IndexedAStarPathFinder<>(_graph);
        _heuristic = (node, endNode) -> 10 * (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y));

        // Create cache
        _cache = new HashMap<>();
    }

    @Override
    protected void onGameStart(Game game) {
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _runnable.forEach(java.lang.Runnable::run);
        _runnable.clear();
    }

    public static PathManager getInstance() {
        return _self;
    }

    public void close() {
        _threadPool.shutdownNow();
    }

    public boolean hasPath(ParcelModel fromParcel, ParcelModel toParcel) {
        return getPath(fromParcel, toParcel) != null;
    }

    public boolean hasPath(ParcelModel fromParcel, ParcelModel toParcel, boolean horizontalApprox, boolean verticalApprox) {
        return getPath(fromParcel, toParcel, horizontalApprox, verticalApprox) != null;
    }

    public PathModel getPath(ParcelModel fromParcel, ParcelModel toParcel, boolean horizontalApprox, boolean verticalApprox) {
        assert fromParcel != null;
        assert toParcel != null;

        PathModel bestPath = null;

        // Exact parcel
        if (toParcel.isWalkable()) {
            PathModel path = getPath(fromParcel, toParcel);
            if (path != null) {
                bestPath = path;
            }
        }

        // Top / bottom / right / left parcel
        if (bestPath == null && horizontalApprox) {
            ParcelModel parcel = null;
            PathModel path = null;
            if ((parcel = WorldHelper.getParcel(toParcel.x - 1, toParcel.y, toParcel.z)) != null && parcel.isWalkable())
                if ((path = getPath(fromParcel, parcel)) != null)
                    bestPath = path;
            if ((parcel = WorldHelper.getParcel(toParcel.x + 1, toParcel.y, toParcel.z)) != null && parcel.isWalkable())
                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
                    bestPath = path;
            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y - 1, toParcel.z)) != null && parcel.isWalkable())
                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
                    bestPath = path;
            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y + 1, toParcel.z)) != null && parcel.isWalkable())
                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
                    bestPath = path;
        }

        // Upper / lower parcel
        if (bestPath == null && verticalApprox) {
            ParcelModel parcel = null;
            PathModel path = null;
            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y, toParcel.z - 1)) != null && parcel.isWalkable())
                if ((path = getPath(fromParcel, parcel)) != null)
                    bestPath = path;
            if ((parcel = WorldHelper.getParcel(toParcel.x, toParcel.y, toParcel.z + 1)) != null && parcel.isWalkable())
                if ((path = getPath(fromParcel, parcel)) != null && (bestPath == null || path.getLength() < bestPath.getLength()))
                    bestPath = path;
        }

        return bestPath;
    }

    private PathModel getPath(ParcelModel fromParcel, ParcelModel toParcel) {
        if (fromParcel == null) {
            Log.error("fromParcel is null");
        }

        if (toParcel == null) {
            Log.error("toParcel is null");
        }

        printDebug("GetPath (from: " + fromParcel.x + "x" + fromParcel.y + " to: " + toParcel.x + "x" + toParcel.y + ")");

        // Non walkable origin / target parcel
        if (!fromParcel.isWalkable() || !toParcel.isWalkable()) {
            return null;
        }

        // Empty path
        if (fromParcel == toParcel) {
            DefaultGraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
            nodes.add(toParcel);
            return PathModel.create(nodes);
        }

        // Get path from cache
        long cacheId = getSum(fromParcel, toParcel);
        PathModel path = _cache.get(cacheId);
//        if (path != null && path.isValid()) {
//            return path;
//        }

        // Looking for new path
        path = PathModel.create(findPath(fromParcel, toParcel));
        _cache.put(cacheId, path);

        return path;
    }

    public GraphPath<ParcelModel> findPath(ParcelModel fromParcel, ParcelModel toParcel) {
        assert fromParcel != null;
        assert toParcel != null;

        printDebug("Find path from " + fromParcel + " to " + toParcel);

        long time = System.currentTimeMillis();

        // Check if target parcel is not surrounded by non-walkable org.smallbox.faraway.core.game.module.room.model
        if (WorldHelper.isSurroundedByBlocked(toParcel)) {
            printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (surrounded)");
            return null;
        }

        // Find path to target parcel
        try {
            GraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
            if (_finder.searchNodePath(fromParcel, toParcel, _heuristic, nodes)) {
                printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (success)");
                return nodes;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // No path found
        printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (fail)");
        return null;
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
//    @Override
//    public void onStructureComplete(StructureModel structure) { _graph.resetAround(structure.getParcel()); }

    public void resetAround(ParcelModel parcel) { _graph.resetAround(parcel); }

    @Override
    public void onRemoveRock(ParcelModel parcel) { _graph.resetAround(parcel); }

    @Override
    public void onChangeGround(ParcelModel parcel) { _graph.resetAround(parcel); }
}

package org.smallbox.faraway.game.module.path;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.area.AreaModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.util.MoveListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathManager extends GameModule {

    private IndexedAStarPathFinder<ParcelModel> _finder;
    private Heuristic<ParcelModel>              _heuristic;
    private Map<ParcelModel, ParcelPathCache>   _cache;

    @Override
    protected void onLoaded() {

    }

    @Override
    protected void onUpdate(int tick) {
        _runnable.forEach(java.lang.Runnable::run);
        _runnable.clear();
    }

    private static final int 			THREAD_POOL_SIZE = 1;

    private static PathManager _self;
    final private ArrayList<Runnable>   _runnable;
    final private ExecutorService 		_threadPool;

    public PathManager() {
        _self = this;
        _threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        _runnable = new ArrayList<>();
    }

    public void init(int width, int height) {
        if (width == 0 || height == 0) {
            throw new RuntimeException("PathManager init with 0 width/height");
        }
        _finder = new IndexedAStarPathFinder<>(ModuleHelper.getWorldModule());
        _heuristic = (node, endNode) -> 10 * (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y));

        // Create cache
        _cache = new HashMap<>();
        for (ParcelModel parcel: ModuleHelper.getWorldModule().getParcelList()) {
            _cache.put(parcel, new ParcelPathCache(parcel));
        }
    }

    public void getPathAsync(final MoveListener listener, final MovableModel movable, final BaseJobModel job, final int x, final int y) {
//		_threadPool.execute(() -> {
        ParcelModel fromParcel = ModuleHelper.getWorldModule().getParcel(movable.getX(), movable.getY());
        ParcelModel toParcel = ModuleHelper.getWorldModule().getParcel(x, y);

        if (WorldHelper.isBlocked(x + 1, y) &&
                WorldHelper.isBlocked(x - 1, y) &&
                WorldHelper.isBlocked(x, y + 1) &&
                WorldHelper.isBlocked(x, y - 1)) {
            printInfo("characters: path fail (surrounded by solid parcel)");
            movable.onPathFailed(job, fromParcel, toParcel);
            if (listener != null) {
                listener.onFail(movable);
            }
            return;
        }

        printDebug("getPathAsync");
        GraphPath<ParcelModel> path = findPath(fromParcel, toParcel);
        if (path != null) {
            printInfo("characters: path success (" + fromParcel.x + "x" + fromParcel.y + " to " + toParcel.x + "x" + toParcel.y + "), job: " + job);
            synchronized (_runnable) {
                _runnable.add(() -> {
                    movable.onPathComplete(path, job, fromParcel, toParcel);
                    if (listener != null) {
                        listener.onSuccess(movable);
                    }
                });
            }
        } else {
            printInfo("characters: path fail");
            synchronized (_runnable) {
                _runnable.add(() -> {
                    movable.onPathFailed(job, fromParcel, toParcel);
                    if (listener != null) {
                        listener.onFail(movable);
                    }
                });
            }
        }
//        });
    }

    public static PathManager getInstance() {
        return _self;
    }

    public void close() {
        _threadPool.shutdownNow();
    }

    public boolean hasPath(ParcelModel fromParcel, ParcelModel toParcel) {
        PathCacheModel pathCache = _cache.get(fromParcel).getPath(toParcel);
        if (pathCache != null && pathCache.isValid()) {
            return true;
        }
        return findPath(fromParcel, toParcel) != null;
    }

    public GraphPath<ParcelModel> getPath(AreaModel fromArea, ParcelModel toParcel) {
        for (ParcelModel parcel: fromArea.getParcels()) {
            return getPath(parcel, toParcel);
        }
        return null;
    }

    public GraphPath<ParcelModel> getPath(ParcelModel fromParcel, ParcelModel toParcel) {
        printDebug("GetPath (from: " + fromParcel.x + "x" + fromParcel.y + " to: " + toParcel.x + "x" + toParcel.y + ")");

        PathCacheModel pathCache = _cache.get(fromParcel).getPath(toParcel);
        if (pathCache != null && pathCache.isValid()) {
            return pathCache.getPath();
        }

        return findPath(fromParcel, toParcel);
//
//        // Return path cache
//        {
//            PathCacheModel cache = _cache.get(fromParcel).getPath(toParcel);
//            if (cache != null && cache.isValid()) {
//                return cache.getPath();
//            }
//        }
//
//        // Find path
////        _threadPool.execute(() -> {
//            if (ModuleHelper.getWorldModule().isSurroundedByBlocked(toParcel.x, toParcel.y)) {
//                printInfo("characters: path fail (surrounded by solid parcel)");
//                return null;
//            }
//
//            Log.debug("getPath path");
//            GraphPath<ParcelModel> path = findPath(fromParcel, toParcel);
//            if (path != null) {
//                printInfo("characters: path success (" + fromParcel.x + "x" + fromParcel.y + " to " + toParcel.x + "x" + toParcel.y + ")");
//                synchronized (_runnable) {
//                    _runnable.add(() -> {
//                        PathCacheModel cache = new PathCacheModel(fromParcel, toParcel, path);
//                        ParcelPathCache ppc = _cache.get(fromParcel);
//                        path.forEach(parcel -> ppc.addPathBy(cache));
//                        ppc.addPath(toParcel, cache);
//                    });
//                }
//                return path;
//            } else {
//                printInfo("cache path fail");
//                synchronized (_runnable) {
//                    _runnable.add(() -> {
//                        _cache.get(fromParcel).addPath(toParcel, null);
//                    });
//                }
//            }
////        });
//
//        return null;
    }

    public GraphPath<ParcelModel> findPath(ParcelModel fromParcel, ParcelModel toParcel) {
        long time = System.currentTimeMillis();

        // Check if target parcel is not surrounded by non-walkable area
        if (WorldHelper.isSurroundedByBlocked(toParcel)) {
            _cache.get(fromParcel).addPath(toParcel, null);
            printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (surrounded)");
            return null;
        }

        // Find path to target parcel
        GraphPath<ParcelModel> path = new DefaultGraphPath<>();
        if (_finder.searchNodePath(fromParcel, toParcel, _heuristic, path)) {
            _cache.get(fromParcel).addPath(toParcel, path);
            printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (success)");
            return path;
        }

        // No path found
        printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (fail)");
        return null;
    }

    @Override
    public void onAddStructure(StructureModel structure) {
        if (_cache != null && structure != null) {
            _cache.get(structure.getParcel()).getPathsBy().forEach(PathCacheModel::invalidate);
        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        if (_cache != null && structure != null) {
            _cache.get(structure.getParcel()).getPathsBy().forEach(PathCacheModel::invalidate);
        }
    }

    public GraphPath<ParcelModel> getBestApprox(ParcelModel fromParcel, ParcelModel toParcel) {
        GraphPath<ParcelModel> bestPath = null;
        if (toParcel != null) {
            if (toParcel.isWalkable()) {
                bestPath = getPath(fromParcel, toParcel);
            } else {
                for (int x = toParcel.x -1; x <= toParcel.x + 1; x++) {
                    for (int y = toParcel.y -1; y <= toParcel.y + 1; y++) {
                        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                        if (parcel != null && parcel.isWalkable()) {
                            GraphPath<ParcelModel> path = getPath(fromParcel, parcel);
                            if (path != null && (bestPath == null || path.getCount() < bestPath.getCount())) {
                                bestPath = path;
                            }
                        }
                    }
                }
            }
        }
        return bestPath;
    }
}

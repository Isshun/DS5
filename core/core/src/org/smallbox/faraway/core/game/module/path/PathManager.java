package org.smallbox.faraway.core.game.module.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.utils.Array;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.area.model.AreaModel;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;

import java.util.ArrayList;
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
    private Map<ParcelModel, ParcelPathCache>   _cache;
    private IndexedGraph                        _graph;

    public PathManager() {
        _self = this;
        _threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        _runnable = new ArrayList<>();
    }

    @Override
    protected void onLoaded(Game game) {
        _graph = new IndexedGraph(ModuleHelper.getWorldModule().getParcels(), game.getInfo());
        _finder = new IndexedAStarPathFinder<>(_graph);
        _heuristic = (node, endNode) -> 10 * (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y));

        // Create cache
        _cache = new HashMap<>();
        for (ParcelModel parcel: ModuleHelper.getWorldModule().getParcelList()) {
            _cache.put(parcel, new ParcelPathCache(parcel));
        }
    }

    @Override
    protected void onUpdate(int tick) {
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
            for (int x = toParcel.x - 1; x <= toParcel.x + 1; x++) {
                for (int y = toParcel.y - 1; y <= toParcel.y + 1; y++) {
                    if (x != toParcel.x || y != toParcel.y) {
                        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y, toParcel.z);
                        if (parcel != null && parcel.isWalkable()) {
                            PathModel path = getPath(fromParcel, parcel);
                            if (path != null && (bestPath == null || path.getLength() < bestPath.getLength())) {
                                bestPath = path;
                            }
                        }
                    }
                }
            }
        }

        // Upper / lower parcel
        if (bestPath == null && verticalApprox) {
            for (int z = toParcel.z - 1; z <= toParcel.z + 1; z++) {
                if (z != toParcel.z) {
                    ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(toParcel.x, toParcel.y, z);
                    if (parcel != null && parcel.isWalkable()) {
                        PathModel path = getPath(fromParcel, parcel);
                        if (path != null && (bestPath == null || path.getLength() < bestPath.getLength())) {
                            bestPath = path;
                        }
                    }
                }
            }
        }

        return bestPath;
    }

    public PathModel getPath(ParcelModel fromParcel, ParcelModel toParcel) {
        printDebug("GetPath (from: " + fromParcel.x + "x" + fromParcel.y + " to: " + toParcel.x + "x" + toParcel.y + ")");

        if (fromParcel == toParcel) {
            DefaultGraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
            nodes.add(toParcel);
            return PathModel.create(nodes);
        }

        ParcelPathCache parcelPathCache = _cache.get(fromParcel);
        if (parcelPathCache == null) {
            parcelPathCache = _cache.put(fromParcel, new ParcelPathCache(fromParcel));
        }

        PathCacheModel pathCache = parcelPathCache.getPath(toParcel);
        if (pathCache != null && pathCache.isValid()) {
            return PathModel.create(pathCache.getPath());
        }

        return PathModel.create(findPath(fromParcel, toParcel));
    }

    public GraphPath<ParcelModel> findPath(ParcelModel fromParcel, ParcelModel toParcel) {
        long time = System.currentTimeMillis();

        // Check if target parcel is not surrounded by non-walkable model
        if (WorldHelper.isSurroundedByBlocked(toParcel)) {
            _cache.get(fromParcel).addPath(toParcel, null);
            printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (surrounded)");
            return null;
        }

        // Find path to target parcel
        GraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
        if (_finder.searchNodePath(fromParcel, toParcel, _heuristic, nodes)) {
            _cache.get(fromParcel).addPath(toParcel, nodes);
            printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (success)");
            return nodes;
        }

        // No path found
        printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (fail)");
        return null;
    }

    // TODO: return cross positions in priority
    public PathModel getBestAround(ParcelModel fromParcel, ParcelModel toParcel) {
        PathModel bestPath = null;
        for (int x = toParcel.x -1; x <= toParcel.x + 1; x++) {
            for (int y = toParcel.y -1; y <= toParcel.y + 1; y++) {
                if (x != toParcel.x || y != toParcel.y) {
                    ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y);
                    if (parcel != null && parcel.isWalkable()) {
                        PathModel path = getPath(fromParcel, parcel);
                        if (path != null && (bestPath == null || path.getLength() < bestPath.getLength())) {
                            bestPath = path;
                        }
                    }
                }
            }
        }
        return bestPath;
    }

    @Override
    public void onStructureComplete(StructureModel structure) { _graph.resetAround(structure.getParcel()); }

    @Override
    public void onAddStructure(StructureModel structure) { _graph.resetAround(structure.getParcel()); }

    @Override
    public void onAddResource(ResourceModel resource) { _graph.resetAround(resource.getParcel()); }

    @Override
    public void onRemoveStructure(StructureModel structure) { _graph.resetAround(structure.getParcel()); }

    @Override
    public void onRemoveResource(ResourceModel resource) { _graph.resetAround(resource.getParcel()); }
}

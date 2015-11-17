package org.smallbox.faraway.core.game.module.path;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.PlantModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;

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
    private Map<Long, PathModel>                _cache;
    private IndexedGraph                        _graph;

    public PathManager() {
        _self = this;
        _threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        _runnable = new ArrayList<>();
    }

    public void init(GameInfo gameInfo) {
        _graph = new IndexedGraph(ModuleHelper.getWorldModule().getParcels(), gameInfo);
        _finder = new IndexedAStarPathFinder<>(_graph);
        _heuristic = (node, endNode) -> 10 * (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y));

        // Create cache
        _cache = new HashMap<>();
    }

    @Override
    protected void onLoaded(Game game) {
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

    private PathModel getPath(ParcelModel fromParcel, ParcelModel toParcel) {
        printDebug("GetPath (from: " + fromParcel.x + "x" + fromParcel.y + " to: " + toParcel.x + "x" + toParcel.y + ")");

        // Non walkable target parcel
        if (!toParcel.isWalkable()) {
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

        long time = System.currentTimeMillis();

        // Check if target parcel is not surrounded by non-walkable model
        if (WorldHelper.isSurroundedByBlocked(toParcel)) {
            printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (surrounded)");
            return null;
        }

        // Find path to target parcel
        GraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
        if (_finder.searchNodePath(fromParcel, toParcel, _heuristic, nodes)) {
            printDebug("Path resolved in " + (System.currentTimeMillis() - time) + "ms (success)");
            return nodes;
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

    @Override
    public void onStructureComplete(StructureModel structure) { _graph.resetAround(structure.getParcel()); }

    @Override
    public void onAddStructure(StructureModel structure) { _graph.resetAround(structure.getParcel()); }

    @Override
    public void onAddPlant(PlantModel resource) { _graph.resetAround(resource.getParcel()); }

    @Override
    public void onRemoveStructure(StructureModel structure) { _graph.resetAround(structure.getParcel()); }

    @Override
    public void onRemovePlant(PlantModel plant) { _graph.resetAround(plant.getParcel()); }

    @Override
    public void onRemoveRock(ParcelModel parcel) { _graph.resetAround(parcel); }

    @Override
    public void onChangeGround(ParcelModel parcel) { _graph.resetAround(parcel); }
}

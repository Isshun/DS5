package org.smallbox.faraway.core.game.module.path;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.utils.Array;
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
    private IndexedGraph _graph;

    public static class IndexedGraph implements com.badlogic.gdx.ai.pfa.indexed.IndexedGraph<ParcelModel> {
        private final int _width;
        private final int _height;
        private final ParcelModel[][][] _parcels;

        public IndexedGraph(ParcelModel[][][] parcels, int width, int height) {
            _width = width;
            _height = height;
            _parcels = parcels;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int f = 0; f < 1; f++) {
                        resetConnection(parcels[x][y][f]);
                    }
                }
            }
        }

        public void addParcelToConnections(Array<Connection<ParcelModel>> array, ParcelModel parcel, int x, int y) {
            ParcelModel toParcel = WorldHelper.getParcel(x, y);
            if (parcel.isWalkable() && toParcel != null && toParcel.isWalkable()) {
                array.add(new ParcelConnection(parcel, toParcel));
            }
        }

        public void resetConnection(ParcelModel parcel) {
            if (parcel != null) {
                Array<Connection<ParcelModel>> connections = new Array<>();
                addParcelToConnections(connections, parcel, parcel.x + 1, parcel.y);
                addParcelToConnections(connections, parcel, parcel.x - 1, parcel.y);
                addParcelToConnections(connections, parcel, parcel.x, parcel.y + 1);
                addParcelToConnections(connections, parcel, parcel.x, parcel.y - 1);

//        // Corners
//        addParcelToConnections(connections, parcel, parcel.x + 1, parcel.y + 1);
//        addParcelToConnections(connections, parcel, parcel.x + 1, parcel.y - 1);
//        addParcelToConnections(connections, parcel, parcel.x - 1, parcel.y + 1);
//        addParcelToConnections(connections, parcel, parcel.x - 1, parcel.y - 1);
                parcel.setConnections(connections);
            }
        }

        @Override
        public Array<Connection<ParcelModel>> getConnections(ParcelModel parcel) {
            return parcel.getConnections();
        }

        @Override
        public int getNodeCount() {
            return _width * _height;
        }

        public void resetAround(ParcelModel parcel) {
            resetConnection(WorldHelper.getParcel(parcel.x, parcel.y));
            resetConnection(WorldHelper.getParcel(parcel.x + 1, parcel.y));
            resetConnection(WorldHelper.getParcel(parcel.x - 1, parcel.y));
            resetConnection(WorldHelper.getParcel(parcel.x, parcel.y + 1));
            resetConnection(WorldHelper.getParcel(parcel.x, parcel.y - 1));
        }
    }

    private IndexedAStarPathFinder<ParcelModel> _finder;
    private Heuristic<ParcelModel>              _heuristic;
    private Map<ParcelModel, ParcelPathCache>   _cache;

    @Override
    protected void onLoaded() {
        _graph = new IndexedGraph(ModuleHelper.getWorldModule().getParcels(), ModuleHelper.getWorldModule().getWidth(), ModuleHelper.getWorldModule().getHeight());
//        _graph = new IndexedGraph(ModuleHelper.getWorldModule().getParcels(), width, height);
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

    private static final int             THREAD_POOL_SIZE = 1;

    private static PathManager _self;
    final private ArrayList<Runnable>   _runnable;
    final private ExecutorService         _threadPool;

    public PathManager() {
        _self = this;
        _threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        _runnable = new ArrayList<>();
    }

    public void init(int width, int height) {
        if (width == 0 || height == 0) {
            throw new RuntimeException("PathManager init with 0 width/height");
        }

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

    public PathModel getPath(AreaModel fromArea, ParcelModel toParcel) {
        for (ParcelModel parcel: fromArea.getParcels()) {
            return getPath(parcel, toParcel);
        }
        return null;
    }

    public PathModel getPath(ParcelModel fromParcel, ParcelModel toParcel) {
        printDebug("GetPath (from: " + fromParcel.x + "x" + fromParcel.y + " to: " + toParcel.x + "x" + toParcel.y + ")");
        fromParcel = fixFromParcel(fromParcel);

        if (fromParcel == toParcel) {
            DefaultGraphPath<ParcelModel> nodes = new DefaultGraphPath<>();
            nodes.add(toParcel);
            return PathModel.create(nodes);
        }

        PathCacheModel pathCache = _cache.get(fromParcel).getPath(toParcel);
        if (pathCache != null && pathCache.isValid()) {
            return PathModel.create(pathCache.getPath());
        }

        return PathModel.create(findPath(fromParcel, toParcel));
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
        fromParcel = fixFromParcel(fromParcel);
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

    public PathModel getBestApprox(ParcelModel fromParcel, ParcelModel toParcel) {
        fromParcel = fixFromParcel(fromParcel);
        if (toParcel != null) {
            if (toParcel.isWalkable()) {
                return getPath(fromParcel, toParcel);
            } else {
                return getBestAround(fromParcel, toParcel);
            }
        }
        return null;
    }

    private ParcelModel fixFromParcel(ParcelModel fromParcel) {
//        if (fromParcel.isWalkable()) {
//            return fromParcel;
//        }
        return fromParcel;
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

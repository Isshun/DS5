package org.smallbox.faraway;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.manager.BaseManager;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathManager extends BaseManager {

    private IndexedAStarPathFinder<ParcelModel> _finder;
    private Heuristic<ParcelModel>              _heuristic;
    private Map<ParcelModel, ParcelPathCache>   _paths;

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
        _finder = new IndexedAStarPathFinder<>(Game.getWorldManager());
        _heuristic = (node, endNode) -> 10 * (Math.abs(node.getX() - endNode.getX()) + Math.abs(node.getY() - endNode.getY()));

        // Create cache
        _paths = new HashMap<>();
        for (ParcelModel parcel: Game.getWorldManager().getParcelList()) {
            _paths.put(parcel, new ParcelPathCache());
        }
    }

	public void getPathAsync(final OnMoveListener listener, final MovableModel movable, final BaseJobModel job, final int x, final int y) {
//		_threadPool.execute(() -> {
            ParcelModel fromParcel = Game.getWorldManager().getParcel(movable.getX(), movable.getY());
            ParcelModel toParcel = Game.getWorldManager().getParcel(x, y);

            if (Game.getWorldManager().isBlocked(x+1, y) &&
                    Game.getWorldManager().isBlocked(x-1, y) &&
                    Game.getWorldManager().isBlocked(x, y+1) &&
                    Game.getWorldManager().isBlocked(x, y-1)) {
                Log.info("character: path fail (surrounded by solid parcel)");
                movable.onPathFailed(job, fromParcel, toParcel);
                if (listener != null) {
                    listener.onFail(job, movable);
                }
                return;
            }

            Log.debug("getPathAsync");
            GraphPath<ParcelModel> path = findPath(fromParcel, toParcel);
            if (path != null) {
                Log.info("character: path success (" + fromParcel.getX() + "x" + fromParcel.getY() + " to " + toParcel.getX() + "x" + toParcel.getY() + "), job: " + job);
                synchronized (_runnable) {
                    _runnable.add(() -> {
                        movable.onPathComplete(path, job, fromParcel, toParcel);
                        if (listener != null) {
                            listener.onSuccess(job, movable);
                        }
                    });
                }
            } else {
                Log.info("character: path fail");
                synchronized (_runnable) {
                    _runnable.add(() -> {
                        movable.onPathFailed(job, fromParcel, toParcel);
                        if (listener != null) {
                            listener.onFail(job, movable);
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

    public GraphPath<ParcelModel> getPath(ParcelModel fromParcel, ParcelModel toParcel) {
        Log.debug("GetPath (from: " + fromParcel.getX() + "x" + fromParcel.getY() + " to: " + toParcel.getX() + "x" + toParcel.getY() + ")");

        return findPath(fromParcel, toParcel);
//
//        // Return path cache
//        {
//            PathCacheModel cache = _paths.get(fromParcel).getPath(toParcel);
//            if (cache != null && cache.isValid()) {
//                return cache.getPath();
//            }
//        }
//
//        // Find path
////        _threadPool.execute(() -> {
//            if (Game.getWorldManager().isSurroundedByBlocked(toParcel.getX(), toParcel.getY())) {
//                Log.info("character: path fail (surrounded by solid parcel)");
//                return null;
//            }
//
//            Log.debug("getPath path");
//            GraphPath<ParcelModel> path = findPath(fromParcel, toParcel);
//            if (path != null) {
//                Log.info("character: path success (" + fromParcel.getX() + "x" + fromParcel.getY() + " to " + toParcel.getX() + "x" + toParcel.getY() + ")");
//                synchronized (_runnable) {
//                    _runnable.add(() -> {
//                        PathCacheModel cache = new PathCacheModel(fromParcel, toParcel, path);
//                        ParcelPathCache ppc = _paths.get(fromParcel);
//                        path.forEach(parcel -> ppc.addPathBy(cache));
//                        ppc.addPath(toParcel, cache);
//                    });
//                }
//                return path;
//            } else {
//                Log.info("cache path fail");
//                synchronized (_runnable) {
//                    _runnable.add(() -> {
//                        _paths.get(fromParcel).addPath(toParcel, null);
//                    });
//                }
//            }
////        });
//
//        return null;
    }

    public GraphPath<ParcelModel> findPath(ParcelModel fromParcel, ParcelModel toParcel) {
        GraphPath<ParcelModel> path = new DefaultGraphPath<>();
        if (_finder.searchNodePath(fromParcel, toParcel, _heuristic, path)) {
            return path;
        }
        return null;
    }

    @Override
    public void onAddStructure(StructureModel structure) {
        if (_paths != null && structure != null) {
            _paths.get(structure.getParcel()).getPathsBy().forEach(PathCacheModel::invalidate);
        }
    }

    @Override
    public void onRemoveStructure(StructureModel structure) {
        if (_paths != null && structure != null) {
            _paths.get(structure.getParcel()).getPathsBy().forEach(PathCacheModel::invalidate);
        }
    }

}

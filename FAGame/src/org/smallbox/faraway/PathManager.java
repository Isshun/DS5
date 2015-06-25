package org.smallbox.faraway;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.manager.BaseManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathManager extends BaseManager {

    private IndexedAStarPathFinder<ParcelModel> _finder;
    private Heuristic<ParcelModel>              _heuristic;

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
    }

	public void getPathAsync(final OnMoveListener listener, final CharacterModel character, final BaseJobModel job, final int x, final int y) {
//		_threadPool.execute(() -> {
            ParcelModel fromParcel = Game.getWorldManager().getParcel(character.getX(), character.getY());
            ParcelModel toParcel = Game.getWorldManager().getParcel(x, y);

            Log.debug("getPathAsync");
            GraphPath<ParcelModel> path = getPath(fromParcel, toParcel);
            if (path != null) {
                Log.info("character: path success (" + fromParcel.getX() + "x" + fromParcel.getY() + " to " + toParcel.getX() + "x" + toParcel.getY() + "), job: " + job);
                synchronized (_runnable) {
                    _runnable.add(() -> {
                        character.onPathComplete(path, job, fromParcel, toParcel);
                        if (listener != null) {
                            listener.onSuccess(job, character);
                        }
                    });
                }
            } else {
                Log.info("character: path fail");
                synchronized (_runnable) {
                    _runnable.add(() -> {
                        character.onPathFailed(job, fromParcel, toParcel);
                        if (listener != null) {
                            listener.onFail(job, character);
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

        GraphPath<ParcelModel> path = new DefaultGraphPath<>();
        if (_finder.searchNodePath(fromParcel, toParcel, _heuristic, path)) {
            return path;
        }

        return null;
    }
}

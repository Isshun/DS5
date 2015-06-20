package org.smallbox.faraway;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import org.smallbox.faraway.game.OnMoveListener;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.BaseManager;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.job.JobModel;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathHelper extends BaseManager {

    private IndexedAStarPathFinder<ParcelModel> _finder;

    @Override
	protected void onUpdate(int tick) {
        _runnable.forEach(java.lang.Runnable::run);
        _runnable.clear();
	}

	private static final int 			THREAD_POOL_SIZE = 1;

	private static PathHelper _self;
	final private ArrayList<Runnable>   _runnable;
	private ExecutorService 			_threadPool;

	public PathHelper() {
		_self = this;
		_threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		_runnable = new ArrayList<>();
	}
	
	public void init(int width, int height) {
		if (width == 0 || height == 0) {
			throw new RuntimeException("PathManager init with 0 width/height");
		}
        _finder = new IndexedAStarPathFinder<>(Game.getWorldManager());
	}

	public void getPathAsync(final OnMoveListener listener, final CharacterModel character, final JobModel job, final int x, final int y) {
		_threadPool.execute(() -> {
            Log.debug("getPathAsync");

            GraphPath<ParcelModel> path = new DefaultGraphPath<>();

            if (_finder.searchNodePath(
                    Game.getWorldManager().getParcel(character.getX(), character.getY()),
                    Game.getWorldManager().getParcel(x, y),
                    (node, endNode) -> 10 * (Math.abs(node.getX() - endNode.getX()) + Math.abs(node.getY() - endNode.getY())),
                    path)) {

                Log.debug("character: path find (" + character.getX() + "x" + character.getY() + " to " + x + "x" + y + ")");
                synchronized (_runnable) {
                    _runnable.add(() -> {
                        character.onPathComplete(path, job);
                        if (listener != null) {
                            listener.onReach(job, character);
                        }
                    });
                }
            } else {
                Log.info("character: path fail");
                synchronized (_runnable) {
                    _runnable.add(() -> {
                        character.onPathFailed(job);
                        if (listener != null) {
                            listener.onFail(job, character);
                        }
                    });
                }
            }
        });
	}
	
	public static PathHelper getInstance() {
		return _self;
	}

	public void close() {
		_threadPool.shutdownNow();		
	}
}

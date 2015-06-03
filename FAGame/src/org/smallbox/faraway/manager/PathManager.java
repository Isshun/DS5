package org.smallbox.faraway.manager;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.AStarPathFinder.Node;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Step;
import org.newdawn.slick.util.pathfinding.heuristics.ManhattanHeuristic;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.Movable;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.job.BaseJob;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PathManager {

	public static class MyMover implements Mover {
		public final Movable	movable;
		public final int 		targetX;
		public final int 		targetY;

		public MyMover(Movable movable, int targetX, int targetY) {
			this.movable = movable;
			this.targetX = targetX;
			this.targetY = targetY;
		}
	}
	
	public interface PathManagerCallback {
		  void	onPathComplete(Path path, BaseJob item);
		  void	onPathFailed(BaseJob item);
	}

	public static class FinderPool {
		private static List<AStarPathFinder> 	_finderPool;
		private static Step[][] 				_steps;

		public static void init() {
			_steps = new Step[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
			_finderPool = new ArrayList<>();
			for (int i = 0; i < THREAD_POOL_SIZE; i++) {
				Node[][] nodes = new Node[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
				_finderPool.add(new AStarPathFinder(ServiceManager.getWorldMap(), 500, true, nodes, _steps, new ManhattanHeuristic(1)));
			}
		}

		public static AStarPathFinder getFinder() {
			synchronized(_finderPool) {
				if (_finderPool.size() > 0) {
					return _finderPool.remove(0);
				}
			}
			return null;
		}

		public static void recycle(AStarPathFinder finder) {
			synchronized(_finderPool) {
				_finderPool.add(finder);
			}
		}
	}

	private static final int 			THREAD_POOL_SIZE = 4;
	protected static final int 			REGION_SIZE = 10;

	private static PathManager 			sSelf;
	final private ArrayList<Runnable> 	_paths;
	private ExecutorService 			_threadPool;

	public PathManager() {
		_threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		_paths = new ArrayList<>();
		FinderPool.init();
	}
	
	public void init() {
	}

	public void getPathAsync(final CharacterModel character, final BaseJob job) {
		final int fromX = character.getX();
		final int fromY = character.getY();
		final int toX = job.getX();
		final int toY = job.getY();

		_threadPool.execute(() -> {
            Log.debug("getPathAsync");

            AStarPathFinder finder = FinderPool.getFinder();
            if (finder == null) {
                character.onPathFailed(job);
                throw new RuntimeException("no more AStarPathFinder in FinderPool");
            }

            MyMover mover = new MyMover(character, job.getX(), job.getY());
            final Path rawpath = finder.findPath(mover, fromX, fromY, toX, toY);
            FinderPool.recycle(finder);

            if (rawpath != null) {
                Log.debug("character: path complete (" + fromX + "x" + fromY + " to " + toX + "x" + toY + ")");

                synchronized(_paths) {
                    _paths.add(() -> character.onPathComplete(rawpath, job));
                }

            } else {
                Log.info("character: path fail");

                // TODO
                job.setBlocked(MainRenderer.getFrame());
                character.onPathFailed(job);
            }
        });
	}
	
	public static PathManager getInstance() {
		if (sSelf == null) {
			sSelf = new PathManager();
		}
		return sSelf;
	}

	public void close() {
		_threadPool.shutdownNow();		
	}

	public List<Runnable> getPaths() {
		return _paths;
	}
}

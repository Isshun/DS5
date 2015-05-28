package org.smallbox.faraway.manager;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.AStarPathFinder.Node;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Step;
import org.newdawn.slick.util.pathfinding.heuristics.ManhattanHeuristic;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.Movable;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.job.Job;
import org.smallbox.faraway.renderer.MainRenderer;

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
		  void	onPathComplete(Path path, Job item);
		  void	onPathFailed(Job item);
	}

	public static class FinderPool {
		private static List<AStarPathFinder> 	_finderPool;
		private static Step[][] 				_steps;

		public static void init() {
			_steps = new Step[Constant.WORLD_WIDTH][Constant.WORLD_HEIGHT];
			_finderPool = new ArrayList<AStarPathFinder>();
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
//	private Map<Integer, Boolean>		_bridges;
//	public List<Door>					_doors;
//	protected Region[][] 				_regions;

	public PathManager() {
		_threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//		_bridges = new HashMap<Integer, Boolean>();
//		_doors = new ArrayList<Door>();
		_paths = new ArrayList<Runnable>();
		FinderPool.init();
	}
	
	public void init() {
//		int columns = Constant.WORLD_WIDTH / REGION_SIZE;
//		int lines = Constant.WORLD_HEIGHT / REGION_SIZE;

//		_regions = new Region[lines][columns];
//		for (int j = 0; j < lines; j++) {
//			for (int i = 0; i < columns; i++) {
//				_regions[j][i] = new Region(i + (j * columns),
//						i * REGION_SIZE,
//						j * REGION_SIZE,
//						(i + 1) * REGION_SIZE,
//						(j + 1) * REGION_SIZE);
//				_doors.addAll(_regions[j][i]._doors);
//			}
//		}
//		
//		for (int j = 0; j < lines; j++) {
//			for (int i = 0; i < columns; i++) {
//				if (j < lines-1) { getBridges(_regions[j][i], _regions[j+1][i]); }
//				if (j > 0) { getBridges(_regions[j][i], _regions[j-1][i]); }
//				if (i < columns-1) { getBridges(_regions[j][i], _regions[j][i+1]); }
//				if (i > 0) { getBridges(_regions[j][i], _regions[j][i-1]); }
//			}
//		}

//		for (Door d1: _doors) {
//			for (Door d2: _doors) {
//				if (_bridges.get(d1.id << 16 + d2.id) != null && _bridges.get(d1.id << 16 + d2.id)) { 
//					Log.info(d1.id + "x" + d2.id + " -> " + _bridges.get(d1.id << 16 + d2.id));
//				}
//			}
//		}
		
//		List<Door> visited = new ArrayList<Door>();
//		dumpLinks(visited, _doors.get(0), 0);
//		
//		visited.clear();
//		getPath(visited, _doors.get(0), _doors.get(10));
	}

	//		void	addObject(int x, int y, boolean walkable) {
	//		  _pathfinder.addObject(x, y, true);
	//		}

//
//	private int getPath(List<Door> visited, Door d1, Door d2) {
//		visited.add(d1);
//		
//		for (Door d: d1.doors) {
//			if (d == d2) {
//				Log.info("in path: " + d.id);
//				return 1;
//			} else if (!visited.contains(d)) {
//				int ret = getPath(visited, d, d2);
//				if (ret == 1) {
//					Log.info("in path: " + d.id);
//				}
//				return ret;
//			}
//		}
//		return 0;
//	}
//
//	private void dumpLinks(List<Door> visited, Door d, int level) {
//		
//		visited.add(d);
//		
////		for (int i = 0; i < level - 1; i++) {
////			System.out.print("-");
////		}
//		System.out.print(String.valueOf(d.id));
//		System.out.print("\n");
//		
//		for (Door d2: d.doors) {
//			System.out.print("-");
//			System.out.print(String.valueOf(d2.id));
//			System.out.print("\n");
//		}
//		
//		System.out.print("\n\n");
//		for (Door d2: d.doors) {
//			if (!visited.contains(d2)) {
//				dumpLinks(visited, d2, level + 1);
//			}
//		}
//
//	}
//
//	private void getBridges(Region r1, Region r2) {
//		Log.info("get bridges between #" + r1.id + " and #" + r2.id);
//		
//		for (Door d1: r1._doors) {
//			for (Door d2: r2._doors) {
//				if (d1.x == d2.x && d1.y == d2.y) {
//					Log.info(d1.id + "x" + d2.id);
//					d1.addBridge(d2);
//					d2.addBridge(d1);
//					_bridges.put(d1.id << 16 + d2.id, true);
//				}
//			}
//		}
//	}
//
//	private long	getSum(int fromX, int fromY, int toX, int toY) {
//		long sum = fromX;
//		sum = sum << 16;
//		sum += fromY;
//		sum = sum << 16;
//		sum += toX;
//		sum = sum << 16;
//		sum += toY;
//		return sum;
//	}

	//		private Vector<Position>		getPath(MapSearchNode nodeStart, MapSearchNode nodeEnd) {
	//
	//
	//		  AStarSearch<MapSearchNode>* astarsearch = new AStarSearch<MapSearchNode>();
	//
	//		  std.vector<Position*> path = _pathfinder.getPath((float)nodeStart.x, (float)nodeStart.y, (float)nodeEnd.x, (float)nodeEnd.y, 100.0f);
	//
	//		  return path;
	//		  // for(vector<Position*>.size_type i = 0; i < path.size(); i++) {
	//		  // 	astarsearch
	//		  // } 
	//
	//		  
	//
	//
	//		  // // std.multimap<int,int>.iterator it;
	//		  // // it = _mapfind(std.pair<int, int>(nodeStart.x + nodeStart.y * WORLD_MAX_SIZE,
	//
	//		  // // if (it != multimap.end) {
	//		  // // 	Error() << "PathManager: this path is already know and cannot be resolve";
	//		  // // }
	//
	//		  // AStarSearch<MapSearchNode>* astarsearch = new AStarSearch<MapSearchNode>();
	//		  // unsigned int SearchCount = 0;
	//		  // const unsigned int NumSearches = 1;
	//
	//		  // while(SearchCount < NumSearches) {
	//
	//		  // 	 // Set Start and goal states
	//		  // 	 astarsearchSetStartAndGoalStates( nodeStart, nodeEnd );
	//
	//		  // 	 unsigned int SearchState;
	//		  // 	 unsigned int SearchSteps = 0;
	//
	//		  // 	 Debug() << "PathManager: searching...";
	//		  // 	 do {
	//		  // 	   SearchState = astarsearchSearchStep();
	//		  // 	   SearchSteps++;
	//		  // 	   ServiceManager.getWorldMap()dump();
	//		  // 	   // if (SearchSteps > 10000 && SearchSteps % 100 == 0)
	//		  // 	   // 	 Debug() << "PathManager: step " << SearchSteps;
	//		  // 	 }
	//		  // 	 while( SearchState == AStarSearch<MapSearchNode>.SEARCH_STATE_SEARCHING );
	//		  // 	 Debug() << "PathManager: search complete";
	//
	//		  // 	 // Path found
	//		  // 	 if( SearchState == AStarSearch<MapSearchNode>.SEARCH_STATE_SUCCEEDED ) {
	//		  // 	   Debug() << "Search found goal state: " << SearchSteps;
	//		  // 	   return astarsearch;
	//		  // 	 }
	//
	//		  // 	 // No path found
	//		  // 	 else if( SearchState == AStarSearch<MapSearchNode>.SEARCH_STATE_FAILED ) {
	//		  // 	   Debug() << "Search terminated. Did not find goal state";
	//		  // 	   astarsearchEnsureMemoryFreed();
	//		  // 	   delete astarsearch;
	//		  // 	   // _mapinsert(std.pair<int, int>(nodeStart.x + nodeStart.y * WORLD_MAX_SIZE,
	//		  // 	   // 									nodeEnd.x + nodeEnd.y * WORLD_MAX_SIZE));
	//		  // 	 }
	//
	//		  // 	 SearchCount++;
	//		  //  }
	//
	//		  // return NULL;
	//		}

//	private Vector<Position>		getPath(int fromX, int fromY, int toX, int toY) {
//
//		//		  MapSearchNode nodeStart;
//		//		  nodeStart.x = fromX;
//		//		  nodeStart.y = fromY;
//		//
//		//		  MapSearchNode nodeEnd;
//		//		  nodeEnd.x = toX;
//		//		  nodeEnd.y = toY;
//		//
//		//		  Vector<Position*> path = getPath(nodeStart, nodeEnd);
//
//		return null;
//	}
//
//	public List<Region>				getRegions() { return _regions; }
//	
//	private Vector<Position>		getPath(Character character, Job item) {
//
//		// if (_map[charactergetId()][itemgetId()]) {
//		// 	Error() << "PathManager: this path is already know and cannot be resolve";
//		// 	return NULL;
//		// }
//
//		//		  MapSearchNode nodeStart;
//		//		  nodeStart.x = charactergetX();
//		//		  nodeStart.y = charactergetY();
//		//
//		//		  MapSearchNode nodeEnd;
//		//		  nodeEnd.x = itemgetX();
//		//		  nodeEnd.y = itemgetY();
//		//
//		//		  vector<Position*> path = getPath(nodeStart, nodeEnd);
//
//		// if (path == NULL) {
//		// 	_map[charactergetId()][itemgetId()] = 1;
//		// } else {
//		// 	_map[charactergetId()][itemgetId()] = 0;
//		// }
//
//		return null;
//	}

	// void	getPathAsync(Character* character, int x, int y) {

	//   _poolenqueue([this, character, x, y] {

	//		 	  MapSearchNode nodeStart;
	//		 	  nodeStart.x = charactergetX();
	//		 	  nodeStart.y = charactergetY();

	//		 	  MapSearchNode nodeEnd;
	//		 	  nodeEnd.x = x;
	//		 	  nodeEnd.y = y;

	//		 	  AStarSearch<MapSearchNode>* path = getPath(nodeStart, nodeEnd);

	//		 	  if (path != NULL) {
	//		 		characteronPathComplete(path, NULL);
	//		 	  } else {
	//		 		characteronPathFailed(NULL);
	//		 	  }

	//		 	});
	// }

	public void getPathAsync(final CharacterModel character, final Job job) {
		final int fromX = character.getX();
		final int fromY = character.getY();
		final int toX = job.getX();
		final int toY = job.getY();

		_threadPool.execute(new Runnable() {
			@Override
			public void run() {
//				int regionByLine = (Constant.WORLD_WIDTH / REGION_SIZE);
				
				//Region r1 = _regions.get((fromX / REGION_SIZE) % regionByLine + (fromY / REGION_SIZE) * regionByLine);
				//Region r2 = _regions.get((toX / REGION_SIZE) % regionByLine + (fromY / REGION_SIZE) * regionByLine);
				
				//Log.info("Go from " + fromX + "x" + fromY + " to " + toX + "x" + toY + " -> region #" + r1 + " to #" + r2);
				
//				ServiceManager.getWorldMap().startDebug(fromX, fromY);
//				ServiceManager.getWorldMap().stopDebug(toX, toY);

				//Log.info("getPathAsync: " + character.getX() + ", " + character.getY() + ", " + toX + ", " + toY);
				Log.debug("getPathAsync");
//
//				// Get in cache
//				long sum1 = getSum(fromX, fromY, toX, toY);
//				if (_pool.get(sum1) != null) {
//					Log.info("character: path in cache");
//					Vector<Position> path = _pool.get(sum1).path;
//					if (path != null) {
//						character.onPathComplete(path, job);
//					} else {
//						character.onPathFailed(job);
//					}
//					return;
//				}
//
//				// Get in cache
//				long sum2 = getSum(toX, toY, fromX, fromY);
//				if (_pool.get(sum2) != null) {
//					Log.info("character: path in cache");
//					character.onPathComplete(_pool.get(sum2).path, job);
//					return;
//				}
//
				
				AStarPathFinder finder = FinderPool.getFinder();
				if (finder == null) {
					character.onPathFailed(job);
					throw new RuntimeException("no more AStarPathFinder in FinderPool");
				}
				
				MyMover mover = new MyMover(character, job.getX(), job.getY());
				final Path rawpath = finder.findPath(mover, fromX, fromY, toX, toY);
				FinderPool.recycle(finder);
				
				if (rawpath != null) {

//					// Cache
//					final Vector<Position> path = new Vector<Position>();
//					for (int i = 0; i < rawpath.getLength(); i++) {
//						path.add(new Position(rawpath.getStep(i).getX(), rawpath.getStep(i).getY()));
//					}
//					_pool.put(sum1, new OldPath(path));
//
//					// Cache
//					Vector<Position> reversedPath = new Vector<Position>();
//					for (int i = rawpath.getLength() - 1; i >= 0; i--) {
//						reversedPath.add(new Position(rawpath.getStep(i).getX(), rawpath.getStep(i).getY()));
//					}
//					_pool.put(sum2, new OldPath(reversedPath));

					Log.debug("character: path complete (" + fromX + "x" + fromY + " to " + toX + "x" + toY + ")");
					
					synchronized(_paths) {
						_paths.add(new Runnable() {
							@Override
							public void run() {
								character.onPathComplete(rawpath, job);
							}
						});
					}
					
				} else {
//					_pool.put(sum1, new OldPath(null));
//					_pool.put(sum2, new OldPath(null));

					Log.info("character: path fail");
					// TODO
					job.setBlocked(MainRenderer.getFrame());
					character.onPathFailed(job);
				}
//				NodesPool.recycle(nodes);
			}
		});
	}
	
	public static PathManager getInstance() {
		if (sSelf == null) {
			sSelf = new PathManager();
			//sSelf.init();
		}
		return sSelf;
	}

	public boolean isBlocked(int startX, int startY, int toX, int toY) {
//		long sum = getSum(startX, startY, toX, toY);
//		Log.info("blocked: " + (_pool.get(sum) != null && _pool.get(sum).blocked));
//		return _pool.get(sum) != null && _pool.get(sum).blocked;
		return false;
	}

	public void close() {
		_threadPool.shutdownNow();		
	}

	public List<Runnable> getPaths() {
		return _paths;
	}
}

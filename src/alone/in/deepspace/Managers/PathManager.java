package alone.in.deepspace.Managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;

import org.jsfml.graphics.Sprite;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFinder;

import alone.in.deepspace.Game;
import alone.in.deepspace.Character.Character;
import alone.in.deepspace.Models.Job;
import alone.in.deepspace.Models.Position;
import alone.in.deepspace.Utils.Constant;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.WorldMap;
import alone.in.deepspace.World.WorldMap.DebugPos;

public class PathManager {
	private static class OldPath {
		public boolean			blocked;
		public Vector<Position>	path;

		public OldPath(Vector<Position> path) {
			this.path = path;
			this.blocked = path == null;
		}
	}

	private static PathManager sSelf;
	private Map<Long, OldPath> _pool;

	public PathManager() {
		_pool = new HashMap<Long, OldPath>();
		//		  _data = new map<int, AStarSearch<MapSearchNode>*>();
		//		  memset(_map, 0, LIMIT_CHARACTER * LIMIT_ITEMS * sizeof(int));
	}

	//		void	addObject(int x, int y, boolean walkable) {
	//		  _pathfinder.addObject(x, y, true);
	//		}


	private long	getSum(int fromX, int fromY, int toX, int toY) {
		long sum = fromX;
		sum = sum << 16;
		sum += fromY;
		sum = sum << 16;
		sum += toX;
		sum = sum << 16;
		sum += toY;
		return sum;
	}

	private void							init() {
		Log.info("PathManager: init");

		// _storage = new list<AStarSearch<MapSearchNode>*>();


		//		  Info() << _pathfinder.getPoint(0, 0)closed;

		// exit(0);


		// int max = 10;

		// Info() << time(0);

		// for (int fromX = 0; fromX < max; fromX++) {
		// 	for (int fromY = 0; fromY < max; fromY++) {
		// 	  for (int toX = 0; toX < max; toX++) {
		// 		for (int toY = 0; toY < max; toY++) {

		// 		  // std.vector<Position*> path = _pathfinder.getPath((float)fromX, (float)fromY, (float)toX, (float)toY, 1.0f);


		// 		  // AStarSearch<MapSearchNode>* path = getPath(fromX, fromY, toX, toY);
		// 		  // _storagepush_back(path);
		// 		  // _datainsert(make_pair(getSum(fromX, fromY, toX, toY), path));

		// 		  // Info() << "size: " << _storagesize();
		// 		}
		// 	  }
		// 	}
		// }

		// Info() << time(0);

		// exit(0);

		// std.list<Job*> list;
		// int w = WorldMap.getInstance()getWidth();
		// int h = WorldMap.getInstance()getHeight();
		// for (int i = w-1; i >= 0; i--) {
		// 	for (int j = h-1; j >= 0; j--) {
		// 	  Job* item = WorldMap.getInstance()getArea(i, j);
		// 	  if (item != NULL && itemgetType() == Job.STRUCTURE_DOOR) {
		// 		Debug() << "Door at pos: " << i << " x " << j;
		// 		list.push_back(item);
		// 	  }
		// 	}
		// }

		// std.list<Job*>.iterator it1;
		// std.list<Job*>.iterator it2;
		// for (it1 = list.begin(); it1 != list.end(); ++it1) {
		// 	for (it2 = list.begin(); it2 != list.end(); ++it2) {
		// 	  if (*it1 != *it2) {
		// 		MapSearchNode nodeStart;
		// 		nodeStart.x = (*it1)getX();
		// 		nodeStart.y = (*it1)getY();

		// 		MapSearchNode nodeEnd;
		// 		nodeEnd.x = (*it2)getX();
		// 		nodeEnd.y = (*it2)getY();

		// 		AStarSearch<MapSearchNode>* path = getPath(nodeStart, nodeEnd);
		// 		pair<Job*, Job*> key = std.make_pair(*it1, *it2);

		// 		_datainsert(make_pair(make_pair(*it1, *it2), path));

		// 		Info() << "PathManager: find path"
		// 			   << " from: " << nodeStart.x << " x " << nodeStart.y
		// 			   << ", to: " << nodeEnd.x << " x " << nodeEnd.y
		// 			   << ", cost: " << pathGetSolutionCost();
		// 		  // done (" << _datasize() << " path)";
		// 	  }
		// 	}
		// }

		//		  Log.info("PathManager: init done (" + _datasize() + " path)");
	}

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
	//		  // 	   WorldMap.getInstance()dump();
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

	private Vector<Position>		getPath(int fromX, int fromY, int toX, int toY) {

		//		  MapSearchNode nodeStart;
		//		  nodeStart.x = fromX;
		//		  nodeStart.y = fromY;
		//
		//		  MapSearchNode nodeEnd;
		//		  nodeEnd.x = toX;
		//		  nodeEnd.y = toY;
		//
		//		  Vector<Position*> path = getPath(nodeStart, nodeEnd);

		return null;
	}

	private Vector<Position>		getPath(Character character, Job item) {

		// if (_map[charactergetId()][itemgetId()]) {
		// 	Error() << "PathManager: this path is already know and cannot be resolve";
		// 	return NULL;
		// }

		//		  MapSearchNode nodeStart;
		//		  nodeStart.x = charactergetX();
		//		  nodeStart.y = charactergetY();
		//
		//		  MapSearchNode nodeEnd;
		//		  nodeEnd.x = itemgetX();
		//		  nodeEnd.y = itemgetY();
		//
		//		  vector<Position*> path = getPath(nodeStart, nodeEnd);

		// if (path == NULL) {
		// 	_map[charactergetId()][itemgetId()] = 1;
		// } else {
		// 	_map[charactergetId()][itemgetId()] = 0;
		// }

		return null;
	}

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

	public void getPathAsync(final Character character, final Job job) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				WorldMap.getInstance().startDebug(character.getPosX(), character.getPosY());
				WorldMap.getInstance().stopDebug(job.getX(), job.getY());

				//Log.info("getPathAsync: " + character.getX() + ", " + character.getY() + ", " + job.getX() + ", " + job.getY());
				Log.debug("getPathAsync");

				Vector<Position> path = new Vector<Position>();

				//		  int x = character.getX();
				//		  int y = character.getY();
				//		  while (x != job.getX() || y != job.getY()) {
				//			  x += (x == job.getX() ? 0 : x > job.getX() ? -1 : 1);
				//			  y += (y == job.getY() ? 0 : y > job.getY() ? -1 : 1);
				//			  path.add(new Position(x, y));
				//		  }
				//		  
				//		  character.onPathComplete(path, job);

				long sum = getSum(character.getPosX(), character.getPosY(), job.getX(), job.getY());

				PathFinder finder = new AStarPathFinder(WorldMap.getInstance(), 500, true);
				Path rawpath = finder.findPath(new Mover() {}, character.getPosX(), character.getPosY(), job.getX(), job.getY());
				if (rawpath != null) {
					for (int i = 0; i < rawpath.getLength(); i++) {
						path.add(new Position(rawpath.getStep(i).getX(), rawpath.getStep(i).getY()));
					}
					_pool.put(sum, new OldPath(path));

					Vector<DebugPos> debugPath = WorldMap.getInstance().getDebug();
					if (debugPath != null) {
						for (DebugPos pos: debugPath) {
							if (inCompletePath(path, pos.x, pos.y)) {
								pos.inPath = true;
							}
						}
					}


					character.onPathComplete(path, job);
				} else {
					_pool.put(sum, new OldPath(null));
					job.setBlocked(Game.getFrame());
					character.onPathFailed(job);
				}
			}
		});
	}

	private boolean inCompletePath(Vector<Position> path, int x, int y) {
		for (Position position : path) {
			if (position.x == x && position.y == y) {
				return true;
			}
		}
		return false;
	}

	public static PathManager getInstance() {
		if (sSelf == null) {
			sSelf = new PathManager();
		}
		return sSelf;
	}

	public boolean isBlocked(int startX, int startY, int toX, int toY) {
		long sum = getSum(startX, startY, toX, toY);
		Log.info("blocked: " + (_pool.get(sum) != null && _pool.get(sum).blocked));
		return _pool.get(sum) != null && _pool.get(sum).blocked;
	}

	// TODO Auto-generated method stub

}

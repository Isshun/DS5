package alone.in.deepspace;
import java.util.Vector;


public class PathManager {
	private static PathManager sSelf;

	public PathManager() {
//		  _pool = new ThreadPool(4);
//		  _data = new map<int, AStarSearch<MapSearchNode>*>();
//		  memset(_map, 0, LIMIT_CHARACTER * LIMIT_ITEMS * sizeof(int));
		}

//		void	addObject(int x, int y, boolean walkable) {
//		  _pathfinder.addObject(x, y, true);
//		}


		int	getSum(int fromX, int fromY, int toX, int toY) {
		  int sum = 0;
		  sum += fromX;
		  sum = sum << 16;
		  sum += fromY;
		  sum = sum << 16;
		  sum += toX;
		  sum = sum << 16;
		  sum += toY;

		  Log.info("sum: " + sum);
		
		  return sum;

		  // int y = sum & 0xFFFF;
		  // int x = sum >> 12;

		  // Info() << "after: " << x << ", " << y;
		}

		void							init() {
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

		Vector<Position>		getPath(int fromX, int fromY, int toX, int toY) {

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

		Vector<Position>		getPath(Character character, Job item) {

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

		public void getPathAsync(Character character, Job job) {
		  Log.info("getPathAsync");

//		  _poolenqueue([this, character, job] {
//
//			  vector<Position*> path = thisgetPath(character, job);
//
//			  // Info() << "getPathAsync: " << path.size();
//			  // Position* pos = path.at(pos);
//
//			  if (path.size() > 0) {
//				characteronPathComplete(path, job);
//			  } else {
//				characteronPathFailed(job);
//			  }
//
//			});
//		}

		}

		public static PathManager getInstance() {
			if (sSelf == null) {
				sSelf = new PathManager();
			}
			return sSelf;
		}

			// TODO Auto-generated method stub
			
		}

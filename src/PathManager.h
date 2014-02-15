#ifndef _C_PATHMANAGER_
#define _C_PATHMANAGER_

#define _GLIBCXX_USE_NANOSLEEP
#include <iostream>
#include <string>
#include <thread>
#include <map>

#include "ThreadPool.h"

#include "defines.h"
#include "MapSearchNode.h"
#include "Job.h"
#include "Pathfinder.h"
#include "Position.h"

typedef vector<Position*>		Path;

class	Character;

class IPathManagerCallback {
 public:
  virtual void	onPathSearch(Path path, Job* item) = 0;
  virtual void	onPathComplete(Path path, Job* item) = 0;
  virtual void	onPathFailed(Job* item) = 0;
};

class	PathManager
{
 public:
	PathManager();
	~PathManager();
	int								getSum(int fromX, int fromY, int toX, int toY);
	void							getPathAsync(Character* character, Job* item);
	vector<Position*>		getPath(int fromX, int fromY, int toX, int toY);
	/* void							getPathAsync(Character* character, int x, int y); */
	void							init();
	void								addObject(int x, int y, bool walkable);

	static PathManager*	getInstance() { return _self; }

 private:
	vector<Position*>		getPath(MapSearchNode nodeStart, MapSearchNode nodeEnd);
	vector<Position*>		getPath(Character* character, Job* item);

	map<int, AStarSearch<MapSearchNode>*>*	_data;

	list<AStarSearch<MapSearchNode>*>*	_storage;

	/* std::multimap<int, int>*		_map; */
	int		_map[LIMIT_CHARACTER][LIMIT_ITEMS];
	ThreadPool*	_pool;
	static PathManager* _self;
	Pathfinder			_pathfinder;
};

#endif

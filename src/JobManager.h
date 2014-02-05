#ifndef _C_JOBMANAGER_
#define _C_JOBMANAGER_

#include <ctime>
#include <map>
#include <list>
#include "defines.h"
#include "WorldMap.h"
#include "Job.h"
#include "FileManager.h"

class	JobManager : public Serializable {
 public:
	JobManager();
	~JobManager();

	enum {
	  ACTION_NONE,
	  ACTION_BUILD,
	  ACTION_GATHER,
	  ACTION_USE,
	  ACTION_MOVE
	};

	virtual void		create();
	virtual void		load(const char* filePath);
	virtual void		save(const char* filePath);

	// Actions
	Job*				build(int type, int posX, int posY);
	Job*				build(BaseItem* item);
	Job*				gather(WorldArea* area);
	void				abort(Job* job);
	void				complete(Job* job);
	void				need(Character* character, int itemType);

	// Gets
	std::list<Job*>*	getJobs() { return _jobs; };
	static JobManager*	getInstance() { return _self; }
	int					getCount() { return _count; }
	Job*				getJob();

	static const char*	getActionName(int action) {
	  switch(action) {
	  case ACTION_NONE:	 return "NONE";
	  case ACTION_BUILD: return "Build";
	  case ACTION_GATHER: return "Gather";
	  case ACTION_USE:   return "Use";
	  case ACTION_MOVE:  return "Move to";
	  default: return "unknow_action";
	  }
	}

 private:
	static JobManager*	_self;
	std::list<Job*>*	_jobs;
	int					_count;
	int					_id;
	int					_start;
};

#endif

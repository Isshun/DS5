package alone.in.deepspace.Managers;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Models.BaseItem;
import alone.in.deepspace.Models.Character;
import alone.in.deepspace.Models.CharacterNeeds;
import alone.in.deepspace.Models.Job;
import alone.in.deepspace.Utils.Log;
import alone.in.deepspace.World.UserItem;
import alone.in.deepspace.World.WorldMap;
import alone.in.deepspace.World.WorldRessource;

public class JobManager {
	public enum Action {
		NONE, BUILD, GATHER, USE, MOVE
	}

	private static JobManager sSelf;

	private ArrayList<Job> _jobs;
	private int _count;
	private int _id;
	private int _start;
	private int _countFree;

	JobManager() {
	  Log.debug("JobManager");

	  _jobs = new ArrayList<Job>();
	  _count = 0;
	  _id = 0;
	  _start = 0;
	  _countFree = 0;

	  Log.debug("JobManager done");
	}

	void	create() {

	}

	void	load(final char filePath) {

	}

	void	save(final char filePath) {

	}

	Job	build(BaseItem item) {
	  if (item == null) {
		Log.error("JobManager: build on null item");
		return null;
	  }

	  Job job = new Job(++_id, item.getX(), item.getY());
	  job.setAction(JobManager.Action.BUILD);
	  job.setItemType(item.getType());
	  job.setItem(item);

	  addJob(job);

	  return job;
	}

	public Job	gather(WorldRessource ressource) {
	  if (ressource == null) {
		Log.error("JobManager: gather on null area");
		return null;
	  }

	  // return if job already exist for this item
	  for (Job job: _jobs) {
		if (job.getItem() == ressource) {
		  return null;
		}
	  }

	  Job job = new Job(++_id, ressource.getX(), ressource.getY());
	  job.setAction(Action.GATHER);
	  job.setItemType(ressource.getType());
	  job.setItem(ressource);

	  addJob(job);

	  return job;
	}

	public void	removeJob(BaseItem item) {
	  List<Job> toRemove = new ArrayList<Job>();

	  for (Job job: _jobs) {
		if (job.getItem() == item) {
		  toRemove.add(job);
		}
	  }

	  for (Job job: toRemove) {
		_jobs.remove(job);
	  }
	}

	public Job	build(BaseItem.Type type, int x, int y) {
	  BaseItem item = null;

	  // Structure
	  if (BaseItem.isStructure(type)) {
		// if (WorldMap.getInstance().getArea(x, y) == null) {
		  item = WorldMap.getInstance().putItem(type, x, y);
		// } else {
		//   Error() + "JobManager: add build on non null area";
		//   return null;
		// }
	  }

	  // Item
	  else if (BaseItem.isItem(type)) {
		if (WorldMap.getInstance().getItem(x, y) != null) {
		  Log.error("JobManager: add build on non null item");
		  return null;
		} else if (WorldMap.getInstance().getArea(x, y) == null
				   || WorldMap.getInstance().getStructure(x, y).isType(BaseItem.Type.STRUCTURE_FLOOR) == false) {
			Log.error("JobManager: add build on non invalid area (null or not STRUCTURE_FLOOR)");
		  return null;
		} else {
		  item = WorldMap.getInstance().putItem(type, x, y);
		}
	  }

	  Job job = new Job(++_id, x, y);
	  job.setAction(Action.BUILD);
	  job.setItemType(type);
	  job.setItem(item);

	  addJob(job);

	  return job;
	}

	// TODO: one pass + check profession
	public Job getJob(Character character) {
		if (_countFree == 0) {
			return null;
		}
	  
		if (character.getJob() != null) {
			return null;
		}
	  
		Log.debug("bestJob: start");

		Job bestJob = getJobForCharacterNeed(character);
		if (bestJob != null) {
			return bestJob;  
		}
	  	  
		int bestDistance = -1;

		{
			int x = character.getX();
			int y = character.getY();
			for (Job job: _jobs) {
				if (job.getCharacter() == null && job.getAction() != Action.GATHER) {
					int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
					if (distance < bestDistance || bestDistance == -1) {
						if (job.getAction() == Action.BUILD && ResourceManager.getInstance().getMatter() == 0) {
							continue;
						}
						bestJob = job;
						bestDistance = distance;
					}
				}
			}
		}

		if (bestJob == null) {
			int x = character.getX();
			int y = character.getY();
			for (Job job: _jobs) {
				if (job.getCharacter() == null) {
					int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
					if (distance < bestDistance || bestDistance == -1) {
						bestJob = job;
						bestDistance = distance;
					}
				}
			}
		}

		if (bestJob != null) {
			Log.debug("bestjob: " + bestDistance + " (" + bestJob.getX() + ", " + bestJob.getY() + ")");
			_countFree--;
		} else {
			Log.debug("bestjob: null");
		}

		return bestJob;
	}
	
	private Job getJobForCharacterNeed(Character character) {
		CharacterNeeds needs = character.getNeeds();
		if (needs.getFood() < 20) {
			UserItem item = WorldMap.getInstance().getNearest(BaseItem.Type.BAR_PUB, character.getPosX(), character.getPosY());
			if (item != null) {
				Job job = new Job(++_id, item.getX(), item.getY());
				job.setAction(JobManager.Action.USE);
				job.setItemType(item.getType());
				job.setItem(item);
				job.setCharacterRequire(character);
				addJob(job);
				_countFree--;
				return job;
			}
		}
		
		return null;
	}

	public List<Job>			getJobs() { return _jobs; };
	int					getCount() { return _count; }
	int					getCountFree() { return _countFree; }

//	// TODO: ugly
//	Job	getJob() {
//	  if (_count == 0) {
//		return null;
//	  }
//
//	  int i = 0;
//	  std.list<Job>.iterator it = _jobs.begin();
//	  while (i++ < _start % _count) {
//		it++;
//	  }
//
//	  for (i = 0; i < _count; i++) {
//		if (it == _jobs.end()) {
//		  it = _jobs.begin();
//		}
//
//		if ((it).getCharacter() == null) {
//		  return it;
//		}
//
//		it++;
//	  }
//
//	  return null;
//	}

	public void	abort(Job job) {
	  Log.debug("Job abort: " + job.getId());
	  _start++;
	  _countFree++;
	  job.setCharacter(null);
	}

	public void	complete(Job job) {
	  Log.debug("Job complete: " + job.getId());

	  _jobs.remove(job);
	  _count--;
	  _start--;
	}

	public void	need(Character character, BaseItem.Type itemType) {
	  Log.debug("JobManager: Character '" + character.getName() + "' need item #" + itemType);

	  BaseItem item = WorldMap.getInstance().find(itemType, true);
	  if (item != null) {

		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.USE);
		job.setCharacterRequire(character);
		job.setItemType(item.getType());
		job.setItem(item);

		addJob(job);
		// PathManager.getInstance().getPathAsync(character, item);
		return;
	  }
	}

	void	addJob(Job job) {
	  _jobs.add(job);
	  _count++;
	  _countFree++;
	}

	public static JobManager getInstance() {
		if (sSelf == null) {
			sSelf = new JobManager();
		}
		return sSelf;
	}

	public static String getActionName(Action action) {
		switch (action) {
		case NONE: return "none";
		case BUILD: return "build";
		case GATHER: return "gather";
		case MOVE: return "move";
		case USE: return "use";
		}
		return null;
	}

	public void cancel(Job job) {
		Log.info("Job cancel: " + job.getId());
		job.setCharacter(null);
		_jobs.remove(job);
		_count--;
		_start--;
	}

	public void clear() {
		_jobs.clear();
	}

}

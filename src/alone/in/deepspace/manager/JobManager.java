package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.CharacterNeeds;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.Job.Abort;
import alone.in.deepspace.model.Job.JobStatus;
import alone.in.deepspace.model.jobCheck.CharacterGoToMettingRoom;
import alone.in.deepspace.model.jobCheck.CharacterPlayTime;
import alone.in.deepspace.model.jobCheck.CheckEmptyDispenser;
import alone.in.deepspace.model.jobCheck.CharacterHasItemToStore;
import alone.in.deepspace.model.jobCheck.CheckLowFood;
import alone.in.deepspace.model.jobCheck.JobCheck;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.UserItem;
import alone.in.deepspace.model.WorldRessource;
import alone.in.deepspace.util.Constant;
import alone.in.deepspace.util.Log;

public class JobManager {
	public enum Action {
		NONE, BUILD, GATHER, USE, MOVE, STORE, DESTROY, WORK, MINING, TAKE, USE_INVENTORY, REFILL
	}

	private static JobManager	sSelf;

	private List<JobCheck>		_jobsCheck;
	private List<JobCheck>		_routineJobsCheck;
	private List<Job> 			_jobs;
	private List<BaseItem> 		_routineItems;
	private int 				_id;

	JobManager() {
		Log.debug("JobManager");

		_id = 0;
		_jobs = new ArrayList<Job>();
		_routineItems = new ArrayList<BaseItem>();

		_jobsCheck = new ArrayList<JobCheck>();
		_jobsCheck.add(new CheckLowFood());
		_jobsCheck.add(new CheckEmptyDispenser());
		
		_routineJobsCheck = new ArrayList<JobCheck>();
		_routineJobsCheck.add(new CharacterHasItemToStore());
		_routineJobsCheck.add(new CharacterPlayTime());
		_routineJobsCheck.add(new CharacterGoToMettingRoom());

		Log.debug("JobManager done");
	}

	public List<Job>	getJobs() { return _jobs; };

	public Job	build(BaseItem item) {
		if (item == null) {
			Log.error("JobManager: build on null item");
			return null;
		}
		
		if (item.isComplete()) {
			Log.error("Build item: already complete, nothing to do");
			return null;
		}
		
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.BUILD);
		job.setItem(item);

		addJob(job);

		return job;
	}

	public Job	gather(BaseItem ressource) {
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

	public Job	build(ItemInfo info, int x, int y) {
		BaseItem item = null;

		// Structure
		if (info.isStructure) {
			BaseItem current = ServiceManager.getWorldMap().getStructure(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build structure: already exist on this area");
				return null;
			}
			item = ServiceManager.getWorldMap().putItem(info, x, y);
		}

		// Item
		else if (info.isUserItem) {
			BaseItem current = ServiceManager.getWorldMap().getItem(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (current != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else if (ServiceManager.getWorldMap().getStructure(x, y) == null
					|| ServiceManager.getWorldMap().getStructure(x, y).isFloor() == false) {
				Log.error("JobManager: add build on non invalid structure (null or not STRUCTURE_FLOOR)");
				return null;
			} else {
				item = ServiceManager.getWorldMap().putItem(info, x, y);
			}
		}

		// Ressource
		else if (info.isResource) {
			BaseItem currentItem = ServiceManager.getWorldMap().getItem(x, y);
			BaseItem currentRessource = ServiceManager.getWorldMap().getRessource(x, y);
			if (currentRessource != null && currentRessource.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (currentItem != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else {
				item = ServiceManager.getWorldMap().putItem(info, x, y);
			}
		}

		return build(item);
	}

	// TODO: one pass + check profession
	public Job getJob(Character character) {
		Log.debug("bestJob: start");

		Job bestJob = getJobForCharacterNeed(character);
		if (bestJob != null) {
			Log.debug("bestJob: 1");
			return bestJob;  
		}

		int bestDistance = -1;
		
		// Character is full: go back to storage area
		if (character.isFull()) {
			// TODO
			ItemInfo info = ServiceManager.getData().getItemInfo("base.storage");
			BaseItem storage = ServiceManager.getWorldMap().getNearest(info, character.getX(), character.getY());
			if (storage != null) {
				Job storeJob = createStoreJob(character, storage);
				addJob(storeJob);
				return storeJob;
			}
		}

		int x = character.getX();
		int y = character.getY();

		Log.debug("bestJob: 3");

		// Regular jobs
		if (bestJob == null) {
			for (Job job: _jobs) {
				if (job.getCharacter() == null && job.getFail() <= 0) {
					if (job.getAction() == Action.BUILD && ResourceManager.getInstance().getMatter() == 0) {
						job.setFail(Abort.NO_MATTER, Game.getFrame());
						continue;
					}
					if ((job.getAction() == Action.GATHER || job.getAction() == Action.MINING) && character.getSpace() == 0) {
						continue;
					}
					int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
					if (distance < bestDistance || bestDistance == -1) {
						bestJob = job;
						bestDistance = distance;
					}
				}
			}
		}

		Log.debug("bestJob: 4");

		// Failed jobs
		if (bestJob == null) {
			for (Job job: _jobs) {
				if (job.getCharacter() == null && job.getFail() > 0) {
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
		} else {
			Log.debug("bestjob: null");
		}

		return bestJob;
	}

	public Job createStoreJob(Character character, BaseItem storage) {
		Job job = new Job(++_id, storage.getX(), storage.getY());
		job.setAction(JobManager.Action.STORE);
		job.setItem(storage);
		job.setCharacterRequire(character);
		return job;
	}

	public Job createTakeJob(Character character, BaseItem storage, ItemFilter filter) {
		Log.debug("create take job");

		Job job = new Job(++_id, storage.getX(), storage.getY());
		job.setAction(JobManager.Action.TAKE);
		job.setItem(storage);
		job.setItemFilter(filter);
		job.setCharacterRequire(character);
		
		return job;
	}

	private Job getJobForCharacterNeed(Character character) {
		CharacterNeeds needs = character.getNeeds();
		if (needs.isHungry()) {
			ItemFilter filter = new ItemFilter(true, true);
			filter.food = true;

			// Have item in inventory
			BaseItem item = character.find(filter);
			if (item != null) {
				Job job = createUseInventoryJob(character, item);
				addJob(job);
				return job;
			}

			// Take item from storage
			item = ServiceManager.getWorldMap().findStorageContains(filter, character.getX(), character.getY());
			if (item != null) {
				Job job = createTakeJob(character, item, filter);
				addJob(job);
				return job;
			}
			
			// Looking for food dispenser
			for (int x = 0; x < ServiceManager.getWorldMap().getWidth(); x++) {
				for (int y = 0; y < ServiceManager.getWorldMap().getHeight(); y++) {
					item = ServiceManager.getWorldMap().getItem(x, y);
					if (item != null && item.matchFilter(filter)) {
						Job job = createUseJob(item);
						addJob(job);
						return job;
					}
				}
			}
		}

		return null;
	}

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

	public void	abort(Job job, Abort reason) {
		Log.debug("Job abort: " + job.getId());

		job.setStatus(JobStatus.ABORTED);
		
		// Job is invalid or USE action, don't resume
		if (reason == Job.Abort.INVALID || job.getAction() == Action.USE) {
			_jobs.remove(job);
			if (job.getItem() != null) {
				job.getItem().setOwner(null);
			}
			
			if (job.getSlot() != null) {
				job.getSlot().getItem().releaseSlot(job.getSlot());
			}
		}

		// Regular job, reset
		else {
			job.setFail(reason, Game.getFrame());
			job.setCharacter(null);
		}
	}

	public void	complete(Job job) {
		Log.debug("Job complete: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);
		
		if (job.getItem() != null) {
			job.getItem().setOwner(null);
		}
		
		if (job.getSlot() != null) {
			job.getSlot().getItem().releaseSlot(job.getSlot());
		}
		
		_jobs.remove(job);
	}

	public void	addJob(Job job) {
		if (job != null && _jobs.contains(job) == false) {
			_jobs.add(job);
		}
	}

	public static JobManager getInstance() {
		if (sSelf == null) {
			sSelf = new JobManager();
		}
		return sSelf;
	}

	public static String getActionName(Action action) {
		switch (action) {
		case NONE: 		return "none";
		case BUILD: 	return "build";
		case GATHER: 	return "gather";
		case REFILL: 	return "refill";
		case MOVE: 		return "move";
		case USE_INVENTORY:
		case USE: 		return "use";
		case STORE: 	return "store";
		case WORK: 		return "work";
		case DESTROY:	return "destroy";
		case MINING:	return "mine";
		case TAKE:		return "take";
		}
		return null;
	}

	public void clear() {
		_jobs.clear();
	}

	public void destroyItem(BaseItem item) {
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.DESTROY);
		job.setItem(item);
		addJob(job);
	}

	public void askStoreCarry() {
		// TODO Auto-generated method stub
		
	}

	public void move(Character character, int x, int y) {
		Job job = new Job(++_id, x, y);
		job.setAction(JobManager.Action.MOVE);
		job.setCharacterRequire(character);
		addJob(job);
	}

	public void giveRoutineJob(Character c) {
		Log.info("createRoutineJob");
		
		for (JobCheck jobCheck: _routineJobsCheck) {
			if (c.getJob() != null) {
				return;
			}
			jobCheck.check(this, c);
		}
		
//		BaseItem bestItem = null;
//		int bestDistance = Integer.MAX_VALUE;
//		
//		for (BaseItem item: _routineItems) {
//			if (item.isFree()) {
//				int distance = Math.abs(item.getX() - c.getX()) + Math.abs(item.getY() - c.getY());
//				if (distance < bestDistance) {
//					bestItem = item;
//				}
//			}
//		}
//		
//		if (bestItem != null) {
//			return createWorkJob(bestItem);
//		}
	}

	private Job createUseInventoryJob(Character character, BaseItem item) {
		if (!item.getInfo().isConsomable) {
			return null;
		}
		
		Job job = new Job(++_id);
		job.setPosition(character.getX(), character.getY());
		job.setAction(JobManager.Action.USE_INVENTORY);
		job.setItem(item);
		job.setDurationLeft(item.getInfo().onAction.duration);

		return job;
	}

	public Job createUseJob(BaseItem item) {
		if (item == null || !item.hasFreeSlot()) {
			return null;
		}
		
		Job job = new Job(++_id);
		ItemSlot slot = item.takeSlot(job);
		job.setSlot(slot);
		job.setPosition(slot.getX(), slot.getY());
		job.setAction(JobManager.Action.USE);
		job.setItem(item);
		job.setDurationLeft(item.getInfo().onAction.duration);

		return job;
	}
	
	private Job createWorkJob(BaseItem item) {
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.WORK);
		job.setItem(item);
		return job;
	}

	public void addRoutineItem(BaseItem item) {
		// TODO
		//_routineItems.add(item);
	}

	public Job createGatherJob(int x, int y) {
		WorldRessource res = ServiceManager.getWorldMap().getRessource(x, y);
		if (res == null) {
			return null;
		}
		
		// Resource is not gatherable
		if (res.getInfo().onGather == null) {
			return null;
		}
		
		Job job = new Job(++_id, res.getX(), res.getY());
		job.setAction(JobManager.Action.GATHER);
		job.setItem(res);

		return job;
	}

	public Job createMiningJob(int x, int y) {
		WorldRessource res = ServiceManager.getWorldMap().getRessource(x, y);
		if (res == null) {
			return null;
		}
		
		// Resource is not minable
		if (res.getInfo().onMine == null) {
			return null;
		}
		
		Job job = new Job(++_id, res.getX(), res.getY());
		job.setAction(JobManager.Action.MINING);
		job.setItem(res);
		
		return job;
	}

	public Job createDumpJob(int x, int y) {
		UserItem item = ServiceManager.getWorldMap().getItem(x, y);
		if (item == null) {
			return null;
		}
		
		Job job = new Job(++_id, item.getX(), item.getY());
		job.setAction(JobManager.Action.DESTROY);
		job.setItem(item);
		
		return job;
	}

	public Job createMovingJob(int x, int y, int stay) {
		Job job = new Job(++_id, x, y);
		job.setAction(JobManager.Action.MOVE);
		job.setDurationLeft(stay);
		return job;
	}

	public Job createRefillJob(Character character, StorageItem storage, ItemFilter filter, StorageItem dispenser) {
		if (storage == null || dispenser == null || storage == dispenser) {
			Log.error("createRefillJob: wrong items");
			return null;
		}
		
		Log.debug("create take job");

		Job job = new Job(++_id, storage.getX(), storage.getY());
		job.setAction(JobManager.Action.REFILL);
		job.setSubAction(JobManager.Action.TAKE);
		job.setDispenser(dispenser);
		dispenser.setWaitRefill(true);
		job.setItem(storage);
		job.setItemFilter(filter);
		job.setCharacterRequire(character);
		
		return job;
	}

	public void onLongUpdate() {
		for (JobCheck jobCheck: _jobsCheck) {
			jobCheck.check(this, null);
		}
	}
}

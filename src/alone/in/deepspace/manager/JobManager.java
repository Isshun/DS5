package alone.in.deepspace.manager;

import java.util.ArrayList;
import java.util.List;

import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.FactoryItem;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.StorageItem;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.Job.Abort;
import alone.in.deepspace.model.job.Job.JobStatus;
import alone.in.deepspace.model.job.JobBuild;
import alone.in.deepspace.model.job.JobDestroy;
import alone.in.deepspace.model.job.JobGather;
import alone.in.deepspace.model.job.JobMining;
import alone.in.deepspace.model.job.JobMove;
import alone.in.deepspace.model.job.JobRefill;
import alone.in.deepspace.model.job.JobStore;
import alone.in.deepspace.model.job.JobUse;
import alone.in.deepspace.model.jobCheck.CharacterGoToMettingRoom;
import alone.in.deepspace.model.jobCheck.CharacterHasItemToStore;
import alone.in.deepspace.model.jobCheck.CharacterIsFull;
import alone.in.deepspace.model.jobCheck.CharacterIsHungry;
import alone.in.deepspace.model.jobCheck.CharacterIsTired;
import alone.in.deepspace.model.jobCheck.CharacterPlayTime;
import alone.in.deepspace.model.jobCheck.CheckEmptyFactory;
import alone.in.deepspace.model.jobCheck.CheckGardenIsMature;
import alone.in.deepspace.model.jobCheck.CheckLowFood;
import alone.in.deepspace.model.jobCheck.JobCharacterCheck;
import alone.in.deepspace.model.jobCheck.JobCheck;
import alone.in.deepspace.util.Log;

public class JobManager {
	public enum Action {
		NONE, BUILD, GATHER, USE, MOVE, STORE, DESTROY, WORK, MINING, TAKE, USE_INVENTORY, REFILL
	}

	private static JobManager	_self;
	private List<Job> 			_jobs;

	private JobCharacterCheck[]		_priorityJobsCheck = {
			new CharacterIsTired(),
			new CharacterIsHungry(),
			new CharacterIsFull(),
	};

	private JobCheck[]		_jobsCheck = {
			new CheckLowFood(),
			new CheckEmptyFactory(),
			new CheckGardenIsMature()
	};

	private JobCharacterCheck[]		_routineJobsCheck = {
			new CharacterHasItemToStore(),
			new CharacterPlayTime(),
			new CharacterGoToMettingRoom()
	};

	private int _nbVisibleJob;
	private List<Job> _toRemove;

	public JobManager() {
		Log.debug("JobManager");

		_self = this;
		_jobs = new ArrayList<Job>();
		_toRemove = new ArrayList<Job>();

		Log.debug("JobManager done");
	}

	public List<Job>	getJobs() { return _jobs; };

	public Job	addBuild(ItemBase item) {
		if (item == null) {
			Log.error("JobManager: build on null item");
			return null;
		}

		if (item.isComplete()) {
			Log.error("Build item: already complete, nothing to do");
			return null;
		}

		Job job = JobBuild.create(item);
		addJob(job);

		return job;
	}

	public Job	addGather(WorldResource ressource) {
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

		Job job = JobGather.create(ressource);
		addJob(job);

		return job;
	}

	public void	removeJob(ItemBase item) {
		List<Job> toRemove = new ArrayList<Job>();

		for (Job job: _jobs) {
			if (job.getItem() == item) {
				toRemove.add(job);
			}
		}

		for (Job job: toRemove) {
			removeJob(job);
		}
	}

	public Job	build(ItemInfo info, int x, int y) {
		ItemBase item = null;

		// Structure
		if (info.isStructure) {
			ItemBase current = ServiceManager.getWorldMap().getStructure(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build structure: already exist on this area");
				return null;
			}
			item = ServiceManager.getWorldMap().putItem(info, x, y);
		}

		// Item
		else if (info.isUserItem) {
			ItemBase current = ServiceManager.getWorldMap().getItem(x, y);
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

		// Resource
		else if (info.isResource) {
			ItemBase currentItem = ServiceManager.getWorldMap().getItem(x, y);
			ItemBase currentRessource = ServiceManager.getWorldMap().getRessource(x, y);
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

		return addBuild(item);
	}

	/**
	 * Assign job to character
	 * 
	 * @param character
	 */
	public void assignJob(Character character) {
		Log.debug("assignJob");

		// Priority jobs
		if (createPriorityJob(character)) {
			return;
		}

		// Regular jobs
		Job job = getBestJob(character);
		if (job != null) {
			job.setStatus(JobStatus.RUNNING);
			character.setJob(job);
			return;
		}

		// Routine jobs
		if (createRoutineJob(character)) {
			return;
		}

		// TODO
		if (job != null) {
			Log.info("assign [" + job.getShortLabel() + "] to [" + character.getName() + "]");
		}

		Log.debug("assignJob: done");
	}

	// TODO: one pass + check profession
	private Job getBestJob(Character character) {
		Log.debug("getBestJob");

		int x = character.getX();
		int y = character.getY();
		int bestDistance = Integer.MAX_VALUE;
		Job bestJob = null;

		// Regular jobs
		for (Job job: _jobs) {
			if (job.isFinish() == false && job.getCharacter() == null && job.getFail() <= 0) {
				if (job.getAction() == Action.BUILD && ResourceManager.getInstance().getMatter().value == 0) {
					// TODO
					job.setFail(Abort.NO_BUILD_RESOURCES, MainRenderer.getFrame());
					continue;
				}
				if ((job.getAction() == Action.GATHER || job.getAction() == Action.MINING) && character.getSpace() == 0) {
					continue;
				}
				int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
				if (distance < bestDistance && job.check(character)) {
					bestJob = job;
					bestDistance = distance;
				}
			}
		}

		Log.debug("bestJob: 4");

		// Failed jobs
		if (bestJob == null) {
			for (Job job: _jobs) {
				if (job.getCharacter() == null && job.getFail() > 0) {
					int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
					if (distance < bestDistance && job.check(character)) {
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

		// Already aborted
		if (job.getStatus() == JobStatus.ABORTED) {
			return;
		}

		job.setStatus(JobStatus.ABORTED);

		// Abort because character inventory is full
		if (reason == Abort.NO_LEFT_CARRY) {
			addStoreJob(job.getCharacter());
		}

		// Abort because factory is out of components
		if (reason == Abort.NO_COMPONENTS) {
			addRefillJob((FactoryItem)job.getItem());
		}

		// Job is invalid, don't resume
		if (reason == Abort.INVALID) {
			removeJob(job);
			return;
		}

		// Job is USE / USE_INVENTORY / MOVE / TAKE / STORE / REFILL action, don't resume
		if (job.getAction() == Action.MOVE ||
				job.getAction() == Action.USE ||
				job.getAction() == Action.REFILL ||
				job.getAction() == Action.USE_INVENTORY ||
				job.getAction() == Action.TAKE ||
				job.getAction() == Action.STORE) {
			removeJob(job);
			return;
		}

		// Regular job, reset
		// TODO
		job.setFail(reason, MainRenderer.getFrame());
		job.setCharacter(null);
	}

	public void addRefillJob(FactoryItem factory) {
		if (factory == null) {
			Log.error("addRefillJob: item is null or invalid");
			return;
		}

		// Looking for storage containing accepted item
		StorageItem storage = null;
		ItemFilter itemFilter = new ItemFilter(true, true); 
		for (ItemInfo neededItemInfo: factory.getInfo().onAction.itemAccept) {
			if (storage == null) {
				itemFilter.itemNeeded = neededItemInfo;
				storage = ServiceManager.getWorldMap().findStorageContains(itemFilter, factory.getX(), factory.getY());
			}
		}

		// No storage containing needed item
		if (storage == null) {
			return;
		}

		// Create jobs if needed item is available
		Job job = createRefillJob(null, storage, itemFilter, factory);
		if (job != null) {
			addJob(job);
		}
	}

	private void removeJob(Job job) {
		if (job.getCharacter() != null) {
			job.getCharacter().setJob(null);
			job.setCharacter(null);
		}

		if (job.getItem() != null) {
			job.getItem().setOwner(null);
		}

		if (job.getSlot() != null) {
			job.getSlot().getItem().releaseSlot(job.getSlot());
		}

		_toRemove.add(job);
		if (Action.MOVE.equals(job.getAction()) == false) {
			_nbVisibleJob--;
		}
	}

	public void	complete(Job job) {
		Log.debug("Job complete: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);

		removeJob(job);
	}

	public void	addJob(Job job) {
		if (job != null && _jobs.contains(job) == false) {

			if (Action.MOVE.equals(job.getAction()) == false) {
				_nbVisibleJob++;
			}

			_jobs.add(job);
		}
	}

	public static JobManager getInstance() {
		return _self;
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

	public void addDestroyJob(ItemBase item) {
		Job job = JobDestroy.create(item);
		addJob(job);
	}

	public void addMoveJob(Character character, int x, int y) {
		Job job = JobMove.create(character, x, y, 0);
		addJob(job);
	}

	public Job addMoveJob(Character character, int x, int y, int stay) {
		Job job = JobMove.create(character, x, y, stay);
		addJob(job);
		return job;
	}

	private boolean createPriorityJob(Character character) {
		Log.debug("createPriorityJob");

		for (JobCharacterCheck jobCheck: _priorityJobsCheck) {
			if (jobCheck.create(this, character)) {
				return true;
			}
		}

		return false;
	}

	public boolean createRoutineJob(Character character) {
		Log.debug("createRoutineJob");

		for (JobCharacterCheck jobCheck: _routineJobsCheck) {
			if (jobCheck.create(this, character)) {
				return true;
			}
		}

		return false;
	}

	public void addRoutineItem(ItemBase item) {
		// TODO
		//_routineItems.add(item);
	}

	public Job createGatherJob(int x, int y) {
		System.out.println("gather: " + x + " x " + y);

		WorldResource res = ServiceManager.getWorldMap().getRessource(x, y);
		if (res == null) {
			return null;
		}

		Job job = JobGather.create(res);
		return job;
	}

	public Job createMiningJob(int x, int y) {
		WorldResource res = ServiceManager.getWorldMap().getRessource(x, y);
		if (res == null) {
			return null;
		}

		Job job = JobMining.create(res);
		return job;
	}

	public Job createDumpJob(int x, int y) {
		UserItem item = ServiceManager.getWorldMap().getItem(x, y);
		if (item == null) {
			return null;
		}

		Job job = JobDestroy.create(item);
		return job;
	}

	public Job createRefillJob(Character character, StorageItem storage, ItemFilter filter, FactoryItem factory) {
		Job job = JobRefill.create(factory, storage, filter);
		if (job != null) {
			job.setCharacterRequire(character);
		}
		return job;
	}
	
	public void onLongUpdate() {
		for (JobCheck jobCheck: _jobsCheck) {
			jobCheck.create(this);
		}

		// Remove invalid job
		List<Job> invalidJobs = new ArrayList<Job>();
		for (Job job: _jobs) {
			if (job.getReason() == Abort.INVALID) {
				invalidJobs.add(job);
			}
		}
		for (Job job: invalidJobs) {
			removeJob(job);
		}
	}

	public void addGatherJob(int x, int y) {
		Job job = createGatherJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public void addMineJob(int x, int y) {
		Job job = createMiningJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public Job addStoreJob(Character character) {
		Job job = JobStore.create(character);
		if (job != null) {
			addJob(job);
		}
		return job;
	}

	public int getNbVisibleJob() {
		return _nbVisibleJob;
	}

	public Job addUseJob(ItemBase item) {
		Job job = JobUse.create(item);
		if (job != null) {
			addJob(job);
		}
		return job;
	}

	public void addJob(Job job, Character character) {
		addJob(job);
		job.setCharacter(character);
	}

	// Remove finished jobs
	public void cleanJobs() {
		if (_toRemove.isEmpty() == false) {
			_jobs.removeAll(_toRemove);
			_toRemove.clear();
		}
	}
}

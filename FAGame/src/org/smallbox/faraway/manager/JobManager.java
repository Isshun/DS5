package org.smallbox.faraway.manager;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.check.old.CharacterCheck;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.job.*;
import org.smallbox.faraway.model.job.BaseJob.JobAbortReason;
import org.smallbox.faraway.model.job.BaseJob.JobStatus;

import java.util.ArrayList;
import java.util.List;

public class JobManager {
	public final static Color COLOR_BUILD = new Color(170, 128, 64);
	public final static Color COLOR_MOVE = Color.CYAN;
	public final static Color COLOR_GATHER = Color.GREEN;
	public final static Color COLOR_MINING = Color.GREEN;
	public final static Color COLOR_WORK = Color.GREEN;
	public final static Color COLOR_REFILL = Color.GREEN;
	public final static Color COLOR_NONE = Color.BLACK;
	public final static Color COLOR_USE_INVENTORY = Color.BLUE;
	public final static Color COLOR_USE = Color.BLUE;
	public final static Color COLOR_DESTROY = new Color(200, 20, 20);
	public final static Color COLOR_STORE = new Color(180, 100, 255);
	public final static Color COLOR_TAKE = new Color(180, 100, 255);

    public void addJob(UserItem item, ItemInfo.ItemInfoAction action) {
        switch (action.type) {
            case "cook":
                addJob(JobCook.create(action, item));
                break;
            case "craft":
                addJob(JobCraft.create(action, item));
                break;
        }
    }

	private static JobManager		_self;
	private List<BaseJob> 			_jobs;
	private int 					_nbVisibleJob;
	private List<BaseJob> 			_toRemove;

//	private CharacterCheck[]		_priorityJobsCheck = {
//			new CharacterIsTired(),
//			new CharacterIsFull(),
//			new CharacterIsHungry(),
//	};
//
//	private Check[]				    _jobsCheck = {
////			new CheckLowFood(),
////			new CheckEmptyFactory(),
////			new CheckGardenIsMature()
//	};
//
//	private CharacterCheck[]		_routineJobsCheck = {
//			new CharacterHasItemToStore(),
//			new CharacterPlayTime(),
//			new CharacterGoToMettingRoom()
//	};

	public JobManager() {
		Log.debug("JobManager");

		_self = this;
		_jobs = new ArrayList<>();
		_toRemove = new ArrayList<>();

		Log.debug("JobManager done");
	}

	public List<BaseJob>	getJobs() { return _jobs; };

	public BaseJob addBuild(ItemBase item) {
		if (item == null) {
			Log.error("JobManager: build on null item");
			return null;
		}

		if (item.isComplete()) {
			Log.error("Build item: already close, nothing to do");
			return null;
		}

		BaseJob job = JobBuild.create(item);
		addJob(job);

		return job;
	}

	public BaseJob addGather(WorldResource ressource) {
		if (ressource == null) {
			Log.error("JobManager: gather on null area");
			return null;
		}

		// return if job already exist for this item
		for (BaseJob job: _jobs) {
			if (job.getItem() == ressource) {
				return null;
			}
		}

		BaseJob job = JobGather.create(ressource);
		addJob(job);

		return job;
	}

	public void	removeJob(ItemBase item) {
		List<BaseJob> toRemove = new ArrayList<>();

		for (BaseJob job: _jobs) {
			if (job.getItem() == item) {
				toRemove.add(job);
			}
		}

		for (BaseJob job: toRemove) {
			removeJob(job);
		}
	}

	public BaseJob build(ItemInfo info, int x, int y) {
		ItemBase item = null;

		// Structure
		if (info.isStructure) {
			ItemBase current = ServiceManager.getWorldMap().getStructure(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build structure: already exist on this area");
				return null;
			}
			item = ServiceManager.getWorldMap().putItem(info, x, y, 0, 0);
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
				item = ServiceManager.getWorldMap().putItem(info, x, y, 0, 0);
			}
		}

		// Resource
		else if (info.isResource) {
			ItemBase currentItem = ServiceManager.getWorldMap().getItem(x, y);
			ItemBase currentRessource = ServiceManager.getWorldMap().getResource(x, y);
			if (currentRessource != null && currentRessource.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (currentItem != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else {
				item = ServiceManager.getWorldMap().putItem(info, x, y, 0, 0);
			}
		}

		return addBuild(item);
	}

	/**
	 * Assign job to character
	 * 
	 * @param character
	 */
	public void assignJob(CharacterModel character) {
		// Priority jobs
		if (createPriorityJob(character)) {
			Log.info("assign priority job to [" + character.getName() + "]");
			return;
		}

		// Regular jobs
		BaseJob job = getBestJob(character);
		if (job != null) {
			job.setStatus(JobStatus.RUNNING);
			character.setJob(job);
			Log.info("assign [" + job.getShortLabel() + "] to [" + character.getName() + "]");
			return;
		}

//		// Routine jobs
//		if (createRoutineJob(character)) {
//			Log.info("assign routine job to [" + character.getName() + "]");
//			return;
//		}
	}

	// TODO: one pass + check profession
	private BaseJob getBestJob(CharacterModel character) {
		Log.debug("getBestJob");

		int x = character.getX();
		int y = character.getY();
		int bestDistance = Integer.MAX_VALUE;
		BaseJob bestJob = null;

		// Regular jobs
        for (CharacterModel.TalentEntry talent: character.getTalents()) {
            for (BaseJob job: _jobs) {
                if (talent.type == job.getTalentNeeded() && job.isFinish() == false && job.getCharacter() == null && job.getFail() <= 0) {
                    int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
                    if (distance < bestDistance && job.check(character)) {
                        bestJob = job;
                        bestDistance = distance;
                    }
                }
            }
            // Job found for current talent
            if (bestJob != null) {
                break;
            }
        }

		Log.debug("bestJob: 4");

		// Failed jobs
		if (bestJob == null) {
			for (BaseJob job: _jobs) {
				if (job.getCharacter() == null && job.getFail() > 0) {
					if (job.getReason() == JobAbortReason.BLOCKED && job.getBlocked() < Game.getUpdate() + Constant.DELAY_TO_RESTART_BLOCKED_JOB) {
						continue;
					}

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

	public void addRefillJob(FactoryItem factory) {
		if (factory == null) {
			Log.error("addRefillJob: item is null or invalid");
			return;
		}

        // TODO
//		// Looking for storage containing accepted item
//		for (ItemInfo neededItemInfo: factory.getInfo().actions.itemAccept) {
//			ItemFilter filter = ItemFilter.createConsomableFilter(neededItemInfo);
//
//			// Looking for storage containing needed item
//			StorageRoom storage = Game.getRoomManager().findStorageContains(filter, factory.getX(), factory.getY());
//			if (storage != null) {
//				Job job = createRefillJob(null, storage, filter, factory);
//				if (job != null) {
//					addJob(job);
//				}
//				return;
//			}
//		}
	}

	public void removeJob(BaseJob job) {
        System.out.println("remove job: " + job.getLabel() + " (" + job.getReasonString() + ")");

		if (job.getCharacter() != null) {
			job.getCharacter().setJob(null);
			job.setCharacter(null);
		}

		if (job.getItem() != null) {
			job.getItem().setOwner(null);
            job.getItem().removeJob(job);
		}

		if (job.getSlot() != null) {
			job.getSlot().getItem().releaseSlot(job.getSlot());
		}

		_toRemove.add(job);
		if (job.isVisibleInUI()) {
			_nbVisibleJob--;
		}
	}

	public void	addJob(BaseJob job) {
		if (job == null || _jobs.contains(job)) {
			Log.error("Trying to add null or already existing job to JobManager");
			return;
		}

		if (job.isVisibleInUI()) {
			_nbVisibleJob++;
		}

		System.out.println("add job: " + job.getLabel());

		_jobs.add(job);
	}

	public static JobManager getInstance() {
		return _self;
	}

	public void clear() {
		_jobs.clear();
	}

	public void addDestroyJob(ItemBase item) {
		BaseJob job = JobDestroy.create(item);
		addJob(job);
	}

	public void addMoveJob(CharacterModel character, int x, int y) {
		BaseJob job = JobMove.create(character, x, y, 0);
		addJob(job);
	}

	public BaseJob addMoveJob(CharacterModel character, int x, int y, int stay) {
		BaseJob job = JobMove.create(character, x, y, stay);
		addJob(job);
		return job;
	}

	private boolean createPriorityJob(CharacterModel character) {
		Log.debug("createPriorityJob");

		for (CharacterCheck jobCheck: character.getPriorities()) {
			if (jobCheck.create(this)) {
				return true;
			}
		}

		return false;
	}

	public BaseJob createGatherJob(int x, int y) {
		System.out.println("gather: " + x + " x " + y);

		WorldResource res = ServiceManager.getWorldMap().getResource(x, y);
		if (res == null) {
			return null;
		}

		BaseJob job = JobGather.create(res);
		return job;
	}

	public BaseJob createMiningJob(int x, int y) {
		WorldResource res = ServiceManager.getWorldMap().getResource(x, y);
		if (res == null) {
			return null;
		}

		BaseJob job = JobMining.create(res);
		return job;
	}

	public BaseJob createDumpJob(int x, int y) {
		UserItem item = ServiceManager.getWorldMap().getItem(x, y);
		if (item == null) {
			return null;
		}

		BaseJob job = JobDestroy.create(item);
		return job;
	}

	public void onLongUpdate() {
        for (CharacterModel character: Game.getCharacterManager().getCharacters()) {
            for (CharacterModel.TalentEntry talent: character.getTalents()) {
                if (talent.check.create(this)) {
                    break;
                }
            }
        }

		// Remove invalid job
		List<BaseJob> invalidJobs = new ArrayList<>();
		for (BaseJob job: _jobs) {
			if (job.getReason() == JobAbortReason.INVALID) {
				invalidJobs.add(job);
			}
		}
		for (BaseJob job: invalidJobs) {
			removeJob(job);
		}
	}

	public void addGatherJob(int x, int y) {
		BaseJob job = createGatherJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public void addMineJob(int x, int y) {
		BaseJob job = createMiningJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public BaseJob addStoreJob(CharacterModel character) {
//		BaseJob job = JobHaul.create(character);
//		if (job != null) {
//			addJob(job);
//		}
//		return job;
        throw new RuntimeException("not implemented");
	}

	public int getNbVisibleJob() {
		return _nbVisibleJob;
	}

	public BaseJob addUseJob(ItemBase item) {
		BaseJob job = JobUse.create(item);
		if (job != null) {
			addJob(job);
		}
		return job;
	}

	public void addJob(BaseJob job, CharacterModel character) {
		addJob(job);
		if (job != null) {
			job.setCharacter(character);
		}
	}

	// Remove finished jobs
	public void cleanJobs() {
		if (_toRemove.isEmpty() == false) {
			_jobs.removeAll(_toRemove);
			_toRemove.clear();
		}
	}

	public void close(BaseJob job) {
		Log.debug("Job close: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);

		removeJob(job);
	}

	public void close(BaseJob job, JobAbortReason reason) {
		Log.debug("Job close: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);

		removeJob(job);
	}

    public void quit(BaseJob job) {
        if (job != null) {
            if (job.getCharacter() != null) {
                job.getCharacter().setJob(null);
            }
            job.setCharacter(null);

            if (!job.canBeResume()) {
                close(job);
            }
        }
    }

	public void quit(BaseJob job, JobAbortReason reason) {
		Log.debug("Job quit: " + job.getId());

		// Already aborted
		if (job.getStatus() == JobStatus.ABORTED) {
			return;
		}

		job.setStatus(JobStatus.ABORTED);
		job.setFail(reason, MainRenderer.getFrame());
		job.setCharacter(null);

		// Remove character lock from item
		if (job.getItem() != null && job.getItem().getOwner() == job.getCharacter()) {
			job.getItem().setOwner(null);
		}

		// Abort because path to item is blocked
		if (reason == JobAbortReason.BLOCKED) {
			if (job.getItem() != null) {
				job.getItem().setBlocked(Game.getUpdate());
			}
		}

		// Abort because character inventory is full
		if (reason == JobAbortReason.NO_LEFT_CARRY) {
			addStoreJob(job.getCharacter());
		}

		// Abort because factory is out of components
		if (reason == JobAbortReason.NO_COMPONENTS) {
			addRefillJob((FactoryItem)job.getItem());
		}

		// Job is invalid, don't resume
		if (reason == JobAbortReason.INVALID) {
			removeJob(job);
			return;
		}

		// Job is USE / USE_INVENTORY / MOVE / TAKE / STORE / REFILL action, don't resume
		if (!job.canBeResume()) {
			removeJob(job);
			return;
		}

		// Regular job, reset
	}

}

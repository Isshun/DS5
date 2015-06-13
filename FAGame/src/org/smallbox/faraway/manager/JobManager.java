package org.smallbox.faraway.manager;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.check.CheckCharacterUse;
import org.smallbox.faraway.model.check.character.CheckCharacterExhausted;
import org.smallbox.faraway.model.check.character.CheckCharacterHungry;
import org.smallbox.faraway.model.check.old.CharacterCheck;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.job.*;
import org.smallbox.faraway.model.job.JobModel.JobAbortReason;
import org.smallbox.faraway.model.job.JobModel.JobStatus;

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

    public void addJob(ItemModel item, ItemInfo.ItemInfoAction action) {
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
	private List<JobModel> 			_jobs;
	private int 					_nbVisibleJob;
	private List<JobModel> 			_toRemove;
    private List<CharacterCheck>    _priorities;

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

        _priorities = new ArrayList<>();
        _priorities.add(new CheckCharacterUse());
        _priorities.add(new CheckCharacterExhausted());
        _priorities.add(new CheckCharacterHungry());

        Log.debug("JobManager done");
	}

	public List<JobModel>	getJobs() { return _jobs; };

	public JobModel addBuild(MapObjectModel item) {
		if (item == null) {
			Log.error("JobManager: build on null item");
			return null;
		}

		if (item.isComplete()) {
			Log.error("Build item: already close, nothing to do");
			return null;
		}

		JobModel job = JobBuild.create(item);
		addJob(job);

		return job;
	}

	public JobModel addGather(ResourceModel ressource) {
		if (ressource == null) {
			Log.error("JobManager: gather on null area");
			return null;
		}

		// return if job already exist for this item
		for (JobModel job: _jobs) {
			if (job.getItem() == ressource) {
				return null;
			}
		}

		JobModel job = JobGather.create(ressource);
		addJob(job);

		return job;
	}

	public void	removeJob(MapObjectModel item) {
		List<JobModel> toRemove = new ArrayList<>();

		for (JobModel job: _jobs) {
			if (job.getItem() == item) {
				toRemove.add(job);
			}
		}

		for (JobModel job: toRemove) {
			removeJob(job);
		}
	}

	public JobModel build(ItemInfo info, int x, int y) {
		MapObjectModel item = null;

		// Structure
		if (info.isStructure) {
			MapObjectModel current = ServiceManager.getWorldMap().getStructure(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build structure: already exist on this area");
				return null;
			}
			item = ServiceManager.getWorldMap().putObject(info, x, y, 0, 0);
		}

		// Item
		else if (info.isUserItem) {
			MapObjectModel current = ServiceManager.getWorldMap().getItem(x, y);
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
				item = ServiceManager.getWorldMap().putObject(info, x, y, 0, 0);
			}
		}

		// Resource
		else if (info.isResource) {
			MapObjectModel currentItem = ServiceManager.getWorldMap().getItem(x, y);
			MapObjectModel currentRessource = ServiceManager.getWorldMap().getResource(x, y);
			if (currentRessource != null && currentRessource.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (currentItem != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else {
				item = ServiceManager.getWorldMap().putObject(info, x, y, 0, 0);
			}
		}

		return addBuild(item);
	}

	/**
	 * Looking for best job to fit character
	 * 
	 * @param character
	 */
	public void assignJob(CharacterModel character) {
		// Priority jobs
		if (assignPriorityJob(character)) {
            Log.debug("assign priority job (" + character.getName() + " -> " + character.getJob().getLabel() + ")");
			return;
		}

		// Regular jobs
        if (assignRegularJob(character)) {
            Log.debug("assign regular job (" + character.getName() + " -> " + character.getJob().getLabel() + ")");
            return;
        }

        if (assignFailJob(character)) {
            Log.debug("assign failed job (" + character.getName() + " -> " + character.getJob().getLabel() + ")");
            return;
        }
	}

	public void removeJob(JobModel job) {
        Log.debug("remove job: " + job.getLabel() + " (" + job.getReasonString() + ")");

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

	public void	addJob(JobModel job) {
		if (job == null || _jobs.contains(job)) {
			Log.error("Trying to add null or already existing job to JobManager");
			return;
		}

		if (job.isVisibleInUI()) {
			_nbVisibleJob++;
		}

		Log.debug("add job: " + job.getLabel());

		_jobs.add(job);
	}

	public static JobManager getInstance() {
		return _self;
	}

	public void clear() {
		_jobs.clear();
	}

	public void addDestroyJob(MapObjectModel item) {
		JobModel job = JobDump.create(item);
		addJob(job);
	}

    /**
     * Create priority job for characters (eat / sleep / get oxygen / move to temperate area)
     *
     * @param character
     * @return
     */
	private boolean assignPriorityJob(CharacterModel character) {
		for (CharacterCheck jobCheck: _priorities) {
			if (jobCheck.check(character)) {
				JobModel job = jobCheck.create(character);
				if (job != null) {
                    assignJobToCharacter(job, character);
					return true;
				}
			}
		}

		return false;
	}

    /**
     * Get job from queue matching character talents
     *
     * @param character
     */
    // TODO: one pass + check profession
    private boolean assignRegularJob(CharacterModel character) {
        int x = character.getX();
        int y = character.getY();
        int bestDistance = Integer.MAX_VALUE;
        JobModel bestJob = null;

        // Regular jobs
        for (CharacterModel.TalentEntry talent: character.getTalents()) {
            for (JobModel job: _jobs) {
                if (talent.type == job.getTalentNeeded() && !job.isFinish() && job.getCharacter() == null && job.getFail() <= 0) {
                    int distance = Math.abs(x - job.getX()) + Math.abs(y - job.getY());
                    if (distance < bestDistance && job.check(character)) {
                        bestJob = job;
                        bestDistance = distance;
                    }
                }
            }
            // Job found for current talent
            if (bestJob != null) {
                assignJobToCharacter(bestJob, character);
                return true;
            }
        }

        return false;
    }

    /**
     * Assign failed job
     *
     * @param character
     * @return
     */
    private boolean assignFailJob(CharacterModel character) {
        int x = character.getX();
        int y = character.getY();
        int bestDistance = Integer.MAX_VALUE;
        JobModel bestJob = null;

        for (JobModel job: _jobs) {
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

        // Job found
        if (bestJob != null) {
            assignJobToCharacter(bestJob, character);
            return true;
        }

        return false;
    }

    /**
     * Assign and start job for designed character
     *
     * @param job
     * @param character
     */
    private void assignJobToCharacter(JobModel job, CharacterModel character) {
        job.setStatus(JobStatus.RUNNING);
        job.setCharacter(character);
        character.setJob(job);
    }

    public JobModel createGatherJob(int x, int y) {
		Log.debug("gather: " + x + " x " + y);

		ResourceModel res = ServiceManager.getWorldMap().getResource(x, y);
		if (res == null) {
			return null;
		}

		JobModel job = JobGather.create(res);
		return job;
	}

	public JobModel createMiningJob(int x, int y) {
		ResourceModel res = ServiceManager.getWorldMap().getResource(x, y);
		if (res == null) {
			return null;
		}

		JobModel job = JobMining.create(res);
		return job;
	}

	public void onLongUpdate() {
		// Remove invalid job
		_jobs.stream().filter(job -> job.getReason() == JobAbortReason.INVALID).forEach(this::removeJob);
	}

	public void addGatherJob(int x, int y) {
		JobModel job = createGatherJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public void addMineJob(int x, int y) {
		JobModel job = createMiningJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public JobModel addStoreJob(CharacterModel character) {
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

	public JobModel addUseJob(MapObjectModel item) {
		JobModel job = JobUse.create(item);
		if (job != null) {
			addJob(job);
		}
		return job;
	}

	public void addJob(JobModel job, CharacterModel character) {
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

	public void close(JobModel job) {
		Log.debug("Job close: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);

		removeJob(job);
	}

	public void close(JobModel job, JobAbortReason reason) {
		Log.debug("Job close: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);

		removeJob(job);
	}

    public void quit(JobModel job) {
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

	public void quit(JobModel job, JobAbortReason reason) {
		Log.debug("Job quit: " + job.getId());

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

//		// Abort because factory is out of components
//		if (reason == JobAbortReason.NO_COMPONENTS) {
//			addRefillJob((FactoryItem)job.getItem());
//		}

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

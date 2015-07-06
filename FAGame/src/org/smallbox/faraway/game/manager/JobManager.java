package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.CheckCharacterJoyDepleted;
import org.smallbox.faraway.game.model.check.CheckCharacterOxygen;
import org.smallbox.faraway.game.model.check.CheckCharacterUse;
import org.smallbox.faraway.game.model.check.character.CheckCharacterExhausted;
import org.smallbox.faraway.game.model.check.character.CheckCharacterHungry;
import org.smallbox.faraway.game.model.check.joy.CheckJoyWalk;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.job.*;
import org.smallbox.faraway.game.model.job.BaseJobModel.JobAbortReason;
import org.smallbox.faraway.game.model.job.BaseJobModel.JobStatus;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobManager extends BaseManager {
	private static JobManager					_self;
	private final List<CharacterCheck> 			_joys;
	private final List<CharacterCheck>  		_priorities;
	private final BlockingQueue<BaseJobModel> 	_jobs;
	private final List<BaseJobModel>			_toRemove;
	private int 								_nbVisibleJob;

	public JobManager() {
		Log.debug("JobManager");

		_self = this;
		_jobs = new LinkedBlockingQueue<>();
		_toRemove = new ArrayList<>();

        _priorities = new ArrayList<>();
		_priorities.add(new CheckCharacterOxygen());
        _priorities.add(new CheckCharacterUse());
        _priorities.add(new CheckCharacterExhausted());
		_priorities.add(new CheckCharacterHungry());
		_priorities.add(new CheckCharacterJoyDepleted());

		_joys = new ArrayList<>();
//		_joys.add(new CheckJoyTalk());
//		_joys.add(new CheckJoyWalk());
		_joys.add(new CheckJoyItem());
//		_joys.add(new CheckJoySleep());

        Log.debug("JobManager done");
	}

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onUpdate(int tick) {
        cleanJobs();

        if (tick % 10 == 0) {
            // Create haul jobs
            _jobs.stream().filter(job -> job instanceof JobHaul).forEach(job -> ((JobHaul)job).getItemAround());
            Game.getWorldManager().getConsumables().stream().filter(consumable -> consumable.getHaul() == null && !consumable.inValidStorage()).forEach(consumable -> {
				addJob(JobHaul.create(consumable));
			});

            // Remove invalid job
            _jobs.stream().filter(job -> job.getReason() == JobAbortReason.INVALID).forEach(this::removeJob);
        }
    }

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

	public Collection<BaseJobModel> getJobs() { return _jobs; };

	public BaseJobModel addBuild(MapObjectModel item) {
		if (item == null) {
			Log.error("JobManager: build on null item");
			return null;
		}

		if (item.isComplete()) {
			Log.error("Build item: already close, nothing to do");
			return null;
		}

		BaseJobModel job = JobBuild.create(item);
		addJob(job);

		return job;
	}

	public BaseJobModel addGather(ResourceModel ressource) {
		if (ressource == null) {
			Log.error("JobManager: gather on null area");
			return null;
		}

		// return if job already exist for this item
		for (BaseJobModel job: _jobs) {
			if (job.getItem() == ressource) {
				return null;
			}
		}

		BaseJobModel job = JobGather.create(ressource);
		addJob(job);

		return job;
	}

	public void	removeJob(MapObjectModel item) {
		List<BaseJobModel> toRemove = new ArrayList<>();

		for (BaseJobModel job: _jobs) {
			if (job.getItem() == item) {
				toRemove.add(job);
			}
		}

		for (BaseJobModel job: toRemove) {
			removeJob(job);
		}
	}

	public BaseJobModel build(ItemInfo info, int x, int y) {
		MapObjectModel item = null;

		// Structure
		if (info.isStructure) {
			MapObjectModel current = Game.getWorldManager().getStructure(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build structure: already exist on this area");
				return null;
			}
			item = Game.getWorldManager().putObject(info, x, y, 0, 0);
		}

		// Item
		else if (info.isUserItem) {
			MapObjectModel current = Game.getWorldManager().getItem(x, y);
			if (current != null && current.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (current != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else if (Game.getWorldManager().getStructure(x, y) == null
					|| Game.getWorldManager().getStructure(x, y).isFloor() == false) {
				Log.error("JobManager: add build on non invalid structure (null or not STRUCTURE_FLOOR)");
				return null;
			} else {
				item = Game.getWorldManager().putObject(info, x, y, 0, 0);
			}
		}

		// Resource
		else if (info.isResource) {
			MapObjectModel currentItem = Game.getWorldManager().getItem(x, y);
			MapObjectModel currentRessource = Game.getWorldManager().getResource(x, y);
			if (currentRessource != null && currentRessource.getInfo().equals(info)) {
				Log.error("Build item: already exist on this area");
				return null;
			} else if (currentItem != null) {
				Log.error("JobManager: add build on non null item");
				return null;
			} else {
				item = Game.getWorldManager().putObject(info, x, y, 0, 0);
			}
		}

		return addBuild(item);
	}

	/**
	 * Looking for best job to fit characters
	 * 
	 * @param character
	 */
	public void assignJob(CharacterModel character) {

		// Priority jobs
		if (assignPriorityJob(character) && character.getJob() != null) {
			Log.debug("assign priority job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
			return;
		}

		// Regular jobs
        if (assignRegularJob(character) && character.getJob() != null) {
            Log.debug("assign regular job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
            return;
        }

        if (assignFailJob(character) && character.getJob() != null) {
            Log.debug("assign failed job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
            return;
        }

		// Joy jobs
		if (assignJoyJob(character) && character.getJob() != null) {
			Log.debug("assign joy job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
			return;
		}
	}

	public void removeJob(BaseJobModel job) {
        Log.debug("remove job: " + job.getLabel() + " (" + job.getReasonString() + ")");

		if (job.getCharacter() != null) {
			job.quit(job.getCharacter());
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

	public void	addJob(BaseJobModel job) {
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
		BaseJobModel job = JobDump.create(item);
		addJob(job);
	}

	/**
	 * Create joy job for list
	 *
	 * @param character
	 * @return
	 */
	private boolean assignJoyJob(CharacterModel character) {
		Collections.shuffle(_joys);
		for (CharacterCheck jobCheck: _joys) {
			if (jobCheck.check(character)) {
				BaseJobModel job = jobCheck.create(character);
				if (job != null) {
					assignJobToCharacter(job, character);
					if (character.getJob() != job) {
						Log.error("Fail to assign job");
					}
					return true;
				}
			}
		}

		return false;
	}

	/**
     * Create priority job for list (eat / sleep / getRoom oxygen / move to temperate area)
     *
     * @param character
     * @return
     */
	private boolean assignPriorityJob(CharacterModel character) {
		for (CharacterCheck jobCheck: _priorities) {
			if (jobCheck.need(character) && jobCheck.check(character)) {
				BaseJobModel job = jobCheck.create(character);
				if (job != null) {
                    assignJobToCharacter(job, character);
					if (character.getJob() != job) {
						Log.error("Fail to assign job");
					}
					return true;
				}
			}
		}

		return false;
	}

    /**
     * Get job from _queue matching characters talents
     *
     * @param character
     */
    // TODO: one pass + onCheck profession
    private boolean assignRegularJob(CharacterModel character) {
        int x = character.getX();
        int y = character.getY();
        int bestDistance = Integer.MAX_VALUE;
        BaseJobModel bestJob = null;

        // Regular jobs
        for (CharacterModel.TalentEntry talent: character.getTalents()) {
            for (BaseJobModel job: _jobs) {
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
				if (character.getJob() != bestJob) {
					Log.error("Fail to assign job");
				}
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
        BaseJobModel bestJob = null;

        for (BaseJobModel job: _jobs) {
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
     * Assign and start job for designed characters
     *
     * @param job
     * @param character
     */
    private void assignJobToCharacter(BaseJobModel job, CharacterModel character) {
        job.setStatus(JobStatus.RUNNING);
        job.start(character);
    }

    public BaseJobModel createGatherJob(int x, int y) {
		Log.debug("gather: " + x + " x " + y);

		ResourceModel res = Game.getWorldManager().getResource(x, y);
		if (res == null) {
			return null;
		}

		BaseJobModel job = JobGather.create(res);
		return job;
	}

	public BaseJobModel createMiningJob(int x, int y) {
		ResourceModel res = Game.getWorldManager().getResource(x, y);
		if (res == null) {
			return null;
		}

		BaseJobModel job = JobMining.create(res);
		return job;
	}

	public void addGatherJob(int x, int y) {
		BaseJobModel job = createGatherJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public void addMineJob(int x, int y) {
		BaseJobModel job = createMiningJob(x, y);
		if (job != null) {
			addJob(job);
		}
	}

	public BaseJobModel addStoreJob(CharacterModel character) {
//		BaseJob job = JobHaul.onCreate(characters);
//		if (job != null) {
//			addJob(job);
//		}
//		return job;
        throw new RuntimeException("not implemented");
	}

	public BaseJobModel addUseJob(MapObjectModel item) {
		BaseJobModel job = JobUse.create(item);
		if (job != null) {
			addJob(job);
		}
		return job;
	}

	public void addJob(BaseJobModel job, CharacterModel character) {
		addJob(job);
		if (job != null) {
			job.start(character);
		}
	}

	// Remove finished jobs
	public void cleanJobs() {
		if (_toRemove.isEmpty() == false) {
			_jobs.removeAll(_toRemove);
			_toRemove.clear();
		}
	}

	public void close(BaseJobModel job) {
		Log.debug("Job close: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);
		if (job.getCharacter() != null) {
			job.quit(job.getCharacter());
		}

		job.close();

		removeJob(job);
	}

	public void close(BaseJobModel job, JobAbortReason reason) {
		Log.debug("Job close: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);
		if (job.getCharacter() != null) {
            job.quit(job.getCharacter());
		}

		removeJob(job);
	}

    public void quit(BaseJobModel job) {
        if (job != null) {
			if (job.getCharacter() != null) {
				job.quit(job.getCharacter());
            }
			job.start(null);

            if (!job.canBeResume()) {
                close(job);
            }
        }
    }

	public void quit(BaseJobModel job, JobAbortReason reason) {
		Log.debug("Job quit: " + job.getId());

		job.setStatus(JobStatus.ABORTED);
		job.setFail(reason, MainRenderer.getFrame());

		if (job.getCharacter() != null) {
			job.quit(job.getCharacter());
			job.start(null);
		}

		// Remove characters lock from item
		if (job.getItem() != null && job.getItem().getOwner() == job.getCharacter()) {
			job.getItem().setOwner(null);
		}

		// Abort because path to item is blocked
		if (reason == JobAbortReason.BLOCKED) {
			if (job.getItem() != null) {
				job.getItem().setBlocked(Game.getUpdate());
				if (!job.canBeResume()) {
					removeJob(job);
				}
			}
		}

		// Abort because characters inventory is full
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

		// Job is USE / USE_INVENTORY / MOVE / TAKE / STORE / REFILL onAction, don't resume
		if (!job.canBeResume()) {
			removeJob(job);
			return;
		}

		// Regular job, reset
	}

	@Override
	public void onAddConsumable(ConsumableModel consumable) {
		for (BaseJobModel job: _jobs) {
			if (job instanceof JobCraft) {
				((JobCraft)job).addConsumable(consumable);
			}
		}
	}

	@Override
	public void onRemoveConsumable(ConsumableModel consumable){
		for (BaseJobModel job: _jobs) {
			if (job instanceof JobCraft) {
				((JobCraft)job).removeConsumable(consumable);
			}
		}
	}

	public List<CharacterCheck> getJoys() {
		return _joys;
	}
}

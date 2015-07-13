package org.smallbox.faraway.game.manager;

import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.check.CheckCharacterOxygen;
import org.smallbox.faraway.game.model.check.CheckCharacterUse;
import org.smallbox.faraway.game.model.check.character.CheckCharacterExhausted;
import org.smallbox.faraway.game.model.check.character.CheckCharacterHungry;
import org.smallbox.faraway.game.model.check.joy.CheckJoyWalk;
import org.smallbox.faraway.game.model.check.old.CharacterCheck;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.BaseJobModel.JobAbortReason;
import org.smallbox.faraway.game.model.job.BaseJobModel.JobStatus;
import org.smallbox.faraway.game.model.job.JobCraft;
import org.smallbox.faraway.game.model.job.JobHaul;
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
	private final CharacterCheck 				_bedCheck;
	private int 								_nbVisibleJob;

	public JobManager() {
		Log.debug("JobManager");

		_self = this;
		_jobs = new LinkedBlockingQueue<>();
		_toRemove = new ArrayList<>();
        _updateInterval = 10;

        _priorities = new ArrayList<>();
		_priorities.add(new CheckCharacterOxygen());
        _priorities.add(new CheckCharacterUse());
        _priorities.add(new CheckCharacterExhausted());
		_priorities.add(new CheckCharacterHungry());
//		_priorities.add(new CheckCharacterJoyDepleted());

		_bedCheck = new CheckCharacterExhausted();

		_joys = new ArrayList<>();
//		_joys.add(new CheckJoyTalk());
		_joys.add(new CheckJoyWalk());
		_joys.add(new CheckJoyItem());
//		_joys.add(new CheckJoySleep());

        Log.debug("JobManager done");
	}

    @Override
    protected void onUpdate(int tick) {
        cleanJobs();

        // Create haul jobs
        _jobs.stream().filter(job -> job instanceof JobHaul).forEach(job -> ((JobHaul)job).getItemAround());
        Game.getWorldManager().getConsumables().stream().filter(consumable -> consumable.getHaul() == null && !consumable.inValidStorage()).forEach(consumable ->
                addJob(JobHaul.create(consumable)));

        // Remove invalid job
        _jobs.stream().filter(job -> job.getReason() == JobAbortReason.INVALID).forEach(this::removeJob);
    }

	public Collection<BaseJobModel> getJobs() { return _jobs; };

	/**
	 * Looking for best job to fit characters
	 * 
	 * @param character
	 */
	public void assign(CharacterModel character) {
		int timetable = character.getTimetable().get(Game.getInstance().getHour());

		// Priority jobs
		if (assignBestPriority(character) && character.getJob() != null) {
			Log.debug("assign priority job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
			return;
		}

		// Sleep time
		if (timetable == 1) {
			if (assignBestSleep(character)) {
				Log.debug("assign sleep job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
				return;
			}
		}

		// Free and regular jobs
		if (timetable == 0 || timetable == 3) {
			// Check joy depleted
			if (timetable == 0) {
				if (assignBestJoy(character, false)) {
					Log.debug("assign joy job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
					return;
				}
			}

			if (assignBestRegular(character) && character.getJob() != null) {
				Log.debug("assign regular job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
				return;
			}

			if (assignFailedJob(character) && character.getJob() != null) {
				Log.debug("assign failed job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
				return;
			}
		}

		// Free time
		if (timetable == 2) {
			if (assignBestJoy(character, true) && character.getJob() != null) {
				Log.debug("assign joy job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
				return;
			}
		}
	}

	private boolean assignBestSleep(CharacterModel character) {
		if (_bedCheck.check(character)) {
			BaseJobModel job = _bedCheck.create(character);
			if (job != null) {
				assignJob(character, job);
				if (character.getJob() != job) {
					Log.error("Fail to assign job");
				}
				return true;
			}
		}
		return false;
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

        job.close();

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

	/**
	 * Create joy job for list
	 *
	 * @param character
	 * @return
	 */
	private boolean assignBestJoy(CharacterModel character, boolean force) {
		Collections.shuffle(_joys);
		for (CharacterCheck jobCheck: _joys) {
			if ((force || jobCheck.need(character)) && jobCheck.check(character)) {
				BaseJobModel job = jobCheck.create(character);
				if (job != null) {
					assignJob(character, job);
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
	private boolean assignBestPriority(CharacterModel character) {
		for (CharacterCheck jobCheck: _priorities) {
			if (jobCheck.need(character) && jobCheck.check(character)) {
				BaseJobModel job = jobCheck.create(character);
				if (job != null) {
                    assignJob(character, job);
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
    private boolean assignBestRegular(CharacterModel character) {
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
                assignJob(character, bestJob);
				if (character.getJob() != bestJob) {
					Log.error("Fail to assign job");
				}
                return true;
            }
        }

        return false;
    }

    /**
     * Assign and start job for designed characters
     *
     * @param job
     * @param character
     */
    private void assignJob(CharacterModel character, BaseJobModel job) {
        job.setStatus(JobStatus.RUNNING);
        job.start(character);
    }

    /**
     * Assign failed job
     *
     * @param character
     * @return
     */
    private boolean assignFailedJob(CharacterModel character) {
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
            assignJob(character, bestJob);
            return true;
        }

        return false;
    }

	public void addJob(BaseJobModel job, CharacterModel character) {
		addJob(job);
		if (job != null) {
			job.start(character);
		}
	}

	// Remove finished jobs
	public void cleanJobs() {
		if (!_toRemove.isEmpty()) {
			_jobs.removeAll(_toRemove);
			_toRemove.clear();
		}
	}

	public void closeJob(BaseJobModel job) {
		Log.debug("Job close: " + job.getId());

		job.setStatus(JobStatus.COMPLETE);
		if (job.getCharacter() != null) {
			job.quit(job.getCharacter());
		}

		removeJob(job);
	}

    public void quitJob(BaseJobModel job) {
        if (job != null) {
			if (job.getCharacter() != null) {
				job.quit(job.getCharacter());
            }
			job.start(null);

            if (!job.canBeResume()) {
                closeJob(job);
            }
        }
    }

	public void quitJob(BaseJobModel job, JobAbortReason reason) {
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
}

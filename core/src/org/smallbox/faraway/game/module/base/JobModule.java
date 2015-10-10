package org.smallbox.faraway.game.module.base;

import org.smallbox.faraway.GetComponentJob;
import org.smallbox.faraway.data.serializer.SerializerInterface;
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
import org.smallbox.faraway.game.model.job.BaseBuildJobModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.BaseJobModel.JobAbortReason;
import org.smallbox.faraway.game.model.job.BaseJobModel.JobStatus;
import org.smallbox.faraway.game.model.job.CheckJoyItem;
import org.smallbox.faraway.game.model.job.JobHaul;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.util.Constant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobModule extends GameModule {
    private List<CharacterCheck> 			_joys;
    private List<CharacterCheck>  		    _priorities;
    private BlockingQueue<BaseJobModel> 	_jobs;
    private List<BaseJobModel>			    _toRemove;
    private CharacterCheck 				    _bedCheck;
    private int 							_nbVisibleJob;

    @Override
    public void onLoaded() {
        printDebug("JobModule");

        ModuleHelper.setJobModule(this);

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

        printDebug("JobModule done");
    }

    @Override
    protected void onUpdate(int tick) {
        cleanJobs();

        // Create Get Components jobs
        ModuleHelper.getWorldModule().getItems().stream().filter(item -> !item.isComplete())
                .forEach(item -> item.getComponents().stream().filter(component -> component.currentQuantity < component.neededQuantity && component.job == null)
                        .forEach(component -> _jobs.add(new GetComponentJob(item, component))));

        // Create Build jobs
        ModuleHelper.getWorldModule().getItems().stream().filter(item -> !item.isComplete()).filter(item -> item.hasAllComponents() && item.getBuildJob() == null)
                .forEach(item -> _jobs.add(new BuildJob(item)));

        // Create haul jobs
        _jobs.stream().filter(job -> job instanceof JobHaul).forEach(job -> ((JobHaul)job).getItemAround());
        ModuleHelper.getWorldModule().getConsumables().stream().filter(consumable -> consumable.getHaul() == null && !consumable.inValidStorage()).forEach(consumable ->
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
            printDebug("assign priority job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
            return;
        }

        // Sleep time
        if (timetable == 1) {
            if (assignBestSleep(character)) {
                printDebug("assign sleep job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
                return;
            }
        }

        // Free and regular jobs
        if (timetable == 0 || timetable == 3) {
            // Check joy depleted
            if (timetable == 0) {
                if (assignBestJoy(character, false)) {
                    printDebug("assign joy job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
                    return;
                }
            }

            if (assignBestRegular(character) && character.getJob() != null) {
                printDebug("assign regular job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
                return;
            }

            if (assignFailedJob(character) && character.getJob() != null) {
                printDebug("assign failed job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
                return;
            }
        }

        // Free time
        if (timetable == 2) {
            if (assignBestJoy(character, true) && character.getJob() != null) {
                printDebug("assign joy job (" + character.getInfo().getName() + " -> " + character.getJob().getLabel() + ")");
                return;
            }
        }
    }

    @Override
    public boolean isMandatory() {
        return true;
    }

    private boolean assignBestSleep(CharacterModel character) {
        if (_bedCheck.check(character)) {
            BaseJobModel job = _bedCheck.create(character);
            if (job != null) {
                assignJob(character, job);
                if (character.getJob() != job) {
                    printError("Fail to assign job");
                }
                return true;
            }
        }
        return false;
    }

    public void removeJob(BaseJobModel job) {
        printDebug("remove job: " + job.getLabel() + " (" + job.getReasonString() + ")");

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
            printError("Trying to add null or already existing job to JobModule");
            return;
        }

        if (job.isVisibleInUI()) {
            _nbVisibleJob++;
        }

        printDebug("add job: " + job.getLabel());

        _jobs.add(job);

        Game.getInstance().notify(observer -> observer.onJobCreate(job));
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
                        printError("Fail to assign job");
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
                        printError("Fail to assign job");
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
                    printError("Fail to assign job");
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
        printDebug("Job close: " + job.getId());

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
            if (!job.canBeResume()) {
                closeJob(job);
            }
        }
    }

    public void quitJob(BaseJobModel job, JobAbortReason reason) {
        printDebug("Job quit: " + job.getId());

        job.setStatus(JobStatus.WAITING);

        job.setFail(reason, MainRenderer.getFrame());

        if (job.getCharacter() != null) {
            job.quit(job.getCharacter());
        }

        // Remove characters lock from item
        if (job.getItem() != null && job.getItem().getOwner() == job.getCharacter()) {
            job.getItem().setOwner(null);
        }

        // Abort because path to item is blocked
        if (reason == JobAbortReason.BLOCKED) {
            return;
        }

        // Job is invalid, don't resume
        if (reason == JobAbortReason.INVALID) {
            job.setStatus(JobStatus.ABORTED);
            removeJob(job);
            return;
        }

        // Job is USE / USE_INVENTORY / MOVE / TAKE / STORE / REFILL onAction, don't resume
        if (!job.canBeResume()) {
            job.setStatus(JobStatus.ABORTED);
            removeJob(job);
            return;
        }

        // Regular job, reset
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable) {
        for (BaseJobModel job: _jobs) {
            if (job instanceof BaseBuildJobModel) {
                ((BaseBuildJobModel)job).addConsumable(consumable);
            }
        }
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable){
        for (BaseJobModel job: _jobs) {
            if (job instanceof BaseBuildJobModel) {
                ((BaseBuildJobModel)job).removeConsumable(consumable);
            }
        }
    }

    public SerializerInterface getSerializer() {
        return new JobModuleSerializer(this);
    }

}

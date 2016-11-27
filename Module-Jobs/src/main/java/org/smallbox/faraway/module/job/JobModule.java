package org.smallbox.faraway.module.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.serializer.GameSerializer;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.check.joy.CheckJoyWalk;
import org.smallbox.faraway.core.game.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.game.module.job.model.BuildJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel.JobAbortReason;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel.JobStatus;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobModule extends GameModule<JobModuleObserver> {
    private BlockingQueue<JobModel>     _jobs = new LinkedBlockingQueue<>();
    private List<CharacterCheck>        _joys;
    private List<CharacterCheck>        _priorities;
    private List<CharacterCheck>        _sleeps;

    @Override
    protected void onGameCreate(Game game) {
        _priorities = new ArrayList<>();
//        _priorities.addSubJob(new CheckCharacterEntertainmentDepleted());


        _joys = new ArrayList<>();
//        _joys.addSubJob(new CheckEntertainmentTalk());
        _joys.add(new CheckJoyWalk());
//        _joys.addSubJob(new CheckEntertainmentSleep());

        _sleeps = new ArrayList<>();
    }

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _jobs.removeIf(job -> job.getReason() == JobAbortReason.INVALID);
        _jobs.removeIf(JobModel::isFinish);

        if (tick % 10 == 0) {
            // Create new job
            _jobs.stream().filter(job -> !job.isCreate()).forEach(JobModel::create);

            // Run auto job
            _jobs.stream().filter(job -> job.isAuto() && job.check(null)).forEach(job -> job.action(null));
        }
    }

    public Collection<JobModel> getJobs() { return _jobs; };

    /**
     * Looking for best job to fit characters
     *
     * @param character
     */
    public void assign(CharacterModel character) {
        int timetable = character.getTimetable().get(Application.gameManager.getGame().getHour());

        // Priority jobs
        JobModel job = getBestPriority(character);

        // Sleep jobs (sleep time)
        if (job == null && timetable == 1) {
            job = getBestSleep(character, true);
        }

        // Entertainment jobs (auto time)
        if (job == null && timetable == 0) {
            job = getBestEntertainment(character, false);
        }

        // Regular jobs (auto and work time)
        if (job == null && (timetable == 0 || timetable == 3)) {
            job = getBestRegular(character);
        }

        // Failed jobs (auto and work time)
        if (job == null && (timetable == 0 || timetable == 3)) {
            job = getFailedJob(character);
        }

        // Entertainment jobs (free time)
        if (timetable == 2) {
            job = getBestEntertainment(character, true);
        }

        if (job != null) {
            assign(character, job);
        }
    }

    private JobModel getBestSleep(CharacterModel character, boolean force) {
        for (CharacterCheck check: _sleeps) {
            if ((force || check.checkJobNeeded(character)) && check.checkJobLaunchable(character)) {
                return check.createJob(character);
            }
        }
        return null;
    }

    private void assign(CharacterModel character, JobModel job) {
        if (job != null) {
            assignJob(character, job);
            if (character.getJob() != job) {
                printError("Fail to assign job");
            } else {
                printDebug("assign job (" + character.getPersonals().getName() + " -> " + character.getJob().getLabel() + ")");
            }
        } else {
            printError("Try to assign null job");
        }
    }

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    public void    addJob(JobModel job) {
        if (job == null || _jobs.contains(job)) {
            printError("Trying to addSubJob null or already existing job to JobModule");
            return;
        }

        printDebug("addSubJob job: " + job.getLabel());

        _jobs.add(job);

        Application.notify(observer -> observer.onJobCreate(job));
    }

    /**
     * Create entertainment job for list
     *
     * @param character
     * @return
     */
    private JobModel getBestEntertainment(CharacterModel character, boolean force) {
        Collections.shuffle(_joys);
        for (CharacterCheck check: _joys) {
            if ((force || check.checkJobNeeded(character)) && check.checkJobLaunchable(character)) {
                return check.createJob(character);
            }
        }
        return null;
    }

    /**
     * Create priority job for list (eat / sleep / getRoom oxygen / move to temperate org.smallbox.faraway.core.game.module.room.model)
     *
     * @param character
     * @return
     */
    private JobModel getBestPriority(CharacterModel character) {
        JobModel job = null;
        for (CharacterCheck jobCheck: _priorities) {
            if (job == null && jobCheck.checkJobNeeded(character) && jobCheck.checkJobLaunchable(character)) {
                job = jobCheck.createJob(character);
            }
        }
        return job;
    }

    /**
     * Get job from _queue matching characters talents
     *
     * @param character
     */
    // TODO: one pass + onCheck profession
    private JobModel getBestRegular(CharacterModel character) {
        int bestDistance = Integer.MAX_VALUE;
        JobModel bestJob = null;

        // Regular jobs
        for (CharacterTalentExtra.TalentEntry talent: character.getTalents().getAll()) {
            if (bestJob == null) {
                for (JobModel job: _jobs) {
                    if (job.isActive() && !job.isAuto()) {
                        Log.debug("Check best regular: " + job.getLabel());
                        ParcelModel parcel = job.getTargetParcel() != null ? job.getTargetParcel() : job.getJobParcel();
                        if (talent.type == job.getTalentNeeded() && !job.isFinish() && job.getCharacter() == null && job.getFail() <= 0) {
                            int distance = WorldHelper.getApproxDistance(character.getParcel(), parcel);
                            if (distance < bestDistance && job.check(character)) {
                                bestJob = job;
                                bestDistance = distance;
                            }
                        }
                    }
                }
            }
        }

        return bestJob;
    }

    /**
     * Assign and start job for designed characters
     *
     * @param job
     * @param character
     */
    private void assignJob(CharacterModel character, JobModel job) {
        job.setStatus(JobStatus.RUNNING);
        job.start(character);
    }

    /**
     * Assign failed job
     *
     * @param character
     * @return
     */
    private JobModel getFailedJob(CharacterModel character) {
        int x = character.getParcel().x;
        int y = character.getParcel().y;
        int bestDistance = Integer.MAX_VALUE;
        JobModel bestJob = null;

        for (JobModel job: _jobs) {
            if (job.getCharacter() == null && job.getFail() > 0) {
                if (job.getReason() == JobAbortReason.BLOCKED && job.getBlocked() < Application.gameManager.getGame().getTick() + Constant.DELAY_TO_RESTART_BLOCKED_JOB) {
                    continue;
                }

                int distance = Math.abs(x - job.getTargetParcel().x) + Math.abs(y - job.getTargetParcel().y);
                if (distance < bestDistance && job.check(character)) {
                    bestJob = job;
                    bestDistance = distance;
                }
            }
        }

        return bestJob;
    }

    public void addJob(JobModel job, CharacterModel character) {
        addJob(job);
        if (job != null) {
            job.start(character);
        }
    }

    public void quitJob(JobModel job, JobAbortReason reason) {
        if (job != null) {
            printDebug("Job quit: " + job.getId());

        } else {
            Log.info("[ERROR] Quit null job");
        }
    }

    @Override
    public void onCancelJobs(ParcelModel parcel, Object object) {
        for (JobModel job : _jobs) {
            if (job.isOpen()) {
                if (object == null && job.getJobParcel() == parcel) {
                    job.cancel();
                }
//                if (object != null && job instanceof HaulJob && ((HaulJob) job).getBuildItem() == object) {
//                    job.cancel();
//                }
                if (object != null && job instanceof BuildJob && ((BuildJob) job).getBuildItem() == object) {
                    job.cancel();
                }
            }
        }
    }

    public GameSerializer getSerializer() {
        return null;
//        return new JobModuleSerializer(this);
    }

    public int getModulePriority() {
        return Constant.MODULE_JOB_PRIORITY;
    }

    public void addPriorityCheck(CharacterCheck check) {
        _priorities.add(check);
    }

    public void addJoyCheck(CharacterCheck check) {
        _joys.add(check);
    }

    public void addSleepCheck(CharacterCheck check) {
        _priorities.add(check);
    }

    public void addJobs(List<JobModel> jobs) {
        jobs.forEach(this::addJob);
    }
}

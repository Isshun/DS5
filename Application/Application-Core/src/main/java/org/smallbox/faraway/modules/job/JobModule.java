package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.CharacterFreeTimeExtra;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.BasicHaulJob;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.itemFactory.BasicCraftJob;
import org.smallbox.faraway.modules.job.JobModel.JobAbortReason;
import org.smallbox.faraway.modules.job.JobModel.JobStatus;
import org.smallbox.faraway.modules.plant.BasicHarvestJob;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class JobModule extends GameModule<JobModuleObserver> {
    private BlockingQueue<JobModel>         _unordonnedJobs = new LinkedBlockingQueue<>();
    private BlockingQueue<JobModel>         _jobs = new LinkedBlockingQueue<>();
    private List<CharacterCheck>            _joys;
    private List<CharacterCheck>            _priorities;
    private List<CharacterCheck>            _sleeps;
    private Map<CharacterModel, Integer>    _characterInnactiveDuration = new ConcurrentHashMap<>();

    @BindModule
    private CharacterModule characterModule;

    @BindModule
    private ConsumableModule consumableModule;

    @Override
    public void onGameCreate(Game game) {
        _priorities = new ArrayList<>();
//        _priorities.addSubJob(new CheckCharacterEntertainmentDepleted());


        _joys = new ArrayList<>();
//        _joys.addSubJob(new CheckEntertainmentTalk());
//        _joys.add(new CheckJoyWalk());
//        _joys.addSubJob(new CheckEntertainmentSleep());

        _sleeps = new ArrayList<>();
    }

    @Override
    protected void onModuleUpdate(Game game) {
        _jobs.removeIf(job -> job.getReason() == JobAbortReason.INVALID);
        _jobs.removeIf(JobModel::isClose);

        // Run auto job
        double hourInterval = getTickInterval() * game.getTickPerHour();
        _jobs.stream().filter(job -> job.isAuto() && job.check(null)).forEach(job -> job.action(null, hourInterval));

        // Assign job to inactive character
        characterModule.getCharacters().stream()
                .filter(CharacterModel::isFree)
                .forEach(this::assign);

    }

    public Collection<JobModel> getJobs() { return _jobs; }

    public <T extends JobModel> Stream<T> getJobs(Class<T> cls) {
        return _jobs.stream().filter(cls::isInstance).map(cls::cast);
    }

//    public <T> Collection<T> getJobs(T cls) {
//        return _jobs.stream()
//                .filter(job -> job.getClass() == cls)
//                .map(cls::cast)
//                .collect(Collectors.toList());
//    }
//
    /**
     * Looking for best job to fit characters
     *
     * @param character
     */
    public void assign(CharacterModel character) {

        fixCharacterInventory();

        // La valeur "innactive duration" sert à temporiser un certain nombre de tick avant l'assignation d'un nouveau
        // job afin de laisser le temps aux autres modules d'effectuer leur update et de peut-être créer des job plus
        // adapté pour le personnage.
        // Par exemple: si le personnage vient de terminer un craft le StorageModule va lancer un BasicStoreJob sur le
        // consomable nouvellement créé, job qui aura des chances d'être assigné à ce personnage car étant le plus proche.
        if (_characterInnactiveDuration.getOrDefault(character, 0) > 10) {
            _characterInnactiveDuration.put(character, 0);

            _jobs.stream()
                    .filter(job -> job.getCharacter() == null)
                    .filter(job -> job.getStatus() == JobStatus.JOB_WAITING || job.getStatus() == JobStatus.JOB_INITIALIZED)
                    .sorted(Comparator.comparingInt(j -> WorldHelper.getApproxDistance(j.getStartParcel(), character.getParcel())))
                    .findFirst()
                    .ifPresent(job -> assign(character, job));

            // Aucun job n'a pu être assigné
            // Assign freetime job
            if (character.getJob() == null && character.hasExtra(CharacterFreeTimeExtra.class)) {
                character.getExtra(CharacterFreeTimeExtra.class).getTypes().stream().findAny().ifPresent(type -> {
                    try {
                        assign(character, createJob(type.getConstructor(CharacterModel.class).newInstance(character)));
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        Log.error(e);
                    }
                });
            }

        }

        if (character.getJob() == null) {
            _characterInnactiveDuration.put(character, _characterInnactiveDuration.getOrDefault(character, 0) + 1);
        }

//        int timetable = character.getTimetable().get(Application.gameManager.getGame().getHour());
//
//        // Priority jobs
//        JobModel job = getBestPriority(character);
//        JobModel job = getBestPriority(character);
//
//        // Sleep jobs (sleep time)
//        if (job == null && timetable == 1) {
//            job = getBestSleep(character, true);
//        }
//
//        // Entertainment jobs (auto time)
//        if (job == null && timetable == 0) {
//            job = getBestEntertainment(character, false);
//        }
//
//        // Regular jobs (auto and work time)
//        if (job == null && (timetable == 0 || timetable == 3)) {
//            job = getBestRegular(character);
//        }
//
//        // Failed jobs (auto and work time)
//        if (job == null && (timetable == 0 || timetable == 3)) {
//            job = getFailedJob(character);
//        }
//
//        // Entertainment jobs (free time)
//        if (timetable == 2) {
//            job = getBestEntertainment(character, true);
//        }
//
//        if (job != null) {
//            assign(character, job);
//        }
    }

    private void fixCharacterInventory() {
        characterModule.getCharacters().stream()
                .filter(character -> character.getJob() == null && !character.getInventory().isEmpty())
                .forEach(character -> {
                    Log.warning(getName() + " have item in inventory without job");
                    character.getInventory().forEach((itemInfo, quantity) ->
                            consumableModule.addConsumable(itemInfo, quantity, character.getParcel()));
                    character.getInventory().clear();
                });
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
            _characterInnactiveDuration.put(character, 0);
            assignJob(character, job);
            if (character.getJob() == null || character.getJob() != job) {
                printError("Fail to assign job");
            } else {
//                printDebug("assign job (" + character.getPersonals().getName() + " -> " + character.getJob().getLabel() + ")");
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

        if (job.getStatus() != JobStatus.JOB_INITIALIZED) {
            throw new GameException(JobModule.class, "Job status must be JOB_INITIALIZED");
        }

        printDebug("addSubJob job: " + job.getLabel());

        _jobs.add(job);
        sortJobs();

        Application.notify(observer -> observer.onJobCreate(job));
    }

    private void sortJobs() {
        _unordonnedJobs.clear();
        _unordonnedJobs.addAll(_jobs);
        _jobs.clear();

        Arrays.asList(BasicCraftJob.class, BasicHarvestJob.class, BasicHaulJob.class)
                .forEach(cls ->
                        _unordonnedJobs.stream()
                                .filter(job -> job.getClass() == cls)
                                .forEach(job -> {
                                    _jobs.add(job);
                                    _unordonnedJobs.remove(job);
                                }));

        _jobs.addAll(_unordonnedJobs);
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
     * Create priority job for list (eat / sleep / getRoom oxygen / move to temperate org.smallbox.faraway.core.module.room.model)
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
     * Retourne la meilleur tache disponible pour le personnage
     *
     * @param character
     */
    // TODO: one pass + onCheck profession
    private JobModel getBestRegular(CharacterModel character) {

        // Regular jobs
        if (character.hasExtra(CharacterSkillExtra.class)) {
            for (CharacterSkillExtra.SkillEntry skill : character.getExtra(CharacterSkillExtra.class).getAll()) {
                int bestDistance = Integer.MAX_VALUE;
                JobModel bestJob = null;

                for (JobModel job : _jobs) {
                    if (!job.isAuto()) {
                        Log.debug("Check best regular: " + job.getLabel());
                        ParcelModel parcel = job.getTargetParcel() != null ? job.getTargetParcel() : job.getJobParcel();
                        if (skill.type == job.getSkillNeeded() && !job.isClose() && job.getCharacter() == null && job.getFail() <= 0) {
                            int distance = WorldHelper.getApproxDistance(character.getParcel(), parcel);
                            if (distance < bestDistance && job.check(character)) {
                                bestJob = job;
                                bestDistance = distance;
                            }
                        }
                    }
                }

                if (bestJob != null) {
                    return bestJob;
                }
            }
        }

        return null;
    }

    /**
     * Assign and start job for designed characters
     *
     * @param job
     * @param character
     */
    private void assignJob(CharacterModel character, JobModel job) {
        job.start(character);
    }

    /**
     * Assign failed job
     *
     * @param character
     * @return
     */
    private JobModel getFailedJob(CharacterModel character) {
        if (character.getParcel() != null) {
            int x = character.getParcel().x;
            int y = character.getParcel().y;
            int bestDistance = Integer.MAX_VALUE;
            JobModel bestJob = null;

            for (JobModel job : _jobs) {
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
        return null;
    }

    public void quitJob(JobModel job, JobAbortReason reason) {
        if (job != null) {
            printDebug("Job quit: " + job.getId());

        } else {
            Log.info("[ERROR] Quit null job");
        }
    }

    // TODO: utile ?
    @Override
    public void onCancelJobs(ParcelModel parcel, Object object) {
        _jobs.stream().filter(JobModel::isOpen).forEach(job -> {
            if (object == null && job.getJobParcel() == parcel) {
                job.close();
            }
//            if (object != null && job instanceof BuildJob && ((BuildJob) job).getBuildItem() == object) {
//                job.close();
//            }
        });
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

    public boolean hasJob(JobModel job) {
        return _jobs.contains(job);
    }

//    public <T extends JobModel> List<T> getJobs(Class<T> cls) {
//        return _jobs.stream()
//                .filter(cls::isInstance)
//                .map(job -> (T)job)
//                .collect(Collectors.toList());
//    }

    public interface JobInitCallback<T> {
        boolean onInit(T job);
    }

    public <T extends JobModel> T createJob(T job) {
        addJob(job);
        return job;
    }

    public <T extends JobModel> T createJob(Class<T> cls, ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel, JobInitCallback<T> jobInitCallback) {
        try {
            T job = cls.getConstructor(ItemInfo.ItemInfoAction.class, ParcelModel.class).newInstance(itemInfoAction, parcelModel);
            boolean ret = jobInitCallback.onInit(job);
            job.onNewInit();
            if (!ret) {
                job.abort();
                return null;
            }

            if (job.getStartParcel() == null) {
                throw new GameException(JobModule.class, "Job startParcel cannot be null", job);
            }

            if (job.getTargetParcel() == null) {
                throw new GameException(JobModule.class, "Job targetParcel cannot be null", job);
            }

            addJob(job);
            return job;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new GameException(JobModule.class, e, "Unable to create job");
        }
    }

    public void removeJob(JobModel job) {
        Log.debug("Remove job " + job + ", status: " + job.getStatus());

        if (job.getCharacter() != null) {
            job.close();
        }
        job.close();
        _jobs.remove(job);
    }
}

package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@GameObject
public class JobModule extends GameModule<JobModuleObserver> {
    private BlockingQueue<JobModel>         _unordonnedJobs = new LinkedBlockingQueue<>();
    private BlockingQueue<JobModel>         _jobs = new LinkedBlockingQueue<>();
    private Map<CharacterModel, Integer>    _characterInnactiveDuration = new ConcurrentHashMap<>();

    @BindComponent
    private CharacterModule characterModule;

    @BindComponent
    private ConsumableModule consumableModule;

    @Inject
    private ApplicationConfigService applicationConfigService;

    @Override
    protected void onModuleUpdate(Game game) {

        // Check all jobs
        _jobs.forEach(JobModel::check);
        _jobs.forEach(JobModel::update);

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

    /**
     * Looking for best job to fit characters
     *
     * @param character
     */
    public void assign(CharacterModel character) {

        // La valeur "innactive duration" sert à temporiser un certain nombre de tick avant l'assignation d'un nouveau
        // job afin de laisser le temps aux autres modules d'effectuer leur update et de peut-être créer des job plus
        // adapté pour le personnage.
        // Par exemple: si le personnage vient de terminer un craft le StorageModule va lancer un BasicStoreJob sur le
        // consomable nouvellement créé, job qui aura des chances d'être assigné à ce personnage car étant le plus proche.
        if (_characterInnactiveDuration.getOrDefault(character, 0) > 10) {
            _characterInnactiveDuration.put(character, 0);

            List<JobModel> availableJobs = _jobs.stream()
                    .filter(job -> job.getCharacter() == null)
                    .filter(job -> job.getStatus() == JobStatus.JOB_WAITING || job.getStatus() == JobStatus.JOB_INITIALIZED)
                    .sorted(Comparator.comparingInt(j -> WorldHelper.getApproxDistance(j.getStartParcel(), character.getParcel())))
                    .collect(Collectors.toList());

            // Assign regular job
            if (character.hasExtra(CharacterSkillExtra.class)) {
                for (CharacterSkillExtra.SkillEntry skill: character.getExtra(CharacterSkillExtra.class).getAll()) {
                    for (JobModel job: availableJobs) {
                        if ((job.getSkillType() == null || job.getSkillType() == skill.type) && job.checkCharacterAccepted(character)) {
                            assign(character, job);
                            return;
                        }
                    }
                }
            }

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

    }

    private void assign(CharacterModel character, JobModel job) {
        if (job != null) {
            _characterInnactiveDuration.put(character, 0);
            job.start(character);
            if (character.getJob() == null || character.getJob() != job) {
                Log.error(JobModule.class, "Fail to assign job");
            } else {
//                printDebug("assign job (" + character.getPersonals().getName() + " -> " + character.getJob().getLabel() + ")");
            }
        } else {
            Log.error(JobModule.class, "Try to assign null job");
        }
    }

    public void    addJob(JobModel job) {
        if (job == null || _jobs.contains(job)) {
            Log.error(JobModule.class, "Trying to addSubJob null or already existing job to JobModule");
            return;
        }

        if (job.getStatus() != JobStatus.JOB_INITIALIZED) {
            throw new GameException(JobModule.class, "Job status must be JOB_INITIALIZED");
        }

        Log.debug(JobModule.class, "addSubJob job: " + job.getLabel());

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

    public void quitJob(JobModel job, JobAbortReason reason) {
        if (job != null) {
            Log.debug(JobModule.class, "Job quit: " + job.getId());

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
        });
    }

    public int getModulePriority() {
        return Constant.MODULE_JOB_PRIORITY;
    }

    public boolean hasJob(JobModel job) {
        return _jobs.contains(job);
    }

    public ApplicationConfig.ApplicationConfigGameInfo getGameConfig() {
        return applicationConfigService.getGameInfo();
    }

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
                job.close();
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

        job.close();
        _jobs.remove(job);
    }
}

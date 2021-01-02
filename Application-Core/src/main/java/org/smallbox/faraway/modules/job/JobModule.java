package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.job.JobModel.JobAbortReason;
import org.smallbox.faraway.modules.job.JobModel.JobStatus;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

@GameObject
public class JobModule extends GameModule<JobModuleObserver> {
    private BlockingQueue<JobModel>         _unordonnedJobs = new LinkedBlockingQueue<>();
    private BlockingQueue<JobModel>         _jobs = new LinkedBlockingQueue<>();

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private JobOrchestratorModule jobOrchestratorModule;

    @Inject
    private GameTime gameTime;

    @Override
    protected void onModuleUpdate(Game game) {

        // Check all jobs
        _jobs.forEach(JobModel::check);
        _jobs.forEach(JobModel::update);

        _jobs.stream().filter(JobModel::isBlocked).filter(jobModel -> jobModel.getBlocked().isBefore(gameTime.getTime())).forEach(JobModel::unblock);

        _jobs.removeIf(job -> job.getReason() == JobAbortReason.INVALID);
        _jobs.removeIf(JobModel::isClose);

        // Run auto job
        double hourInterval = getTickInterval() * game.getTickPerHour();
        _jobs.stream().filter(job -> job.isAuto() && job.check(null)).forEach(job -> job.action(null, hourInterval));

        // Assign job to inactive character
        jobOrchestratorModule.assign();
    }

    public Collection<JobModel> getJobs() { return _jobs; }

    public <T extends JobModel> Stream<T> getJobs(Class<T> cls) {
        return _jobs.stream().filter(cls::isInstance).map(cls::cast);
    }

    @Deprecated
    public <T extends JobModel> T createJob(Class<T> cls, ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel, JobInitCallback<T> jobInitCallback) {
        try {
            T job = cls.getConstructor(ItemInfo.ItemInfoAction.class, ParcelModel.class).newInstance(itemInfoAction, parcelModel);
            boolean ret = jobInitCallback.onInit(job);
            job.onNewInit();
            if (!ret) {
                job.close();
                return null;
            }

            addJob(job);
            return job;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new GameException(JobModule.class, e, "Unable to create job");
        }
    }

    public void addJob(JobModel job) {
        if (job == null) {
            throw new GameException(JobModule.class, "Cannot create null job", job);
        }

        if (_jobs.contains(job)) {
            throw new GameException(JobModule.class, "Trying to create a job already existing", job);
        }

        if (job.getStartParcel() == null) {
            throw new GameException(JobModule.class, "Job startParcel cannot be null", job);
        }

        if (job.getStatus() != JobStatus.JOB_INITIALIZED) {
            throw new GameException(JobModule.class, "Job status must be JOB_INITIALIZED");
        }

        Log.debug(JobModule.class, "add job: " + job.getLabel());

        job.executeInitTasks();

        _jobs.add(job);

        if (CollectionUtils.isNotEmpty(job.getSubJob())) {
            _jobs.addAll(job.getSubJob());
        }

        sortJobs();

        Application.notify(observer -> observer.onJobCreate(job));
    }

    private void sortJobs() {
        _unordonnedJobs.clear();
        _unordonnedJobs.addAll(_jobs);
        _jobs.clear();

//        Arrays.asList(BasicCraftJob.class, BasicHarvestJob.class, BasicHaulJob.class)
//                .forEach(cls ->
//                        _unordonnedJobs.stream()
//                                .filter(job -> job.getClass() == cls)
//                                .forEach(job -> {
//                                    _jobs.add(job);
//                                    _unordonnedJobs.remove(job);
//                                }));

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

    public JobModel getJob(int jobId) {
        return _jobs.stream().filter(jobModel -> jobModel.getId() == jobId).findFirst().orElse(null);
    }

    public interface JobInitCallback<T> {
        boolean onInit(T job);
    }

    public void removeJob(JobModel job) {
        Log.debug("Remove job " + job + ", status: " + job.getStatus());

        job.close();
        _jobs.remove(job);
    }
}

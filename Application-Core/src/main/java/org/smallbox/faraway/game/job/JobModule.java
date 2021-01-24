package org.smallbox.faraway.game.job;

import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.SuperGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.log.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import static org.smallbox.faraway.game.job.JobAbortReason.INVALID;
import static org.smallbox.faraway.game.job.JobStatus.*;

@GameObject
public class JobModule extends SuperGameModule<JobModel, JobModuleObserver> {
    private final BlockingQueue<JobModel>         _unordonnedJobs = new LinkedBlockingQueue<>();

    @Inject private ConsumableModule consumableModule;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private GameTime gameTime;

    @Override
    protected void onModuleUpdate(Game game) {

        // Check all jobs
        modelList.forEach(job -> {

            if (job.check()) {
                job.update();
            } else {
                job.close(gameTime.now());
            }

        });

        // Put back blocked jobs in waiting status
        modelList.stream().filter(job -> job.hasStatus(JOB_BLOCKED)).filter(job -> job.getBlocked().isBefore(gameTime.now())).forEach(job -> {
            job.setStatus(JOB_WAITING);
            job.setBlockedUntil(null);
        });

        // Remove invalid or completed jobs
        modelList.removeIf(job -> job.hasReason(INVALID) || job.hasStatus(JOB_COMPLETE));

        // Run auto job
        modelList.stream().filter(JobModel::isAuto).forEach(job -> job.action(null, getTickInterval() * game.getTickPerHour(), gameTime.now()));

    }

    public <T extends JobModel> Stream<T> getJobs(Class<T> cls) {
        return modelList.stream().filter(cls::isInstance).map(cls::cast);
    }

    @Deprecated
    public <T extends JobModel> T createJob(Class<T> cls, ItemInfo.ItemInfoAction itemInfoAction, Parcel parcel, JobInitCallback<T> jobInitCallback) {
        try {
            T job = cls.getConstructor(ItemInfo.ItemInfoAction.class, Parcel.class).newInstance(itemInfoAction, parcel);
            boolean ret = jobInitCallback.onInit(job);
            job.onNewInit();
            if (!ret) {
                job.close(gameTime.now());
                return null;
            }

            add(job);
            return job;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new GameException(JobModule.class, e, "Unable to create job");
        }
    }

    @Override
    public void add(JobModel job) {
        if (job == null) {
            throw new GameException(JobModule.class, "Cannot create null job", job);
        }

        if (modelList.contains(job)) {
            throw new GameException(JobModule.class, "Trying to create a job already existing", job);
        }

        if (job.getStatus() != JOB_INITIALIZED) {
            throw new GameException(JobModule.class, "Job status must be JOB_INITIALIZED");
        }

        Log.debug(JobModule.class, "add job: " + job.getLabel());

        job.executeInitTasks();

        super.add(job);

        if (CollectionUtils.isNotEmpty(job.getSubJob())) {
            job.getSubJob().forEach(super::add);
        }

        sortJobs();

        Application.notify(observer -> observer.onJobCreate(job));
    }

    private void sortJobs() {
        _unordonnedJobs.clear();
        _unordonnedJobs.addAll(modelList);
        modelList.clear();

//        Arrays.asList(BasicCraftJob.class, BasicHarvestJob.class, BasicHaulJob.class)
//                .forEach(cls ->
//                        _unordonnedJobs.stream()
//                                .filter(job -> job.getClass() == cls)
//                                .forEach(job -> {
//                                    _jobs.add(job);
//                                    _unordonnedJobs.remove(job);
//                                }));

        modelList.addAll(_unordonnedJobs);
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
    public void onCancelJobs(Parcel parcel, Object object) {
        modelList.stream().filter(JobModel::isOpen).forEach(job -> {
            if (object == null && job.getTargetParcel() == parcel) {
                job.close(gameTime.now());
            }
        });
    }

    public int getModulePriority() {
        return Constant.MODULE_JOB_PRIORITY;
    }

    public boolean hasJob(JobModel job) {
        return modelList.contains(job);
    }

    public JobModel getJob(int jobId) {
        return modelList.stream().filter(jobModel -> jobModel.getId() == jobId).findFirst().orElse(null);
    }

    public interface JobInitCallback<T> {
        boolean onInit(T job);
    }

    @Override
    public void remove(JobModel job) {
        Log.debug("Remove job " + job + ", status: " + job.getStatus());
        job.close(gameTime.now());
        super.remove(job);
    }
}

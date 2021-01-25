package org.smallbox.faraway.game.character;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.module.SuperGameModule2;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.JobStatus;
import org.smallbox.faraway.game.job.JobTask;
import org.smallbox.faraway.game.job.JobTaskReturn;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@GameObject
public class CharacterJobModule extends SuperGameModule2<CharacterModuleObserver> {
    @Inject private CharacterMoveModule characterMoveModule;
    @Inject private CharacterModule characterModule;
    @Inject private GameTime gameTime;

    @Override
    public int getModulePriority() {
        return Constant.MODULE_CHARACTER_PRIORITY;
    }

    @Override
    public void onModuleUpdate(Game game) {
        double hourInterval = getTickInterval() / game.getTickPerHour();

        characterModule.getAll().forEach(character -> {
            Optional.ofNullable(character.getJob()).ifPresent(job -> actionJob(character, job, hourInterval));
        });
    }

    public void actionJob(CharacterModel character, JobModel job, double hourInterval) {

        // Execute les taches à la suite tant que le retour est TASK_COMPLETE
        while (!job._tasks.isEmpty()) {

            // Job can be executed remotely or character is on job's accepted parcels
            if (job.getAcceptedParcels().isEmpty() || job.getAcceptedParcels().contains(character.getParcel())) {
                JobTaskReturn ret = actionTask(job, character, hourInterval, gameTime.now(), job._tasks.peek());
                if (ret != JobTaskReturn.TASK_COMPLETED) {
                    return;
                }
            }

            // Character isn't on job's accepted parcel, move to position
            else {
                CharacterMoveStatus status = characterMoveModule.move(character, job);

                if (status == CharacterMoveStatus.BLOCKED) {
                    job.clearCharacter(character, gameTime.now());
                    job.setStatus(JobStatus.JOB_BLOCKED);
                    job.setBlockedUntil(gameTime.plus(5, TimeUnit.MINUTES));
                }

                return;
            }
        }

        // All tasks has been executed
        job.close(gameTime.now());
    }

    /**
     * Execute les taches présentes dans le job
     */
    public JobTaskReturn actionTask(JobModel job, CharacterModel character, double hourInterval, LocalDateTime currentTime, JobTask jobTask) {
        if (job.isClose()) throw new GameException(JobModel.class, "Cannot call action on finished job");
        if (job.status != JobStatus.JOB_RUNNING) throw new GameException(JobModel.class, "Status must be JOB_RUNNING");

        JobTaskReturn ret = actionTask(job, character, jobTask, hourInterval, currentTime);

        switch (ret) {
            // Task return TASK_COMPLETED_STOP, stop to execute action until next update
            case TASK_COMPLETED_STOP -> job._tasks.poll();

            // Task is complete, take next task
            case TASK_COMPLETED -> job._tasks.poll();

            // Task return TASK_ERROR, immediatly close job
            case TASK_ERROR -> job.close(currentTime);
        }

        return ret;
    }

    /**
     * Execute la tache passé en paramètre
     */
    private JobTaskReturn actionTask(JobModel job, CharacterModel character, JobTask task, double hourInterval, LocalDateTime localDateTime) {
        Log.debug(JobModel.class, "actionTask: (taks: %s, job: %s)", task.label, this);

        if (task.technicalAction != null) {
            task.technicalAction.onExecuteTask(job);
            return task.taskReturn;
        }

        if (task.startTime == null) {
            task.init(localDateTime);
        }

        task.action(character, hourInterval, localDateTime);
        job._lastTaskReturn = task.getStatus(character, hourInterval, localDateTime);
        job._label = task.label;

        Log.debug(JobModel.class, "actionTask return: %s", job._lastTaskReturn);

        return job._lastTaskReturn;
    }

}

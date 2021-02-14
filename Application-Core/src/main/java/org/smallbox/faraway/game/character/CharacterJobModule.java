package org.smallbox.faraway.game.character;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.ThreadManager;
import org.smallbox.faraway.core.module.SuperGameModule2;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.*;
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
    @Inject private ThreadManager threadManager;
    @Inject private JobModule jobModule;
    @Inject private GameTime gameTime;
    @Inject private Game game;

    @OnGameUpdate
    public void onGameUpdate() {
        double hourInterval = threadManager.getTickInterval() / game.getTickPerHour();

        characterModule.getAll().forEach(character -> {
            Optional.ofNullable(character.getJob()).ifPresent(job -> actionJob(character, job, hourInterval));
        });
    }

    public void actionJob(CharacterModel character, JobModel job, double hourInterval) {

        // Execute les taches à la suite tant que le retour est TASK_COMPLETE
        while (!job.getTasks().isEmpty()) {

            // Job can be executed remotely or character is on job's accepted parcels
            if (job.getAcceptedParcels().isEmpty() || job.getAcceptedParcels().contains(character.getParcel())) {
                JobTaskReturn ret = actionTask(job, character, hourInterval, gameTime.now(), job.lastTask());
                if (ret != JobTaskReturn.TASK_COMPLETED) {
                    return;
                }
            }

            // Character isn't on job's accepted parcel, move to position
            else {
                CharacterMoveStatus status = characterMoveModule.move(character, job);

                if (status == CharacterMoveStatus.BLOCKED) {
                    jobModule.clearCharacter(job, character);
                    job.setStatus(JobStatus.JOB_BLOCKED);
                    job.setBlockedUntil(gameTime.plus(5, TimeUnit.MINUTES));
                }

                return;
            }
        }

        // All tasks has been executed
        jobModule.remove(job);
    }

    /**
     * Execute les taches présentes dans le job
     */
    public JobTaskReturn actionTask(JobModel job, CharacterModel character, double hourInterval, LocalDateTime currentTime, JobTask jobTask) {
        if (job.isClose()) throw new GameException(JobModel.class, "Cannot call action on finished job");
        if (job.getStatus() != JobStatus.JOB_RUNNING) throw new GameException(JobModel.class, "Status must be JOB_RUNNING");

        JobTaskReturn ret = actionTask(job, character, jobTask, hourInterval, currentTime);

        switch (ret) {
            // Task return TASK_COMPLETED_STOP, stop to execute action until next update
            case TASK_COMPLETED_STOP -> job.nextTask();

            // Task is complete, take next task
            case TASK_COMPLETED -> job.nextTask();

            // Task return TASK_ERROR, immediatly close job
            case TASK_ERROR -> jobModule.remove(job);
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
        JobTaskReturn taskReturn = task.getStatus(character, hourInterval, localDateTime);
        job.setLabel(task.label);

        Log.debug(JobModel.class, "actionTask return: %s", taskReturn);

        return taskReturn;
    }

    public void clearJob(CharacterModel character, JobModel job) {
        if (character.getJob() == job) {
            character.setJob(null);
            character.setLastJobDate(gameTime.now());
            character.clearMove();
            Log.debug("Job cleared from character: " + character);

            jobModule.clearCharacter(job, character);
        }
    }

}

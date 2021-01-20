package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.SuperGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.CharacterFreeTimeExtra;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.freeTimeJobs.WalkJobFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

@GameObject
public class JobOrchestratorModule extends SuperGameModule<JobModel, JobModuleObserver> {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private CharacterModule characterModule;
    @Inject private WalkJobFactory walkJobFactory;
    @Inject private JobModule jobModule;
    @Inject private GameTime gameTime;
    @Inject private Game game;

    @Override
    public void onGameLongUpdate(Game game) {
        characterModule.getAll().stream().filter(CharacterModel::isFree).forEach(this::assign);
    }

    /**
     * Looking for best job to fit characters
     */
    private void assign(CharacterModel character) {

        List<JobModel> availableJobs = jobModule.getAll().stream()
                .filter(JobModel::isAvailable)
                .filter(JobModel::isFree)
                .filter(JobModel::isSubJobCompleted)
                .filter(JobModel::initConditionalCompleted)
                .sorted(Comparator.comparingInt(job -> WorldHelper.getApproxDistance(job.getTargetParcel(), character.getParcel())))
                .collect(Collectors.toList());

        // Assign regular job
        if (character.hasExtra(CharacterSkillExtra.class)) {
            for (CharacterSkillExtra.SkillEntry skill : character.getExtra(CharacterSkillExtra.class).getAll()) {
                for (JobModel job : availableJobs) {
                    if ((job.getSkillType() == null || job.getSkillType() == skill.type)) {
                        assign(character, job);
                        return;
                    }
                }
            }
        }

        // Aucun job n'a pu être assigné
        // Assign freetime job
        if (character.getJob() == null && waitTimeBeforeOptionalExpired(character) && character.hasExtra(CharacterFreeTimeExtra.class)) {
//                character.getExtra(CharacterFreeTimeExtra.class).getTypes().stream().findAny().ifPresent(type -> {
//                        JobModel freeTimeJob = type.getConstructor(CharacterModel.class).newInstance(character);
            JobModel job = walkJobFactory.createJob(character);
            jobModule.add(job);
            assign(character, job);
//                });
        }

    }

    private boolean waitTimeBeforeOptionalExpired(CharacterModel character) {
        return (character.getLastJobDate() != null ? character.getLastJobDate() : gameTime.getStartGameTime()).plus(applicationConfig.game.minuteBeforeIdleJob, MINUTES).isBefore(gameTime.now());
    }

    private void assign(CharacterModel character, JobModel job) {
        Objects.requireNonNull(job, "Try to assign null job");

        // Remove optional job
        Optional.ofNullable(character.getJob()).filter(JobModel::isOptional).ifPresent(jobModel -> jobModel.close(gameTime.now()));

        job.start(character);
    }

}

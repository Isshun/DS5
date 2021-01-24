package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.SuperGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.modules.character.CharacterModule;
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
    @Inject private PathManager pathManager;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private CharacterModule characterModule;
    @Inject private WalkJobFactory walkJobFactory;
    @Inject private JobModule jobModule;
    @Inject private GameTime gameTime;
    @Inject private Game game;

    @Override
    public void onGameLongUpdate(Game game) {
        List<JobModel> potentialJobs = jobModule.getAll().stream()
                .filter(JobModel::isAvailable)
                .filter(JobModel::isFree)
                .filter(JobModel::isSubJobCompleted)
                .filter(JobModel::initConditionalCompleted)
                .collect(Collectors.toList());

        characterModule.getAll().stream().filter(CharacterModel::isFree).forEach(character -> assign(character, potentialJobs));
    }

    /**
     * Looking for best job to fit characters
     */
    private void assign(CharacterModel character, List<JobModel> potentialJobs) {

        potentialJobs.forEach(job -> {
            JobCharacterStatus statusForCharacter = new JobCharacterStatus();
            statusForCharacter.character = character;
            statusForCharacter.available = true;
            statusForCharacter.approxDistance = WorldHelper.getApproxDistance(job.getTargetParcel(), character.getParcel());
            statusForCharacter.skillLevel = getSkillLevel(character, job.getSkillType());
            if (!hasPath(job, character)) {
                statusForCharacter.label = "No path";
                statusForCharacter.available = false;
            }
            job.statusMap.put(character, statusForCharacter);
        });

//        Collections.sort(potentialJobs, Comparator.comparing(o -> o.getStatusForCharacter(character).skillLevel, j -> j));
//        Collections.sort(potentialJobs, new Comparator<JobModel>() {
//            @Override
//            public int compare(JobModel o1, JobModel o2) {
//                int skillLevelDiff = o1.getStatusForCharacter(character).skillLevel - o2.getStatusForCharacter(character).skillLevel;
//                if (skillLevelDiff != 0) {
//                    return skillLevelDiff;
//                }
//                return 0;
//            }
//        });
        potentialJobs.sort(Comparator.comparing(o -> o.getStatusForCharacter(character)));

        potentialJobs.forEach(job -> job.getStatusForCharacter(character).index = potentialJobs.indexOf(job));

        potentialJobs.stream()
                .filter(job -> job.getStatusForCharacter(character).available).findFirst()
                .ifPresent(bestJob -> assign(character, bestJob));

        //        // Assign regular job
//        if (character.hasExtra(CharacterSkillExtra.class)) {
//            for (CharacterSkillExtra.SkillEntry skill : character.getExtra(CharacterSkillExtra.class).getAll()) {
//                for (JobModel job : availableJobs) {
//                    if ((job.getSkillType() == null || job.getSkillType() == skill.type)) {
//                        assign(character, job);
//                        return;
//                    }
//                }
//            }
//        }

//        // Aucun job n'a pu être assigné
//        // Assign freetime job
//        if (character.getJob() == null && waitTimeBeforeOptionalExpired(character) && character.hasExtra(CharacterFreeTimeExtra.class)) {
////                character.getExtra(CharacterFreeTimeExtra.class).getTypes().stream().findAny().ifPresent(type -> {
////                        JobModel freeTimeJob = type.getConstructor(CharacterModel.class).newInstance(character);
//            JobModel job = walkJobFactory.createJob(character);
//            jobModule.add(job);
//            assign(character, job);
////                });
//        }

    }

    private int getSkillLevel(CharacterModel character, CharacterSkillExtra.SkillType skillType) {
        return Optional.ofNullable(character.getExtra(CharacterSkillExtra.class).get(skillType)).map(skillEntry -> (int) skillEntry.level).orElse(0);
    }

    private boolean hasPath(JobModel job, CharacterModel character) {
        return pathManager.getPath(character.getParcel(), job.getTargetParcel(), false, false, true) != null;
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

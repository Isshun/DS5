package org.smallbox.faraway.modules.job;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.CharacterFreeTimeExtra;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@GameObject
public class JobOrchestratorModule {
    private Map<CharacterModel, Integer> _characterInnactiveDuration = new ConcurrentHashMap<>();

    @Inject
    private CharacterModule characterModule;

    @Inject
    private JobModule jobModule;

    public void assign() {
        characterModule.getCharacters().stream()
                .filter(CharacterModel::isFree)
                .forEach(this::assign);
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
        if (_characterInnactiveDuration.getOrDefault(character, 0) > 2) {
            _characterInnactiveDuration.put(character, 0);

            List<JobModel> availableJobs = jobModule.getJobs().stream()
                    .filter(job -> job.getCharacter() == null)
                    .filter(job -> job.getStatus() == JobModel.JobStatus.JOB_WAITING || job.getStatus() == JobModel.JobStatus.JOB_INITIALIZED)
                    .filter(JobModel::isSubJobCompleted)
                    .filter(JobModel::initConditionalCompleted)
                    .sorted(Comparator.comparingInt(j -> WorldHelper.getApproxDistance(j.getStartParcel(), character.getParcel())))
                    .collect(Collectors.toList());

            // Assign regular job
            if (character.hasExtra(CharacterSkillExtra.class)) {
                for (CharacterSkillExtra.SkillEntry skill: character.getExtra(CharacterSkillExtra.class).getAll()) {
                    for (JobModel job: availableJobs) {
                        if ((job.getSkillType() == null || job.getSkillType() == skill.type)) {
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
                        JobModel freeTimeJob = type.getConstructor(CharacterModel.class).newInstance(character);
                        jobModule.addJob(freeTimeJob);
                        assign(character, freeTimeJob);
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
        Objects.requireNonNull(job, "Try to assign null job");

        _characterInnactiveDuration.put(character, 0);
        job.start(character);
        if (character.getJob() == null || character.getJob() != job) {
            Log.error(JobModule.class, "Fail to assign job");
        }

    }

}

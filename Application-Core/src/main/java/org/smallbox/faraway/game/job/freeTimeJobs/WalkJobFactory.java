package org.smallbox.faraway.game.job.freeTimeJobs;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobFactory;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.ActionDurationTask;
import org.smallbox.faraway.game.job.task.MoveTask;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@GameObject
public class WalkJobFactory extends JobFactory {
    private final static int MAX_DISTANCE = 10;

    @Inject private ApplicationConfig applicationConfig;
    @Inject private ConsumableModule consumableModule;
    @Inject private PathManager pathManager;

    public JobModel createJob(CharacterModel character) {
        JobModel job = new JobModel();

        job.setMainLabel("Walks");
        job.setOptional(true);
        job.setMoveSpeed(0.25f);

        IntStream.range(0, 10).forEach(value -> {
            job.addTask(new MoveTask("Walks", () -> WorldHelper.getRandomParcel(character.getParcel(), MAX_DISTANCE)));
            job.addTask(new ActionDurationTask("Walks", 5, TimeUnit.MINUTES, (c, hourInterval, localDateTime) -> {}));
        });

        return job;
    }

}

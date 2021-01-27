package org.smallbox.faraway.game.dig.factory;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.dig.DigJob;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.ActionTask;
import org.smallbox.faraway.game.job.task.TechnicalTask;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.GameException;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

public abstract class DigJobFactory {
    @Inject private ApplicationConfig applicationConfig;

    public DigJob createJob(Parcel digParcel) {
        if (digParcel.getRockInfo() != null) {
            DigJob job = new DigJob(digParcel);

            job.setMainLabel("Dig");
            job.setSkillType(CharacterSkillExtra.SkillType.DIG);
            job.setIcon("[base]/graphics/jobs/ic_mining.png");
            job.setColor(new Color(0x80391eff));
            job.setTotalDuration(applicationConfig.game.digTime);

            WorldHelper.getParcelAround(digParcel, SurroundedPattern.X_CROSS, job::addAcceptedParcel);
            WorldHelper.getParcelAround(WorldHelper.getParcelOffset(digParcel, 0, 0, 1), SurroundedPattern.X_CROSS, job::addAcceptedParcel);

            // Dig action
            job.addTask(new ActionTask("Dig", (character, hourInterval, localDateTime) -> {
                if (digParcel.getRockInfo() != null) {
                    job.addProgression(hourInterval);
                }
            }, () -> job.getDuration() >= job.getTotalDuration() ? TASK_COMPLETED : TASK_CONTINUE));

            // - Create output products
            // - Remove rock from parcel
            // - Refresh GraphNode connections
            job.addTask(new TechnicalTask(this::digAction));

            job.onNewInit();

            return job;
        }

        throw new GameException(DigJobFactory.class, "Unable to create dig job");
    }

    protected abstract void digAction(JobModel job);
}

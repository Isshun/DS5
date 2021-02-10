package org.smallbox.faraway.game.building;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.item.UsableItem;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.ActionTask;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;

@GameObject
public class DestructJobFactory {
    @Inject private ItemModule itemModule;
    @Inject private PathManager pathManager;
    @Inject private ApplicationConfig applicationConfig;

    public JobModel createJob(Parcel targetParcel) {
        UsableItem item = itemModule.getItem(targetParcel);
        if (item != null) {
            DestructJob destructJob = new DestructJob(targetParcel);

            destructJob.setSkillType(CharacterSkillExtra.SkillType.BUILD);
            destructJob.setMainLabel("Destruct " + item.getInfo().label);
            destructJob.setIcon("data/graphics/jobs/ic_build.png");
            destructJob.setTotalDuration(item.getBuildCost());
            destructJob.setAcceptedParcel(WorldHelper.getParcelAround(targetParcel, SurroundedPattern.SQUARE));

            // Job
            destructJob.addTask(new ActionTask("Destruct", (character, hourInterval, localDateTime) -> destructJob.addProgression(1 / applicationConfig.game.buildTime * hourInterval)));

            // Close
            destructJob.addCloseTask(job -> itemModule.remove(item)); // Delete item
            destructJob.addCloseTask(job -> pathManager.refreshConnections(targetParcel)); // Refresh path manager

            return destructJob;
        }
        return null;
    }

}

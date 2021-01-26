package org.smallbox.faraway.game.building;

import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.core.world.model.BuildableMapObject;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.ActionTask;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.SurroundedPattern;
import org.smallbox.faraway.game.world.WorldHelper;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

@GameObject
public class BuildJobFactory {
    @Inject private ItemModule itemModule;
    @Inject private PathManager pathManager;
    @Inject private BringItemJobFactory bringItemJobFactory;
    @Inject private ApplicationConfig applicationConfig;

    public JobModel createJob(ItemInfo itemInfo, Parcel targetParcel) {
        BuildableMapObject item = itemModule.addItem(itemInfo, false, targetParcel);
        BuildJob buildJob = new BuildJob(targetParcel);

        buildJob.setSkillType(CharacterSkillExtra.SkillType.BUILD);
        buildJob.setMainLabel("Build " + itemInfo.label);
        buildJob.setIcon("[base]/graphics/jobs/ic_build.png");
        buildJob.setTotalDuration(item.getBuildCost());
        buildJob.setAcceptedParcel(WorldHelper.getParcelAround(targetParcel, SurroundedPattern.SQUARE));

        itemInfo.receipts.stream().findFirst().ifPresent(
                receiptInfo -> receiptInfo.inputs.forEach(
                        inputInfo -> buildJob.addSubJob(bringItemJobFactory.createJob(buildJob, item, inputInfo.item, inputInfo.quantity))));

        // Job
        buildJob.addTask(new ActionTask("Build", (character, hourInterval, localDateTime) -> {
            item.actionBuild(1 / applicationConfig.game.buildTime * hourInterval);
            buildJob.addProgression(1 / applicationConfig.game.buildTime * hourInterval);
        }, () -> item.isComplete() ? TASK_COMPLETED : TASK_CONTINUE));

        // Close
        buildJob.addCloseTask(job -> removeUncompletedObject(item)); // Delete uncompleted object
        buildJob.addCloseTask(job -> pathManager.refreshConnections(targetParcel)); // Refresh path manager

        return buildJob;
    }

    private void removeUncompletedObject(BuildableMapObject _mapObject) {
        if (!_mapObject.isComplete()) {
            itemModule.removeObject(_mapObject);
        }
    }

}

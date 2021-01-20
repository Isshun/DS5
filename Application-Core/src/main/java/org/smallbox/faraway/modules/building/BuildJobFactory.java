package org.smallbox.faraway.modules.building;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.Parcel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.task.ActionTask;
import org.smallbox.faraway.modules.job.task.MoveTask;

import static org.smallbox.faraway.modules.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.modules.job.JobTaskReturn.TASK_CONTINUE;

@GameObject
public class BuildJobFactory {
    @Inject private ItemModule itemModule;
    @Inject private PathManager pathManager;
    @Inject private BringItemJobFactory bringItemJobFactory;
    @Inject private ApplicationConfig applicationConfig;

    public JobModel createJob(ItemInfo itemInfo, Parcel targetParcel) {
        BuildJob buildJob = new BuildJob();

        buildJob.setSkillType(CharacterSkillExtra.SkillType.BUILD);

        buildJob.setMainLabel("Build " + itemInfo.label);
        buildJob._targetParcel = targetParcel;
        buildJob._mapObject = itemModule.addItem(itemInfo, false, targetParcel);

        itemInfo.receipts.stream().findFirst().ifPresent(
                receiptInfo -> receiptInfo.inputs.forEach(
                        inputInfo -> buildJob.addSubJob(bringItemJobFactory.createJob(buildJob, buildJob._mapObject, inputInfo.item, inputInfo.quantity))));

        // Job
        buildJob.addTask(new MoveTask("Move to parcel", () -> targetParcel));
        buildJob.addTask(new ActionTask("Build", (character, hourInterval, localDateTime) -> {
            buildJob._mapObject.actionBuild(1 / applicationConfig.game.buildTime * hourInterval);
            buildJob.setProgress(buildJob._mapObject.getBuildValue(), buildJob._mapObject.getBuildCost());
        }, () -> buildJob._mapObject.isComplete() ? TASK_COMPLETED : TASK_CONTINUE));

        // Close
        buildJob.addCloseTask(job -> removeUncompletedObject(buildJob._mapObject)); // Delete uncompleted object
        buildJob.addCloseTask(job -> pathManager.refreshConnections(job.getTargetParcel())); // Refresh path manager

        return buildJob;
    }

    private void removeUncompletedObject(BuildableMapObject _mapObject) {
        if (!_mapObject.isComplete()) {
            itemModule.removeObject(_mapObject);
        }
    }

}

package org.smallbox.faraway.modules.building;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

@GameObject
public class BuildJobFactory {

    @Inject
    private ItemModule itemModule;

    @Inject
    private PathManager pathManager;

    @Inject
    private ApplicationConfigService applicationConfigService;

    public JobModel createJob(ItemInfo itemInfo, ParcelModel targetParcel) {
        BuildJob job = new BuildJob();

        job.setSkillType(CharacterSkillExtra.SkillType.BUILD);

        job._targetParcel = targetParcel;
        job._startParcel = targetParcel;
        job._jobParcel = targetParcel;

        // Init
        job.addInitTask(() -> job._mapObject = itemModule.addItem(itemInfo, false, targetParcel));

        // Job
        job.addMoveTask("Move to parcel", targetParcel);
        job.addTask("Build", (character, hourInterval) -> {
            job._mapObject.actionBuild(1 / applicationConfigService.getGameInfo().buildTime * hourInterval);
            job.setProgress(job._mapObject.getBuildValue(), job._mapObject.getBuildCost());

            if (!job._mapObject.isComplete()) {
                return JobTaskReturn.TASK_CONTINUE;
            }

            return JobTaskReturn.TASK_COMPLETE;
        });

        // Close
        job.addCloseTask(() -> removeUncompletedObject(job._mapObject)); // Delete uncompleted object
        job.addCloseTask(() -> pathManager.refreshConnections(job.getJobParcel())); // Refresh path manager

        return job;
    }

    private void removeUncompletedObject(BuildableMapObject _mapObject) {
        if (!_mapObject.isComplete()) {
            itemModule.removeObject(_mapObject);
        }
    }

}

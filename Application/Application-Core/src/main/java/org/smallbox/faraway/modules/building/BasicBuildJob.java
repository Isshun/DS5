package org.smallbox.faraway.modules.building;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.modules.job.JobTaskReturn;

import java.util.Map;

/**
 * Job déplacant les consomables vers les zones de stockage
 */
public class BasicBuildJob extends JobModel {

    protected Map<ConsumableItem, Integer> _targetConsumables;
    private MapObjectModel _mapObject;

    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

    public static void buildStructure(JobModule jobModule, BuildableMapObject mapObject) {

        jobModule.createJob(BasicBuildJob.class, null, mapObject.getParcel(), job -> {
            mapObject.setBuildJob(job);

            job.setMainLabel("Build " + mapObject.getInfo().label);

            job._mapObject = mapObject;
            job._targetParcel = mapObject.getParcel();

            job.addMoveTask("Move to object", mapObject.getParcel());
            job.addTask("Build", (character, hourInterval) -> {
                mapObject.actionBuild(1 / Application.config.game.buildTime * hourInterval);
                job.setProgress(mapObject.getBuildValue(), mapObject.getBuildCost());
                return mapObject.isBuildComplete() ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
            });

            return true;
        });

    }

    public BasicBuildJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
        super(itemInfoAction, parcelModel);
    }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        return JobCheckReturn.OK;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillNeeded() {
        return CharacterSkillExtra.SkillType.STORE;
    }

    public MapObjectModel getObject() {
        return _mapObject;
    }

}

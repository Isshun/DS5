package org.smallbox.faraway.modules.building;

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
public class BasicRepairJob extends JobModel {

    protected Map<ConsumableItem, Integer> _targetConsumables;
    private MapObjectModel _mapObject;

    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

    public static void repairStructure(JobModule jobModule, BuildableMapObject mapObject) {

        jobModule.createJob(BasicRepairJob.class, null, mapObject.getParcel(), job -> {
            job.setMainLabel("Repair " + mapObject.getInfo().label);

            job._mapObject = mapObject;
            job._targetParcel = mapObject.getParcel();

            job.addTask("Move to object", character -> character.moveTo(mapObject.getParcel()) ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE);
            job.addTask("Repair", character -> {
                mapObject.setHealth(mapObject.getHealth() + 1);
                job.setProgress(mapObject.getHealth(), mapObject.getMaxHealth());
                return mapObject.getHealth() >= mapObject.getMaxHealth() ? JobTaskReturn.TASK_COMPLETE : JobTaskReturn.TASK_CONTINUE;
            });

            return true;
        });

    }

    public BasicRepairJob(ItemInfo.ItemInfoAction itemInfoAction, ParcelModel parcelModel) {
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

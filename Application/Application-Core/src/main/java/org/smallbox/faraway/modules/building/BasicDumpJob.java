package org.smallbox.faraway.modules.building;

import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

import java.util.Map;

/**
 * Job déplacant les consomables vers les zones de stockage
 */
public class BasicDumpJob extends JobModel {

    protected Map<ConsumableItem, Integer> _targetConsumables;
    private MapObjectModel _mapObject;

    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

    public BasicDumpJob(ItemModule itemModule, BuildableMapObject mapObject) {
        super(null, mapObject.getParcel());

        _mapObject = mapObject;
        _targetParcel = mapObject.getParcel();
        _startParcel = mapObject.getParcel();

        setMainLabel("Dump " + mapObject.getInfo().label);

        addMoveTask("Move to object", mapObject.getParcel());

        addTask("Dump", (character, hourInterval) -> {
            setProgress(_progress + (1 / 0.15 * hourInterval), 1);

            if (_progress < 1) {
                return JobTaskReturn.TASK_CONTINUE;
            }

            itemModule.removeObject(mapObject);
            return JobTaskReturn.TASK_COMPLETE;
        });
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

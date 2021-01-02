package org.smallbox.faraway.modules.building;

import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.modules.character.model.CharacterSkillExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobTaskReturn;

/**
 * Job déplacant les consomables vers les zones de stockage
 */
public class BasicDumpJob extends JobModel {

    private MapObjectModel _mapObject;

    public BasicDumpJob(ItemModule itemModule, BuildableMapObject mapObject) {
        super(null, mapObject.getParcel());

        _mapObject = mapObject;
        _targetParcel = mapObject.getParcel();

        setMainLabel("Dump " + mapObject.getInfo().label);

        addMoveTask("Move to object", mapObject::getParcel);

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
    public boolean checkCharacterAccepted(CharacterModel character) {

        // Character have no skill
        if (!character.hasExtra(CharacterSkillExtra.class) || !character.getExtra(CharacterSkillExtra.class).hasSkill(CharacterSkillExtra.SkillType.BUILD)) {
            return false;
        }

        // Character is qualified for job
        return true;

    }

    public MapObjectModel getObject() {
        return _mapObject;
    }

    @Override
    public CharacterSkillExtra.SkillType getSkillType() {
        return CharacterSkillExtra.SkillType.BUILD;
    }

}

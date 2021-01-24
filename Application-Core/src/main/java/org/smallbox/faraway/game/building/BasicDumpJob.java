package org.smallbox.faraway.game.building;

import org.smallbox.faraway.core.world.model.BuildableMapObject;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.job.JobCheckReturn;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.job.task.ActionTask;
import org.smallbox.faraway.game.job.task.MoveTask;

import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_COMPLETED;
import static org.smallbox.faraway.game.job.JobTaskReturn.TASK_CONTINUE;

/**
 * Job déplacant les consomables vers les zones de stockage
 */
public class BasicDumpJob extends JobModel {

    private final MapObjectModel _mapObject;

    public BasicDumpJob(ItemModule itemModule, BuildableMapObject mapObject) {
        super(null, mapObject.getParcel());

        _mapObject = mapObject;
        _targetParcel = mapObject.getParcel();

        setMainLabel("Dump " + mapObject.getInfo().label);

        addTask(new MoveTask("Move to object", mapObject::getParcel));

        addTask(new ActionTask("Dump", (character, hourInterval, localDateTime) -> {
            setProgress(_progress + (1 / 0.15 * hourInterval), 1);
            if (_progress >= 1) {
                itemModule.removeObject(mapObject);
            }
        }, () -> _progress >= 1 ? TASK_COMPLETED : TASK_CONTINUE));
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

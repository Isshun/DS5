package org.smallbox.faraway.game.building;

import org.smallbox.faraway.core.world.model.BuildableMapObject;
import org.smallbox.faraway.game.consumable.ConsumableItem;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.character.model.CharacterSkillExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.job.JobCheckReturn;
import org.smallbox.faraway.game.job.JobModel;

import java.util.Map;

/**
 * Job déplacant les consomables vers les zones de stockage
 */
public class BuildJob extends JobModel {

    protected Map<ConsumableItem, Integer> _targetConsumables;
    public BuildableMapObject _mapObject;

    public Map<ConsumableItem, Integer> getConsumables() { return _targetConsumables; }

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

}

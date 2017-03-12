package org.smallbox.faraway.modules.item;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.job.JobModel;

/**
 * Created by Alex on 09/10/2015.
 */
public class BuildJob extends JobModel {
    private final BuildableMapObject _buildItem;

    public BuildJob(BuildableMapObject item) {
        super(null, item.getParcel());
//        super(null, item.getParcel(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));
        _buildItem = item;
        _buildItem.setBuildJob(this);
        _label = "Build " + _buildItem.getInfo().label;
    }

    public BuildableMapObject getBuildItem() {
        return _buildItem;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return CharacterTalentExtra.TalentType.BUILD;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        if (!_buildItem.hasAllComponents()) {
            return JobCheckReturn.STAND_BY;
        }

        if (!Application.pathManager.hasPath(character.getParcel(), _buildItem.getParcel())) {
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onClose() {
        _buildItem.removeJob(this);
    }
}

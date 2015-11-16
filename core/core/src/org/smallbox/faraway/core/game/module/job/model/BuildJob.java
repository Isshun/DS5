package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.engine.drawable.IconDrawable;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.BuildableMapObject;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 09/10/2015.
 */
public class BuildJob extends JobModel {
    private final BuildableMapObject _buildItem;

    public BuildJob(BuildableMapObject item) {
        super(null, item.getParcel(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));
        _buildItem = item;
        _buildItem.setBuildJob(this);
        _label = "Build " + _buildItem.getInfo().label;
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

        if (!PathManager.getInstance().hasPath(character.getParcel(), _buildItem.getParcel())) {
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        _message = "Building";

        PathModel path = PathManager.getInstance().getBestAround(character.getParcel(), _jobParcel);
        if (path != null) {
            _targetParcel = path.getLastParcel();
            System.out.println("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
            character.move(path);
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (WorldHelper.getApproxDistance(_character.getParcel(), _buildItem.getParcel()) <= 2) {
            if (_buildItem.build()) {
                _buildItem.setBuildJob(null);
                if (_buildItem instanceof ItemModel) {
                    Application.getInstance().notify(observer -> observer.onItemComplete((ItemModel) _buildItem));
                } else if (_buildItem instanceof StructureModel) {
                    Application.getInstance().notify(observer -> observer.onStructureComplete((StructureModel)_buildItem));
                }
                return JobActionReturn.COMPLETE;
            }
            _progress = (double)_buildItem.getCurrentBuild() / _buildItem.getTotalBuild();
        }
        return JobActionReturn.CONTINUE;
    }

    @Override
    protected void onFinish() {
    }
}

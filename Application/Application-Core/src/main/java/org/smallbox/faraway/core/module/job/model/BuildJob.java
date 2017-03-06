package org.smallbox.faraway.core.module.job.model;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.modules.character.model.CharacterTalentExtra;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.module.world.model.BuildableMapObject;
import org.smallbox.faraway.util.Log;

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
    public void onCancel() {
        throw new NotImplementedException("");

//        _buildItem.getComponents().forEach(component -> ModuleHelper.getWorldModule().putConsumable(_jobParcel, component.info, component.currentQuantity));
//        _buildItem.getComponents().clear();
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
    protected void onStart(CharacterModel character) {
        _message = "Building";

        _buildItem.addJob(this);

        PathModel path = Application.pathManager.getPath(character.getParcel(), _jobParcel, true, false);
        if (path != null) {
            _targetParcel = path.getLastParcel();
            Log.info("best path to: " + _targetParcel.x + "x" + _targetParcel.y + " (" + character.getPersonals().getFirstName() + ")");
            character.move(path);
        }
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (WorldHelper.getApproxDistance(_character.getParcel(), _buildItem.getParcel()) <= 2) {
            if (_buildItem.build()) {
                _buildItem.setBuildJob(null);
                Application.notify(observer -> observer.onObjectComplete(_buildItem));
                return JobActionReturn.COMPLETE;
            }
            _progress = (double)_buildItem.getBuildProgress() / _buildItem.getBuildCost();
        }
        return JobActionReturn.CONTINUE;
    }

    @Override
    protected void onClose() {
        _buildItem.removeJob(this);
    }
}

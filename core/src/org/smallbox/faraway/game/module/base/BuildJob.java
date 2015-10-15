package org.smallbox.faraway.game.module.base;

import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.BuildableMapObject;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.util.OnMoveListener;

/**
 * Created by Alex on 09/10/2015.
 */
public class BuildJob extends BaseJobModel {
    private final BuildableMapObject _buildItem;
    private JobActionReturn _return = JobActionReturn.CONTINUE;

    public BuildJob(BuildableMapObject item) {
        super(null, item.getX(), item.getY(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));
        _buildItem = item;
        _buildItem.setBuildJob(this);
    }

    @Override
    public String getShortLabel() {
        return "Build " + _buildItem.getInfo().label;
    }

    @Override
    public ParcelModel getActionParcel() {
        return null;
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return CharacterModel.TalentType.BUILD;
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        return _buildItem.hasAllComponents();
    }

    @Override
    protected void onFinish() {
    }

    @Override
    protected void onStart(CharacterModel character) {
        _character = character;

        // TODO: reliquat
        _posX = _buildItem.getX();
        _posY = _buildItem.getY();
        _message = "Build " + _buildItem.getInfo().label;

        _character.moveApprox(this, _buildItem.getParcel(), new OnMoveListener() {
            @Override
            public void onReach(BaseJobModel job, MovableModel movable) {
            }

            @Override
            public void onFail(BaseJobModel job, MovableModel movable) {
                _return = JobActionReturn.QUIT;
            }

            @Override
            public void onSuccess(BaseJobModel job, MovableModel movable) {
            }
        });
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (WorldHelper.getApproxDistance(_character.getParcel(), _buildItem.getParcel()) <= 2) {
            if (_buildItem.build()) {
                _buildItem.setBuildJob(null);
                _return = JobActionReturn.FINISH;
                if (_buildItem instanceof ItemModel) {
                    Game.getInstance().notify(observer -> observer.onRefreshItem((ItemModel) _buildItem));
                } else if (_buildItem instanceof StructureModel) {
                    Game.getInstance().notify(observer -> observer.onRefreshStructure((StructureModel)_buildItem));
                }
            }
            _progress = (double)_buildItem.getCurrentBuild() / _buildItem.getTotalBuild();
        }
        return _return;
    }
}

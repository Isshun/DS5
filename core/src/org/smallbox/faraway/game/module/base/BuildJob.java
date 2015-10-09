package org.smallbox.faraway.game.module.base;

import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.core.drawable.IconDrawable;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.MovableModel;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.util.OnMoveListener;

/**
 * Created by Alex on 09/10/2015.
 */
public class BuildJob extends BaseJobModel {
    private JobActionReturn _return = JobActionReturn.CONTINUE;

    public BuildJob(ItemModel item) {
        super(null, item.getX(), item.getY(), new IconDrawable("data/res/ic_build.png", 0, 0, 32, 32), new AnimDrawable("data/res/actions.png", 0, 64, 32, 32, 7, 10));
        _item = item;
        _item.setBuildJob(this);
    }

    @Override
    public String getShortLabel() {
        return "Build " + _item.getInfo().label;
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
        return _item.hasAllComponents();
    }

    @Override
    protected void onFinish() {
    }

    @Override
    protected void onStart(CharacterModel character) {
        _character = character;

        // TODO: reliquat
        _posX = _item.getX();
        _posY = _item.getY();

        _character.moveTo(this, _item.getParcel(), new OnMoveListener() {
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
        if (_character.getParcel() == _item.getParcel()) {
            if (_item.build()) {
                _item.setBuildJob(null);
                _return = JobActionReturn.FINISH;
            }
        }
        return _return;
    }
}

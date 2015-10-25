package org.smallbox.faraway.core.game.module.job;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

/**
 * Created by Alex on 25/10/2015.
 */
public class SleepJob extends JobModel {
    private long            _wakeTime;
    private ItemModel       _sleepItem;
    private long            _sleepTime;

    public SleepJob() {
        _sleepTime = Game.getInstance().getTick();
        _wakeTime = _sleepTime + (GameData.config.tickPerHour * 6);
    }

    public SleepJob(ItemModel item) {
        _sleepItem = item;
        _jobParcel = _targetParcel = item.getParcel();
        _sleepTime = Game.getInstance().getTick();
        _wakeTime = _sleepTime + (GameData.config.tickPerHour * 6);
    }

    @Override
    public String getShortLabel() {
        return "Sleep";
    }

    @Override
    public String getMessage() {
        return _sleepItem == null ? "Sleep on the ground" : "Sleep in " + _sleepItem.getInfo().label;
    }

    @Override
    public ParcelModel getActionParcel() {
        return null;
    }

    @Override
    protected boolean onCheck(CharacterModel character) {
        return true;
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (Game.getInstance().getTick() < _wakeTime) {
            character.getNeeds().isSleeping = true;
            _progress = (double)(Game.getInstance().getTick() - _sleepTime) / (_wakeTime - _sleepTime);
            return JobActionReturn.CONTINUE;
        }

        character.getNeeds().isSleeping = false;
        return JobActionReturn.FINISH;
    }

    @Override
    protected void onFinish() {
        if (_sleepItem != null) {
            _sleepItem.freeSlot(this);
        }
    }

    @Override
    public CharacterModel.TalentType getTalentNeeded() {
        return null;
    }

    public void setWakeTime(int time) {
        _wakeTime = _sleepTime + (GameData.config.tickPerHour * time);
    }
}

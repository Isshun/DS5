package org.smallbox.faraway.core.game.module.job;

import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;

/**
 * Created by Alex on 25/10/2015.
 */
public class SleepJob extends JobModel {
    private long            _wakeTime;
    private ItemModel       _sleepItem;
    private long            _sleepTime;

    public SleepJob(ParcelModel parcel) {
        _label = "Sleep on ground";
        _targetParcel = parcel;
        _sleepTime = Game.getInstance().getTick();
        _wakeTime = _sleepTime + (GameData.config.tickPerHour * 6);
    }

    public SleepJob(ParcelModel parcel, ItemModel item) {
        _label = "Sleep in " + item.getInfo().label;
        _sleepItem = item;
        _jobParcel = _targetParcel = parcel;
        _sleepTime = Game.getInstance().getTick();
        _wakeTime = _sleepTime + (GameData.config.tickPerHour * 6);
    }

    public ItemModel getItem() { return _sleepItem; }

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
        if (_item != null && !PathManager.getInstance().hasPath(character.getParcel(), _item.getParcel())) {
            return false;
        }

        return true;
    }

    @Override
    protected void onStart(CharacterModel character) {
        if (_sleepItem != null) {
            _slot = _sleepItem.takeSlot(this);
            _targetParcel = _slot != null ? _slot.getParcel() : _sleepItem.getParcel();
        }
        character.moveTo(_targetParcel, null);
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (Game.getInstance().getTick() < _wakeTime) {
            character.setSleeping(true);
            character.getNeeds().isSleeping = true;
            _progress = (double)(Game.getInstance().getTick() - _sleepTime) / (_wakeTime - _sleepTime);
            return JobActionReturn.CONTINUE;
        }

        character.setSleeping(false);
        return JobActionReturn.FINISH;
    }

    @Override
    protected void onFinish() {
        if (_sleepItem != null && _slot != null) {
            _sleepItem.releaseSlot(_slot);
        }
    }

    @Override
    protected void onQuit(CharacterModel character) {
        character.getNeeds().isSleeping = false;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return null;
    }

    public void setWakeTime(int time) {
        _wakeTime = _sleepTime + (GameData.config.tickPerHour * time);
    }
}

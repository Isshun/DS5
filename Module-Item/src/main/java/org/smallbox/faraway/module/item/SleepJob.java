package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.module.item.item.ItemModel;
import org.smallbox.faraway.module.item.item.ItemSlot;

/**
 * Created by Alex on 25/10/2015.
 */
public class SleepJob extends JobModel {
    private ItemModel       _sleepItem;
    private long            _startTime;
    private ItemSlot        _slot;

    public SleepJob(ParcelModel parcel) {
        _label = "Sleep on ground";
        _message = "Sleep on ground";
        _targetParcel = parcel;
    }

    public SleepJob(ParcelModel parcel, ItemModel item) {
        _label = _message = Data.getString("Sleep in") + " " + Data.getString(item.getInfo().label);
        _sleepItem = item;
        _jobParcel = _targetParcel = parcel;
    }

    public ItemModel getItem() { return _sleepItem; }

    @Override
    protected JobCheckReturn onCheck(CharacterModel character) {
        if (_sleepItem != null && !PathManager.getInstance().hasPath(character.getParcel(), _sleepItem.getParcel())) {
            return JobCheckReturn.ABORT;
        }
        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        _startTime = Game.getInstance().getTick();
        _endTime = computeWakeTime(character);

        if (_sleepItem != null) {
            _slot = _sleepItem.takeSlot(this);
            _sleepItem.addJob(this);
            _targetParcel = _slot != null ? _slot.getParcel() : _sleepItem.getParcel();
        }

        character.moveTo(_targetParcel, null);
    }

    private long computeWakeTime(CharacterModel character) {
        double change = 0;

        // Add character change property
        change += character.getType().needs.energy.change.sleep;

        // Add item change property
        if (_sleepItem != null) {
            for (ItemInfo.ItemInfoAction action: _sleepItem.getInfo().actions) {
                change += action.effects.energy;
            }
        }

        assert change > 0;

//        // Get next alarm
//        int hoursByDay = Game.getInstance().getInfo().planet.dayDuration;
//        int bedHour = Game.getInstance().getHour();
//        int wakeTime = -1;
//        for (int i = 0; i < hoursByDay; i++) {
//            if (character.getTimetable().get((bedHour + i) % hoursByDay) != 1) {
//                wakeTime = i;
//            }
//        }

        return Game.getInstance().getTick() + (int)((100 - character.getNeeds().get("energy")) / change);
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        if (Game.getInstance().getTick() < _endTime) {
            character.setSleeping(true);
            character.getNeeds().isSleeping = true;
            _progress = (double)(Game.getInstance().getTick() - _startTime) / (_endTime - _startTime);
            return JobActionReturn.CONTINUE;
        }

        character.setSleeping(false);
        return JobActionReturn.COMPLETE;
    }

    @Override
    protected void onFinish() {
        if (_sleepItem != null && _slot != null) {
            _sleepItem.releaseSlot(_slot);
            _sleepItem.removeJob(this);
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

//    public void setWakeTime(int time) {
//        _wakeTime = _startTime + (Application.getInstance().getConfig().game.tickPerHour * time);
//    }
}
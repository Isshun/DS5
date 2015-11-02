package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ItemSlot;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.Log;

public class UseJob extends JobModel {

    private int _current;

    @Override
    public boolean canBeResume() {
        return false;
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return null;
    }

    private UseJob() {
        super();
    }

    public static UseJob create(ItemModel item) {
        if (item == null || !item.hasFreeSlot()) {
            return null;
        }

        ItemInfo.ItemInfoAction infoAction = item.getInfo().actions.get(0);

        UseJob job = new UseJob();
        ItemSlot slot = item.takeSlot(job);
        if (slot != null) {
            job.setSlot(slot);
            job.setTargetParcel(WorldHelper.getParcel(slot.getX(), slot.getY()));
        } else {
            job.setTargetParcel(item.getParcel());
        }
        job.setActionInfo(infoAction);
        job.setItem(item);
        job.setCost(infoAction.cost);

        return job;
    }

    public static UseJob create(CharacterModel character, ItemModel item) {
        if (character == null) {
            return null;
        }

        UseJob job = create(item);
        if (job != null) {
            job.setCharacterRequire(character);
        }

        return job;
    }

    // TODO: make objects stats table instead switch
    @Override
    public JobActionReturn onAction(CharacterModel character) {
        // Wrong call
        if (_item == null || character == null) {
            Log.error("wrong call");
            return JobActionReturn.ABORT;
        }

        // Item not reached
        if (character.getParcel() != _targetParcel) {
            return JobActionReturn.ABORT;
        }

        // Character is sleeping
        if (character.isSleeping() && !_item.isSleepingItem()) {
            Log.debug("use: sleeping . use canceled");
            return JobActionReturn.QUIT;
        }

        if (!check(character)) {
            return JobActionReturn.ABORT;
        }

        Log.debug("Character #" + character.getName() + ": actionUse");

        // Character using item
        _current++;
        _progress = (double)_current / _cost;
        _item.use(_character, 0);
        if (_current < _cost) {
            return JobActionReturn.CONTINUE;
        }

//        // Set characters direction
//        if (_item.getX() > _targetParcel.x) { character.setDirection(Direction.RIGHT); }
//        if (_item.getX() < _targetParcel.x) { character.setDirection(Direction.LEFT); }
//        if (_item.getY() > _targetParcel.y) { character.setDirection(Direction.TOP); }
//        if (_item.getY() < _targetParcel.y) { character.setDirection(Direction.BOTTOM); }

        return JobActionReturn.FINISH;
    }

    @Override
    public boolean onCheck(CharacterModel character) {
        // Item is null
        if (_item == null) {
            _reason = JobAbortReason.INVALID;
            return false;
        }

        // Item is no longer exists
        if (_item != WorldHelper.getItem(_item.getX(), _item.getY())) {
            _reason = JobAbortReason.INVALID;
            return false;
        }

        return true;
    }

    @Override
    protected void onStart(CharacterModel character) {
        character.moveTo(_targetParcel, null);
    }

    @Override
    protected void onFinish() {

    }

    @Override
    public String getLabel() {
        if (_actionInfo != null && _actionInfo.label != null) {
            return _actionInfo.label;
        }
        return "use " + _item.getLabel();
    }

    @Override
    public String getShortLabel() {
        return "use " + _item.getLabel();
    }

    @Override
    public ParcelModel getActionParcel() {
        return _item.getParcel();
    }

}

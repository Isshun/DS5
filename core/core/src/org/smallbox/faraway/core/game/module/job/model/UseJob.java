package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ItemSlot;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
import org.smallbox.faraway.core.util.Log;

public class UseJob extends JobModel {
    private int         _current;
    private ItemModel   _item;
    private ItemSlot    _slot;

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return null;
    }

    private UseJob() {
        super();
    }

    public static UseJob create(ItemModel item) {
        assert item != null;

        if (!item.hasFreeSlot()) {
            return null;
        }

        ItemInfo.ItemInfoAction infoAction = item.getInfo().actions.get(0);

        UseJob job = new UseJob();
        job.setActionInfo(infoAction);
        job._item = item;
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

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        // Item is no longer exists
        if (_item != _item.getParcel().getItem()) {
            _reason = JobAbortReason.INVALID;
            return JobCheckReturn.ABORT;
        }

        if (!PathManager.getInstance().hasPath(character.getParcel(), _item.getParcel())) {
            return JobCheckReturn.STAND_BY;
        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        assert _character != null;

        _slot = _item.takeSlot(this);
        _targetParcel = _slot != null ? _slot.getParcel() : _item.getParcel();
        character.moveTo(_targetParcel, null);
    }

    @Override
    public JobActionReturn onAction(CharacterModel character) {
        assert _item != null;
        assert _character != null;

        // Item not reached
        if (character.getParcel() != _targetParcel) {
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

        return JobActionReturn.COMPLETE;
    }

    @Override
    protected void onQuit(CharacterModel character) {
        if (_item != null && _slot != null) {
            _item.releaseSlot(_slot);
        }
    }

    @Override
    public String getLabel() {
        if (_actionInfo != null && _actionInfo.label != null) {
            return _actionInfo.label;
        }
        return "use " + _item.getLabel();
    }
}

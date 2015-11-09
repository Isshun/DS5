package org.smallbox.faraway.core.game.module.job.model;

import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.drawable.AnimDrawable;
import org.smallbox.faraway.core.game.module.character.model.CharacterTalentExtra;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.MoveListener;

public class ConsumeJob extends JobModel {

    private enum State {
        MOVE_TO_CONSUMABLE,
        MOVE_TO_FREE_SPACE
    }
    private State _state = State.MOVE_TO_CONSUMABLE;

    private ConsumableModel     _consumable;
    private ItemInfo            _itemInfo;
    private double              _current;

    private ConsumeJob(ParcelModel parcel) {
        super(null, parcel, null, new AnimDrawable("data/res/action_consume.png", 0, 0, 32, 32, 2, 10));
    }

    public static ConsumeJob create(CharacterModel character, ConsumableModel consumable) {
        assert consumable != null;
        assert character != null;

        ConsumeJob job = character.getInventory() == consumable ? new ConsumeJob(character.getParcel()) : new ConsumeJob(consumable.getParcel());
        job.setCharacterRequire(character);
        job._cost = consumable.getInfo().consume.cost;
        job._consumable = consumable;
        job._itemInfo = consumable.getInfo();

        return job;
    }

    @Override
    public JobCheckReturn onCheck(CharacterModel character) {
        if (_character.getInventory() != null && _character.getInventory().getInfo() != _itemInfo) {
            return JobCheckReturn.ABORT;
        }

        // Missing item
        if (_consumable.getQuantity() <= 0) {
            Log.error("ConsumeJob: item cannot be null, non consumable or empty");
            _reason = JobAbortReason.INVALID;
            return JobCheckReturn.ABORT;
        }

        // Item is no longer exists
        if (_consumable != _character.getInventory() && _consumable.getParcel().getConsumable() != _consumable) {
//            _reason = JobAbortReason.ABORT;
            return JobCheckReturn.ABORT;
        }

        // Consumable has been locked by another job
        if (_consumable.getLock() != null && _consumable.getLock() != this) {
            return JobCheckReturn.ABORT;
        }

        // Path exists
        if (PathManager.getInstance().hasPath(character.getParcel(), _consumable.getParcel(), true, false)) {
            return JobCheckReturn.ABORT;
        }

        return JobCheckReturn.OK;
    }

    @Override
    protected void onStart(CharacterModel character) {
        if (_consumable.getLock() != null && _consumable.getLock() != this) {
            _status = JobStatus.ABORTED;
            return;
        }

        PathModel path = PathManager.getInstance().getPath(character.getParcel(), _consumable.getParcel(), true, false);
        if (path == null) {
            _status = JobStatus.ABORTED;
            return;
        }

        _consumable.lock(this);
        _targetParcel = path.getLastParcel();
        character.move(path, new MoveListener<CharacterModel>() {
            @Override
            public void onReach(CharacterModel movable) {
            }

            @Override
            public void onFail(CharacterModel movable) {
                finish();
            }
        });
    }

    // TODO: make objects stats table instead switch
    @Override
    public JobActionReturn onAction(CharacterModel character) {
        // Wrong call
        if (character == null) {
            Log.error("wrong call");
            return JobActionReturn.ABORT;
        }

        if (!check(character)) {
            return JobActionReturn.ABORT;
        }

        // Part 1 - Move to consumable
        if (_state == State.MOVE_TO_CONSUMABLE) {
            _state = State.MOVE_TO_FREE_SPACE;

            if (_consumable == null) {
                Log.error("wrong call");
                return JobActionReturn.ABORT;
            }

            if (_consumable.getLock() != this) {
                Log.error("consumable is not locked for current job");
                return JobActionReturn.ABORT;
            }

//            ParcelModel parcel = WorldHelper.getNearest(_targetParcel.x, _targetParcel.y, true, true, false, false, false, false, false);
//            if (parcel == null) {
//                return JobActionReturn.ABORT;
//            }

//            _targetParcel = parcel;
            _character.addInventory(_consumable, 1);
            if (_character.getInventory() == null || _character.getInventory().getInfo() != _consumable.getInfo()) {
                return JobActionReturn.ABORT;
            }
            _consumable.lock(null);

            // Remove consumable if depleted
            if (_consumable.getQuantity() <= 0) {
                ModuleHelper.getWorldModule().removeConsumable(_consumable);
            }

//            character.moveTo(parcel, null);

            return JobActionReturn.CONTINUE;
        }

        // Part 2 - Move to free space
        if (_state == State.MOVE_TO_FREE_SPACE) {
            Log.debug("Character #" + character.getPersonals().getName() + ": actionUse (" + _progress + ")");

            // Character use item
            _current++;
            _progress = _current / _cost;
            _character.getInventory().consume(_character, 0);
            if (_current < _cost) {
                return JobActionReturn.CONTINUE;
            }

            // Clear inventory when job are done
            _character.setInventory(null);
        }

        return JobActionReturn.COMPLETE;
    }

    @Override
    public void onQuit(CharacterModel character) {
        if (_consumable != null && _consumable.getLock() == this) {
            _consumable.lock(null);
        }
    }

    @Override
    protected void onFinish() {
        if (_consumable != null && _consumable.getLock() == this) {
            _consumable.lock(null);
        }
    }

    @Override
    public String getLabel() {
        if (_actionInfo != null && _actionInfo.label != null) {
            return _actionInfo.label;
        }
        return "use " + _consumable.getLabel();
    }

    @Override
    public CharacterTalentExtra.TalentType getTalentNeeded() {
        return null;
    }
}

package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.core.drawable.AnimDrawable;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.util.Log;

public class JobConsume extends BaseJobModel {

    private enum State {
        MOVE_TO_CONSUMABLE,
        MOVE_TO_FREE_SPACE
    }
    private State _state = State.MOVE_TO_CONSUMABLE;

	private ConsumableModel     _consumable;
    private ItemInfo            _itemInfo;

	private JobConsume(ParcelModel parcel) {
		super(null, parcel, null, new AnimDrawable("data/res/action_consume.png", 0, 0, 32, 32, 2, 10));
	}

	public static JobConsume create(CharacterModel character, ConsumableModel consumable) {
		if (character == null) {
			Log.error("Create ConsumeJob with null characters");
			return null;
		}

		if (consumable == null) {
			Log.error("Create ConsumeJob with null item");
			return null;
		}

		JobConsume job = null;
		if (character.getInventory() == consumable) {
			job = new JobConsume(character.getParcel());
		} else {
			job = new JobConsume(consumable.getParcel());
		}
		job.setCharacterRequire(character);
		job.setActionInfo(consumable.getInfo().actions.get(0));
		job.setConsumable(consumable);
        job._itemInfo = consumable.getInfo();

		return job;
	}

	private void setConsumable(ConsumableModel consumable) {
		_consumable = consumable;
	}

	@Override
	public void onQuit(CharacterModel character) {
	}

	@Override
	public boolean onCheck(CharacterModel character) {
        if (_character.getInventory() != null && _character.getInventory().getInfo() == _itemInfo) {
            return true;
        }

		// Missing item
		if (_consumable == null || _consumable.getQuantity() <= 0) {
			Log.error("JobConsume: item cannot be null, non consumable or empty");
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_consumable != _targetParcel.getConsumable()) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
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

            ParcelModel parcel = WorldHelper.getNearest(_targetParcel.x, _targetParcel.y, true, true, false, false, false, false, false);
            if (parcel == null) {
                return JobActionReturn.ABORT;
            }

            _targetParcel = parcel;
            _character.addInventory(_consumable, 1);
            if (_character.getInventory() == null || _character.getInventory().getInfo() != _consumable.getInfo()) {
                return JobActionReturn.ABORT;
            }

            // Remove consumable if depleted
            if (_consumable.getQuantity() <= 0) {
                ModuleHelper.getWorldModule().removeConsumable(_consumable);
            }
            _consumable = null;

            character.moveTo(this, parcel, null);

            return JobActionReturn.CONTINUE;
        }

        // Part 2 - Move to free space
        if (_state == State.MOVE_TO_FREE_SPACE) {
            Log.debug("Character #" + character.getInfo().getName() + ": actionUse (" + _progress + ")");

            // Character using item
            if (_progress++ < _cost) {

                // Use item
                _character.getInventory().use(_character, (int) (_cost - _progress));

                return JobActionReturn.CONTINUE;
            }

            // Clear inventory when job are done
            _character.setInventory(null);
        }

		return JobActionReturn.FINISH;
	}

	@Override
	protected void onFinish() {
	}

	@Override
	public String getLabel() {
		if (_actionInfo != null && _actionInfo.label != null) {
			return _actionInfo.label;
		}
		return "use " + _consumable.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "consume " + _consumable.getLabel();
	}

	@Override
	public ParcelModel getActionParcel() {
		return null;
	}

	@Override
	public boolean canBeResume() {
		return false;
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return null;
	}

	public ConsumableModel getConsumable() {
		return _consumable;
	}
}

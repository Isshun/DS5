package org.smallbox.faraway.game.model.job;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.Movable.Direction;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemSlot;

import java.util.List;

public class JobConsume extends BaseJobModel {

	public static JobConsume create(CharacterModel character, ConsumableModel item) {
		if (character == null) {
			Log.error("Create ConsumeJob with null character");
			return null;
		}

		if (item == null) {
			Log.error("Create ConsumeJob with null item");
			return null;
		}

		JobConsume job = new JobConsume();
		job.setCharacterRequire(character);
		if (character.getInventory() == item) {
			job.setPosition(character.getX(), character.getY());
		} else {
			job.setPosition(item.getX(), item.getY());
		}
		job.setActionInfo(item.getInfo().actions.get(0));
		job.setItem(item);

		return job;
	}

	@Override
	protected void onStart(CharacterModel character) {
	}

	@Override
	public void onQuit(CharacterModel character) {
	}

	@Override
	public boolean onCheck(CharacterModel character) {
		// Item is null
		if (_item == null || !_item.isConsumable() || _item.getQuantity() <= 0) {
			Log.error("JobConsume: item cannot be null, non consumable or empty");
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_item != _character.getInventory() && _item != Game.getWorldManager().getConsumable(_item.getX(), _item.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
	}

	// TODO: make objects stats table instead switch
	@Override
	public JobActionReturn onAction(CharacterModel character) {
		// Wrong call
		if (_item == null || character == null) {
			Log.error("wrong call");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return JobActionReturn.ABORT;
		}

		if (!check(character)) {
			JobManager.getInstance().close(this, _reason);
			return JobActionReturn.ABORT;
		}

		Log.debug("Character #" + character.getInfo().getName() + ": actionUse (" + _progress + ")");

		// Character using item
		if (_progress++ < _cost) {
			// Item is use by 2 or more character
			if (_item.getNbFreeSlots() + 1 < _item.getNbSlots()) {
				character.getNeeds().addRelation(1);
				List<ItemSlot> slots = _item.getSlots();
				for (ItemSlot slot: slots) {
					CharacterModel slotCharacter = slot.getJob() != null ? slot.getJob().getCharacter() : null;
					Game.getRelationManager().meet(character, slotCharacter);
				}
			}

			// Set character direction
			if (_item.getX() > _posX) { character.setDirection(Direction.RIGHT); }
			if (_item.getX() < _posX) { character.setDirection(Direction.LEFT); }
			if (_item.getY() > _posY) { character.setDirection(Direction.TOP); }
			if (_item.getY() < _posY) { character.setDirection(Direction.BOTTOM); }

			// Use item
			_item.use(_character, (int) (_cost - _progress));

			return JobActionReturn.CONTINUE;
		}

		return JobActionReturn.FINISH;
	}

	@Override
	protected void onFinish() {
		if (_item.getInfo().isConsumable) {
			((ConsumableModel)_item).addQuantity(-1);
			if (_item.getQuantity() <= 0) {
				Game.getWorldManager().removeConsumable((ConsumableModel) _item);
			}
		}
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
	public boolean canBeResume() {
		return false;
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return null;
	}
}

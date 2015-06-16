package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.Movable.Direction;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ConsumableModel;
import org.smallbox.faraway.model.item.ItemSlot;

import java.util.List;

public class JobConsume extends JobModel {

	@Override
	public boolean canBeResume() {
		return false;
	}

	@Override
	public CharacterModel.TalentType getTalentNeeded() {
		return null;
	}

	private JobConsume() {
		super();
	}

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

	// TODO: make objects stats table instead switch
	@Override
	public boolean action(CharacterModel character) {
		// Wrong call
		if (_item == null || character == null) {
			Log.error("wrong call");
			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
			return true;
		}

		// Item not reached
		if (character.getX() != _posX || character.getY() != _posY) {
			return false;
		}

		// Character is sleeping
		if (character.isSleeping() && _item.isSleepingItem() == false) {
			Log.debug("use: sleeping . use canceled");
			JobManager.getInstance().close(this, _reason);
			return false;
		}

		if (check(character) == false) {
			JobManager.getInstance().close(this, _reason);
			return true;
		}

		Log.debug("Character #" + character.getName() + ": actionUse (" + _progress + ")");

		// Character using item
		if (_progress++ < _cost) {
			// Set running
			_status = JobStatus.RUNNING;

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

			return false;
		}

		if (_item.getInfo().isConsumable) {
			((ConsumableModel)_item).addQuantity(-1);
			if (_item.getQuantity() <= 0) {
				Game.getWorldManager().removeConsumable((ConsumableModel) _item);
			}
		}

		JobManager.getInstance().close(this);

		return true;
	}

	@Override
	public String getType() {
		return "use";
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// Item is no longer exists
		if (_item.isConsumable()) {
			if (_item != _character.getInventory() && _item != ServiceManager.getWorldMap().getConsumable(_item.getX(), _item.getY())) {
				_reason = JobAbortReason.INVALID;
				return false;
			}
		} else {
			if (_item != ServiceManager.getWorldMap().getItem(_item.getX(), _item.getY())) {
				_reason = JobAbortReason.INVALID;
				return false;
			}
		}

		if (_item.getQuantity() <= 0) {
			return false;
		}

//		// No space left in inventory
//		if (_item.isFactory() && character.hasInventorySpaceLeft() == false) {
//			_reason = JobAbortReason.NO_LEFT_CARRY;
//			return false;
//		}

		return true;
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

}

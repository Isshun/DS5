package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.Movable.Direction;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.*;

import java.util.List;

public class JobConsume extends BaseJob {

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

	public static JobConsume create(ItemBase item) {
		if (item == null || !item.hasFreeSlot()) {
			return null;
		}

		ItemInfo.ItemInfoAction infoAction = item.getInfo().actions.get(0);

		JobConsume job = new JobConsume();
		ItemSlot slot = item.takeSlot(job);
		if (slot != null) {
			job.setSlot(slot);
			job.setPosition(slot.getX(), slot.getY());
		} else {
			job.setPosition(item.getX(), item.getY());
		}
		job.setActionInfo(infoAction);
		job.setItem(item);
		job.setQuantityTotal(infoAction.cost);

		return job;
	}

	public static JobConsume create(CharacterModel character, ConsumableItem item) {
		if (character == null) {
			return null;
		}

		JobConsume job = create(item);
		if (job != null) {
			job.setCharacterRequire(character);
		}

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

		Log.debug("Character #" + character.getName() + ": actionUse");

		// Character using item
		if (_cost++ < _totalCost) {
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
			_item.use(_character, (int) (_totalCost - _cost));

			return false;
		}

		if (_item.getInfo().isConsomable) {
			((ConsumableItem)_item).addQuantity(-1);
			if (_item.getQuantity() <= 0) {
				Game.getWorldManager().removeConsumable((ConsumableItem) _item);
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
		if (_item.isConsomable()) {
			if (_item != ServiceManager.getWorldMap().getConsumable(_item.getX(), _item.getY())) {
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

		// Factory is empty
		if (_item.isFactory() && ((FactoryItem)_item).getInventory().size() == 0) {
			_reason = JobAbortReason.NO_COMPONENTS;
			return false;
		}

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

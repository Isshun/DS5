package alone.in.deepspace.model.job;

import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Movable.Direction;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemSlot;
import alone.in.deepspace.model.item.StorageItem;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.util.Log;

public class JobUse extends Job {

	private JobUse() {
		super();
	}

	public static Job create(ItemBase item) {
		if (item == null || !item.hasFreeSlot()) {
			return null;
		}

		JobUse job = new JobUse();
		ItemSlot slot = item.takeSlot(job);
		job.setSlot(slot);
		job.setPosition(slot.getX(), slot.getY());
		job.setAction(JobManager.Action.USE);
		job.setItem(item);
		job.setDurationLeft(item.getInfo().onAction.duration);

		return job;
	}

	public static Job create(ItemBase item, Character character) {
		if (character == null) {
			return null;
		}
		
		Job job = create(item);
		job.setCharacterRequire(character);
		
		return job;
	}

	// TODO: make objects stats table instead switch
	@Override
	public boolean action(Character character) {
		// Wrong call
		if (_item == null || character == null) {
			Log.error("wrong call");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;
		}
		
		// Item not reached
		if (character.getX() != _posX || character.getY() != _posY) {
			return false;
		}

		// Character is sleeping
		if (character.isSleeping() && _item.isSleepingItem() == false) {
			Log.debug("use: sleeping . use canceled");
			return false;
		}
		
		if (check(character) == false) {
			JobManager.getInstance().abort(this, _reason);
			return true;
		}
		
		Log.debug("Character #" + character.getName() + ": actionUse");

		// Character using item
		if (_durationLeft > 0) {
			// Set running
			_status = JobStatus.RUNNING;
			
			// Decrease duration
			decreaseDurationLeft();

			// Item is use by 2 or more character
			if (_item.getNbFreeSlots() + 1 < _item.getNbSlots()) {
				character.getNeeds().addRelation(1);
				List<ItemSlot> slots = _item.getSlots();
				for (ItemSlot slot: slots) {
					Character slotCharacter = slot.getJob() != null ? slot.getJob().getCharacter() : null;
					Game.getRelationManager().meet(character, slotCharacter);
				}
			}

			// Set character direction
			if (_item.getX() > _posX) { character.setDirection(Direction.RIGHT); }
			if (_item.getX() < _posX) { character.setDirection(Direction.LEFT); }
			if (_item.getY() > _posY) { character.setDirection(Direction.TOP); }
			if (_item.getY() < _posY) { character.setDirection(Direction.BOTTOM); }
			
			// Add item effects and create crafted item
			UserItem produce = _item.use(character, _durationLeft);
			if (produce != null) {
				character.addInventory(produce);
			}
		}
		
		// Check after action
		if (_durationLeft > 0) {
			return false;
		}

		JobManager.getInstance().complete(this);
		return true;
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_item.getX(), _item.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// No space left in inventory
		if (_item.isFactory() && character.hasInventorySpaceLeft() == false) {
			_reason = JobAbortReason.NO_LEFT_CARRY;
			return false;
		}
		
		// Factory is empty
		if (_item.isFactory() && ((StorageItem)_item).getInventory().size() == 0) {
			_reason = JobAbortReason.NO_COMPONENTS;
			return false;
		}
		
		return true;
	}

	@Override
	public String getLabel() {
		return "use " + _item.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "use " + _item.getLabel();
	}
}

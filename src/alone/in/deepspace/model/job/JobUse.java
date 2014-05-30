package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.ItemSlot;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.StorageItem;

public class JobUse extends Job {

	private JobUse() {
		super();
	}

	public static Job create(BaseItem item) {
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

	@Override
	public Abort check(Character character) {
		// Item is null
		if (_item == null) {
			return Abort.INVALID;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_item.getX(), _item.getY())) {
			return Abort.INVALID;
		}
		
		// No space left in inventory
		if (_item.isFactory() && character.hasInventorySpaceLeft() == false) {
			return Abort.NO_LEFT_CARRY;
		}
		
		// Factory is empty
		if (_item.isFactory() && ((StorageItem)_item).getInventory().size() == 0) {
			return Abort.NO_COMPONENTS;
		}
		
		return null;
	}

}

package alone.in.deepspace.model;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.util.Log;

public class JobUse extends Job {

	public JobUse(int id) {
		super(id);
	}

	public JobUse(int id, int x, int y) {
		super(id, x, y);
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

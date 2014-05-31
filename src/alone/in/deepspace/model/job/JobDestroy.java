package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.character.Character;

public class JobDestroy extends Job {

	private JobDestroy(int x, int y) {
		super(x, y);
	}

	public static Job create(BaseItem item) {
		if (item == null) {
			return null;
		}
		
		Job job = new JobDestroy(item.getX(), item.getY());
		job.setAction(JobManager.Action.DESTROY);
		job.setItem(item);
		return job;
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_item == null) {
			_reason = Abort.INVALID;
			return false;
		}
		
		// Item is no longer exists
		if (_item != ServiceManager.getWorldMap().getItem(_item.getX(), _item.getY())) {
			_reason = Abort.INVALID;
			return false;
		}
		
		// No space left in inventory
		if (_item.isFactory() && character.hasInventorySpaceLeft() == false) {
			_reason = Abort.NO_LEFT_CARRY;
			return false;
		}
		
		// Factory is empty
		if (_item.isFactory() && ((StorageItem)_item).getInventory().size() == 0) {
			_reason = Abort.NO_COMPONENTS;
			return false;
		}
		return true;
	}

	@Override
	public boolean action(Character character) {
		ResourceManager.getInstance().addMatter(1);
		ServiceManager.getWorldMap().removeItem(_item);
		JobManager.getInstance().complete(this);
		return true;
	}

}

package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.util.Log;

public class JobTake extends Job {

	private StorageItem		_storage;
	
	private JobTake(int x, int y) {
		super(x, y);
	}

	public static Job create(Character character, StorageItem storage, ItemFilter filter) {
		Log.debug("create take job");

		JobTake job = new JobTake(storage.getX(), storage.getY());
		job.setAction(JobManager.Action.TAKE);
		job.setItem(storage);
		job.setItemFilter(filter);
		job.setCharacterRequire(character);
		
		job._storage = storage;
		
		return job;
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_item == null || _storage == null) {
			_reason = Abort.INVALID;
			return false;
		}
		
		// No space left in inventory
		if (_character.hasInventorySpaceLeft() == false) {
			_reason = Abort.NO_LEFT_CARRY;
			return false;
		}
		
		// Storage not contains requested item
		if (_storage.contains(_filter) == false) {
			_reason = Abort.INVALID;
			return false;
		}

		return true;
	}

	@Override
	// TODO: check character inventory space
	public boolean action(Character character) {
		if (_item == null || _filter == null) {
			JobManager.getInstance().abort(this, Job.Abort.INVALID);
			Log.error("actionTake: invalid job");
			return true;
		}
		
		StorageItem storage = (StorageItem)_item;
		BaseItem neededItem = storage.get(_filter);
		if (neededItem != null) {
			character.addInventory(neededItem);
			storage.remove(neededItem);
		}

		JobManager.getInstance().complete(this);
		return true;
	}

}

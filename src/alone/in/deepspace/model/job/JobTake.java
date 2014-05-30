package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.StorageItem;
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
	public Abort check(Character character) {
		// Item is null
		if (_item == null || _storage == null) {
			return Abort.INVALID;
		}
		
		// No space left in inventory
		if (_character.hasInventorySpaceLeft() == false) {
			return Abort.NO_LEFT_CARRY;
		}
		
		// Storage not contains requested item
		if (_storage.contains(_filter) == false) {
			return Abort.INVALID;
		}

		return null;
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

package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.room.StorageRoom;
import alone.in.deepspace.util.Log;

public class JobTake extends Job {

	private StorageRoom		_storage;
	
	private JobTake(int x, int y) {
		super(x, y);
	}

	public static Job create(Character character, StorageRoom storage, ItemFilter filter) {
		Log.debug("create take job");

		JobTake job = new JobTake(storage.getX(), storage.getY());
		job.setAction(JobManager.Action.TAKE);
		job.setItemFilter(filter);
		job.setCharacterRequire(character);
		
		job._storage = storage;
		
		return job;
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_filter == null || _storage == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = JobAbortReason.NO_LEFT_CARRY;
			return false;
		}
		
		// Storage not contains requested item
		if (_storage.contains(_filter) == false) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		return true;
	}

	@Override
	public boolean action(Character character) {
		if (_storage == null || _filter == null) {
			JobManager.getInstance().abort(this, Job.JobAbortReason.INVALID);
			Log.error("actionTake: invalid job");
			return true;
		}
		
		if (character.hasInventorySpaceLeft() && _storage.contains(_filter)) {
			UserItem neededItem = _storage.take(_filter);
			character.addInventory(neededItem);
		}

		JobManager.getInstance().complete(this);
		return true;
	}

	@Override
	public String getLabel() {
		if (_filter.itemMatched != null) {
			return "take " + _filter.itemMatched.label;
		}
		return "take";
	}

	@Override
	public String getShortLabel() {
		if (_filter.itemMatched != null) {
			return "take " + _filter.itemMatched.label;
		}
		return "take";
	}
}

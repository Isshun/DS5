package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.util.Log;

public class JobRefill extends Job {

	private StorageItem _storage;
	private StorageItem _dispenser;

	private JobRefill(int x, int y) {
		super(x, y);
	}

	public static Job create(StorageItem dispenser, StorageItem storage, ItemFilter filter) {
		if (storage == null || dispenser == null || storage == dispenser) {
			Log.error("createRefillJob: wrong items");
			return null;
		}

		JobRefill job = new JobRefill(storage.getX(), storage.getY());
		job.setAction(JobManager.Action.REFILL);
		job.setSubAction(JobManager.Action.TAKE);
		dispenser.setRefillJob(job);
		job.setItem(storage);
		job.setItemFilter(filter);
		
		job._storage = storage;
		job._dispenser = dispenser;

		return job;
	}

	@Override
	public Abort check(Character character) {
		// Item is null
		if (_item == null || _storage == null || _dispenser == null || _filter == null) {
			return Abort.INVALID;
		}
		
		if (_subAction == Action.TAKE) {
			return checkTake();
		} else {
			return checkStore();
		}
	}

	private Abort checkStore() {
		// Dispenser no longer exists
		if (_dispenser != ServiceManager.getWorldMap().getItem(_dispenser.getX(), _dispenser.getY())) {
			return Abort.INVALID;
		}
		
		// Character inventory is empty
		if (_character.getInventory().size() == 0) {
			return Abort.NO_COMPONENTS;
		}

		return null;
	}

	private Abort checkTake() {
		// Storage no longer exists
		if (_storage != ServiceManager.getWorldMap().getItem(_storage.getX(), _storage.getY())) {
			return Abort.INVALID;
		}

		// Dispenser no longer exists
		if (_dispenser != ServiceManager.getWorldMap().getItem(_dispenser.getX(), _dispenser.getY())) {
			return Abort.INVALID;
		}

		// No space left in inventory
		if (_character.hasInventorySpaceLeft() == false) {
			return Abort.NO_LEFT_CARRY;
		}
		
		return null;
	}

	public StorageItem getDispenser() {
		return _dispenser;
	}

	@Override
	public boolean action(Character character) {
		Abort reason = check(character);
		if (reason != null) {
			JobManager.getInstance().abort(this, Job.Abort.INVALID);
			Log.error("actionRefill: invalid job");
			return true;
		}
		
		// Take in storage
		if (_subAction == Action.TAKE) {
			return actionTake(character);
		}
		
		// Refill dispenser
		else {
			return actionStore(character);
		}
	}

	private boolean actionStore(Character character) {
		if (_carryItems != null) {
			_dispenser.addInventory(_carryItems);
			character.removeInventory(_carryItems);
		}
		
		JobManager.getInstance().complete(this);
		return true;
	}

	private boolean actionTake(Character character) {
		BaseItem item = _storage.get(_filter);
		while (item != null && character.getInventoryLeftSpace() > 0) {
			addCarry(item);
			character.addInventory(item);
			_storage.remove(item);
			item = _storage.get(_filter);
		}

		// Change to STORE job
		setPosition(_dispenser.getX(), _dispenser.getY());
		setSubAction(Action.STORE);
		character.setJob(this);
		
		return false;
	}

}

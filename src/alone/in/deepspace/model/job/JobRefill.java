package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.JobManager.Action;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.FactoryItem;
import alone.in.deepspace.model.item.ItemBase;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.item.StorageItem;
import alone.in.deepspace.util.Log;

public class JobRefill extends Job {

	private StorageItem _storage;
	private StorageItem _factory;

	private JobRefill(int x, int y) {
		super(x, y);
	}

	public static Job create(FactoryItem factory, StorageItem storage, ItemFilter filter) {
		if (storage == null || factory == null || storage == factory) {
			Log.error("createRefillJob: wrong items");
			return null;
		}

		JobRefill job = new JobRefill(storage.getX(), storage.getY());
		job.setAction(JobManager.Action.REFILL);
		job.setSubAction(JobManager.Action.TAKE);
		factory.setRefillJob(job);
		job.setItem(storage);
		job.setItemFilter(filter);
		
		job._storage = storage;
		job._factory = factory;

		return job;
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_item == null || _storage == null || _factory == null || _filter == null) {
			_reason = Abort.INVALID;
			return false;
		}
		
		if (_subAction == Action.TAKE) {
			return checkTake(character);
		} else {
			return checkStore(character);
		}
	}

	private boolean checkStore(Character character) {
		// Dispenser no longer exists
		if (_factory != ServiceManager.getWorldMap().getItem(_factory.getX(), _factory.getY())) {
			_reason = Abort.INVALID;
			return false;
		}
		
		// Character inventory is empty
		if (_character.getInventory().size() == 0) {
			_reason = Abort.NO_COMPONENTS;
			return false;
		}

		return true;
	}

	private boolean checkTake(Character character) {
		// Storage no longer exists
		if (_storage != ServiceManager.getWorldMap().getItem(_storage.getX(), _storage.getY())) {
			_reason = Abort.INVALID;
			return false;
		}

		// Dispenser no longer exists
		if (_factory != ServiceManager.getWorldMap().getItem(_factory.getX(), _factory.getY())) {
			_reason = Abort.INVALID;
			return false;
		}

		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = Abort.NO_LEFT_CARRY;
			return false;
		}
		
		return true;
	}

	public StorageItem getDispenser() {
		return _factory;
	}

	@Override
	public boolean action(Character character) {
		if (check(character) == false) {
			JobManager.getInstance().abort(this, _reason);
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
			_factory.addInventory(_carryItems);
			character.removeInventory(_carryItems);
		}
		
		JobManager.getInstance().complete(this);
		return true;
	}

	private boolean actionTake(Character character) {
		ItemBase item = _storage.get(_filter);
		while (item != null && character.getInventoryLeftSpace() > 0) {
			addCarry(item);
			character.addInventory(item);
			_storage.remove(item);
			item = _storage.get(_filter);
		}

		// Change to STORE job
		setPosition(_factory.getX(), _factory.getY());
		setSubAction(Action.STORE);
		character.setJob(this);
		
		return false;
	}

	@Override
	public String getLabel() {
		return "refill " + _factory.getLabel();
	}

	@Override
	public String getShortLabel() {
		return "refill " + _factory.getLabel();
	}
}

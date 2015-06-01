package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.JobManager.Action;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.FactoryItem;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.room.StorageRoom;

import java.util.ArrayList;

public class JobRefill extends Job {
	private ArrayList<UserItem> 	_carryItems;
	private StorageRoom 			_storage;
	private FactoryItem 			_factory;

	private JobRefill(int x, int y) {
		super(null, x, y);
	}

	public static Job create(FactoryItem factory, StorageRoom storage, ItemFilter filter) {
		if (storage == null || factory == null) {
			Log.error("createRefillJob: wrong items");
			return null;
		}

		JobRefill job = new JobRefill(storage.getX(), storage.getY());
		job.setAction(JobManager.Action.REFILL);
		job.setSubAction(JobManager.Action.TAKE);
		factory.setRefillJob(job);
		job.setItem(factory);
		job.setItemFilter(filter);
		
		job._carryItems = new ArrayList<UserItem>();
		job._storage = storage;
		job._factory = factory;

		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		// Item is null
		if (_item == null || _storage == null || _factory == null || _filter == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		if (_subAction == Action.TAKE) {
			return checkTake(character);
		} else {
			return checkStore(character);
		}
	}

	private boolean checkStore(CharacterModel character) {
		// Dispenser no longer exists
		if (_factory != ServiceManager.getWorldMap().getItem(_factory.getX(), _factory.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Character inventory is empty
		if (_character.getInventory().size() == 0) {
			_reason = JobAbortReason.NO_COMPONENTS;
			return false;
		}

		return true;
	}

	private boolean checkTake(CharacterModel character) {
		// Storage no longer exists
		if (Game.getRoomManager().getRoomList().contains(_storage) == false) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// Dispenser no longer exists
		if (_factory != ServiceManager.getWorldMap().getItem(_factory.getX(), _factory.getY())) {
			_reason = JobAbortReason.INVALID;
			return false;
		}

		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = JobAbortReason.NO_LEFT_CARRY;
			return false;
		}
		
		return true;
	}

	public FactoryItem getDispenser() {
		return _factory;
	}

	@Override
	public boolean action(CharacterModel character) {
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

	private boolean actionStore(CharacterModel character) {
		if (_carryItems != null) {
			_factory.addInventory(_carryItems);
			character.removeInventory(_carryItems);
		}
		
		JobManager.getInstance().complete(this);
		return true;
	}

	private boolean actionTake(CharacterModel character) {
		while (character.hasInventorySpaceLeft() && _storage.contains(_filter)) {
			UserItem item = _storage.take(_filter);
			character.addInventory(item);
			_carryItems.add(item);
		}

		// Change to STORE job
		setPosition(_factory.getX(), _factory.getY());
		setSubAction(Action.STORE);
		character.setJob(this);
		
		return false;
	}

	@Override
	public String getLabel() {
		return "refill " + _factory.getLabel() + _subAction;
	}

	@Override
	public String getShortLabel() {
		return "refill " + _factory.getLabel() + _subAction;
	}
}

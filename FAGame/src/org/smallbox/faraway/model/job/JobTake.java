package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.ItemFilter;
import org.smallbox.faraway.model.item.UserItem;
import org.smallbox.faraway.model.room.StorageRoom;

public class JobTake extends JobModel {

	private StorageRoom		_storage;
	
	private JobTake(int x, int y) {
		super(null, x, y);
	}

	public static JobModel create(UserItem item) {
		Log.debug("create take job");

		JobTake job = new JobTake(item.getX(), item.getY());
		job.setAction(JobManager.Action.TAKE);
		job.setItem(item);
		
		return job;
	}

	public static JobModel create(CharacterModel character, StorageRoom storage, ItemFilter filter) {
		Log.debug("create take job");

		JobTake job = new JobTake(storage.getX(), storage.getY());
		job.setAction(JobManager.Action.TAKE);
		job.setItemFilter(filter);
		job.setCharacterRequire(character);
		
		job._storage = storage;
		
		return job;
	}

	@Override
	public boolean check(CharacterModel character) {
		// No space left in inventory
		if (character.hasInventorySpaceLeft() == false) {
			_reason = JobAbortReason.NO_LEFT_CARRY;
			return false;
		}
		
		// Item is not in storage nor on the floor
		if (_storage == null && _item == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// Check for item in storage room
		if (_storage != null && checkInStorage()) {
			return true;
		}

		// Check for item on floor
		else if (_item != null && checkOnFloor()) {
			return true;
		}
		
		return false;
	}

	private boolean checkOnFloor() {
		// Item is not on target location
		if (_item != Game.getWorldManager().getItem(_posX, _posY)) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		return true;
	}

	private boolean checkInStorage() {
		// filter is null
		if (_filter == null) {
			_reason = JobAbortReason.INVALID;
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
	public boolean action(CharacterModel character) {
		// Take item in storage room
		if (_storage != null) {
			if (_filter == null) {
				JobManager.getInstance().abort(this, JobModel.JobAbortReason.INVALID);
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
		
		// Take item on floor
		else if (_item != null) {
			if (character.hasInventorySpaceLeft() && _item == Game.getWorldManager().getItem(_posX, _posY)) {
				UserItem neededItem = Game.getWorldManager().takeItem(_posX, _posY);
				character.addInventory(neededItem);
			}

			JobManager.getInstance().complete(this);
			return true;
		}

		JobManager.getInstance().abort(this, JobModel.JobAbortReason.INVALID);
		Log.error("actionTake: invalid job");
		return true;
	}

	@Override
	public String getType() {
		return "take";
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
		if (_item != null) {
			return "take " + _item.getLabel();
		}
		if (_filter != null && _filter.itemMatched != null) {
			return "take " + _filter.itemMatched.label;
		}
		return "take";
	}
}

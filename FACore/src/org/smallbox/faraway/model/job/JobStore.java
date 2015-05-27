package org.smallbox.faraway.model.job;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.engine.util.Log;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.item.ItemBase;
import org.smallbox.faraway.model.room.StorageRoom;

public class JobStore extends Job {

	private StorageRoom _storage;

	private JobStore(int x, int y) {
		super(x, y);
	}

	// TODO: check if item to store is accepter by storage
	public static Job create(Character character, StorageRoom storage) {
		if (storage == null) {
			Log.error("createStoreJob: storage cannot be null");
			return null;
		}
		
		if (character == null) {
			Log.error("createStoreJob: character cannot be null");
			return null;
		}

		if (character.getInventory().size() == 0) {
			Log.error("addStoreJob: character inventory cannot be empty");
			return null;
		}
		
		JobStore job = new JobStore(storage.getX(), storage.getY());
		job.setAction(JobManager.Action.STORE);
		job.setCharacterRequire(character);
		
		job._storage = storage;
		
		return job;
	}

	public static Job create(Character character) {
		if (character == null) {
			Log.error("createStoreJob: character cannot be null");
			return null;
		}

		ItemBase itemToStore = character.getInventory().get(0);
		StorageRoom storage = Game.getRoomManager().getNearestStorage(character.getX(), character.getY(), itemToStore);
		if (storage == null) {
			return null;
		}
		
		return create(character, storage);
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_storage == null) {
			_reason = JobAbortReason.INVALID;
			return false;
		}
		
		// TODO: storage is not empty
		// TODO: storage accept item

		return true;
	}

	// TODO: add inventory filter
	@Override
	public boolean action(Character character) {
		if (_storage == null) {
			Log.error("Character: actionStore on non storage item");
			JobManager.getInstance().abort(this, JobAbortReason.INVALID);
			return true;		
		}

		_storage.store(character.getInventory());
		character.clearInventory();
		JobManager.getInstance().complete(this);
		return true;
	}

	@Override
	public String getLabel() {
		return "store";
	}

	@Override
	public String getShortLabel() {
		return "store";
	}

}

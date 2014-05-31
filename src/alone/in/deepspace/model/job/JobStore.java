package alone.in.deepspace.model.job;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.BaseItem;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.util.Log;

public class JobStore extends Job {

	private StorageItem _storage;

	private JobStore(int x, int y) {
		super(x, y);
	}

	// TODO: check if item to store is accepter by storage
	public static Job create(Character character, StorageItem storage) {
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
		job.setItem(storage);
		job.setCharacterRequire(character);
		
		job._storage = storage;
		
		return job;
	}

	public static Job create(Character character) {
		if (character == null) {
			Log.error("createStoreJob: character cannot be null");
			return null;
		}

		BaseItem itemToStore = character.getInventory().get(0);
		StorageItem storage = ServiceManager.getWorldMap().getNearestStorage(character.getX(), character.getY(), itemToStore);
		
		return create(character, storage);
	}

	@Override
	public boolean check(Character character) {
		// Item is null
		if (_item == null || _storage == null) {
			_reason = Abort.INVALID;
			return false;
		}

		return true;
	}

	// TODO: add inventory filter
	@Override
	public boolean action(Character character) {
		if (_storage == null) {
			Log.error("Character: actionStore on non storage item");
			JobManager.getInstance().abort(this, Abort.INVALID);
			return true;		
		}

		_storage.addInventory(character.getInventory());
		character.clearInventory();
		JobManager.getInstance().complete(this);
		return true;
	}

}

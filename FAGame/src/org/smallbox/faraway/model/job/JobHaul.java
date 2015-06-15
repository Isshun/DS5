//package org.smallbox.faraway.model.job;
//
//import org.smallbox.faraway.Game;
//import org.smallbox.faraway.engine.util.Log;
//import org.smallbox.faraway.manager.JobManager;
//import org.smallbox.faraway.model.character.CharacterModel;
//import org.smallbox.faraway.model.item.ItemBase;
//
//public class JobHaul extends BaseJob {
//
//	private JobHaul(int x, int y) {
//		super(null, x, y);
//	}
//
//	// TODO: check if item to store is accepter by storage
//	public static BaseJob create(CharacterModel character, StorageRoom storage) {
//		if (storage == null) {
//			Log.error("createStoreJob: storage cannot be null");
//			return null;
//		}
//
//		if (character == null) {
//			Log.error("createStoreJob: character cannot be null");
//			return null;
//		}
//
//		if (character.getComponents().size() == 0) {
//			Log.error("addStoreJob: character inventory cannot be empty");
//			return null;
//		}
//
//		JobHaul job = new JobHaul(storage.getX(), storage.getY());
//		job.setCharacterRequire(character);
//
//		job._storage = storage;
//
//		return job;
//	}
//
//	public static BaseJob create(CharacterModel character) {
//		if (character == null) {
//			Log.error("createStoreJob: character cannot be null");
//			return null;
//		}
//
//		ItemBase itemToStore = character.getComponents().getRoom(0);
//		StorageRoom storage = Game.getRoomManager().getNearestFreeStorage(character.getX(), character.getY(), itemToStore);
//		if (storage == null) {
//			return null;
//		}
//
//		return create(character, storage);
//	}
//
//	@Override
//	public boolean check(CharacterModel character) {
//		// Item is null
//		if (_storage == null) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}
//
//		// TODO: storage is not empty
//		// TODO: storage accept item
//
//		return true;
//	}
//
//	// TODO: add inventory filter
//	@Override
//	public boolean action(CharacterModel character) {
//		if (_storage == null) {
//			Log.error("Character: actionStore on non storage item");
//			JobManager.getInstance().quit(this, JobAbortReason.INVALID);
//			return true;
//		}
//
//		_storage.store(character.getComponents());
//		character.clearInventory();
//		JobManager.getInstance().close(this);
//		return true;
//	}
//
//	@Override
//	public String getType() {
//		return "store";
//	}
//
//	@Override
//	public boolean canBeResume() {
//		return false;
//	}
//
//	@Override
//	public CharacterModel.TalentType getTalentNeeded() {
//		return CharacterModel.TalentType.HAUL;
//	}
//
//	@Override
//	public String getLabel() {
//		return "store";
//	}
//
//	@Override
//	public String getShortLabel() {
//		return "store";
//	}
//
//}

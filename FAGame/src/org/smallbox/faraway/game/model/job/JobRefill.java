//package org.smallbox.faraway.game.model.job;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.util.Log;
//import org.smallbox.faraway.game.manager.JobManager;
//import org.smallbox.faraway.game.manager.ServiceManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.FactoryItem;
//import org.smallbox.faraway.game.model.item.ItemFilter;
//import org.smallbox.faraway.game.model.item.UserItem;
//import org.smallbox.faraway.game.model.room.StorageRoom;
//
//import java.util.ArrayList;
//
//public class JobRefill extends BaseJob {
//	private ArrayList<UserItem> 	_carryItems;
//	private StorageRoom 			_storage;
//	private FactoryItem 			_factory;
//
//	private JobRefill(int x, int y) {
//		super(null, x, y);
//	}
//
//	public static BaseJob create(FactoryItem factory, StorageRoom storage, ItemFilter filter) {
//		if (storage == null || factory == null) {
//			Log.error("createRefillJob: wrong items");
//			return null;
//		}
//
//		JobRefill job = new JobRefill(storage.getX(), storage.getY());
//		factory.setRefillJob(job);
//		job.setItem(factory);
//		job.setItemFilter(filter);
//
//		job._carryItems = new ArrayList<UserItem>();
//		job._storage = storage;
//		job._factory = factory;
//
//		return job;
//	}
//
//	@Override
//	public boolean check(CharacterModel character) {
//		// Item is null
//		if (_item == null || _storage == null || _factory == null || _filter == null) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}
//
//		//TODO
////		if (_subAction == ActionType.TAKE) {
////			return checkTake(character);
////		} else {
////			return checkStore(character);
////		}
//		return null;
//	}
//
//	private boolean checkStore(CharacterModel character) {
//		// Dispenser no longer exists
//		if (_factory != Game.getWorldManager().getItem(_factory.getX(), _factory.getY())) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}
//
//		// Character inventory is empty
//		if (_character.getComponents().size() == 0) {
//			_reason = JobAbortReason.NO_COMPONENTS;
//			return false;
//		}
//
//		return true;
//	}
//
//	private boolean checkTake(CharacterModel character) {
//		// Storage no longer exists
//		if (Game.getRoomManager().getRoomList().contains(_storage) == false) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}
//
//		// Dispenser no longer exists
//		if (_factory != Game.getWorldManager().getItem(_factory.getX(), _factory.getY())) {
//			_reason = JobAbortReason.INVALID;
//			return false;
//		}
//
//		// No space left in inventory
//		if (character.hasInventorySpaceLeft() == false) {
//			_reason = JobAbortReason.NO_LEFT_CARRY;
//			return false;
//		}
//
//		return true;
//	}
//
//	public FactoryItem getDispenser() {
//		return _factory;
//	}
//
//	@Override
//	public boolean action(CharacterModel character) {
//		if (check(character) == false) {
//			JobManager.getInstance().quit(this, _reason);
//			Log.error("actionRefill: invalid job");
//			return true;
//		}
//
//		// Take in storage
//		if (_subAction == ActionType.TAKE) {
//			return actionTake(character);
//		}
//
//		// Refill dispenser
//		else {
//			return actionStore(character);
//		}
//	}
//
//	@Override
//	public String getType() {
//		return "refill";
//	}
//
//	private boolean actionStore(CharacterModel character) {
//		if (_carryItems != null) {
//			_factory.addComponent(_carryItems);
//			character.removeInventory(_carryItems);
//		}
//
//		JobManager.getInstance().close(this);
//		return true;
//	}
//
//	private boolean actionTake(CharacterModel character) {
//		while (character.hasInventorySpaceLeft() && _storage.contains(_filter)) {
//			UserItem item = _storage.take(_filter);
//			character.addComponent(item);
//			_carryItems.add(item);
//		}
//
//		// Change to STORE job
//		setPosition(_factory.getX(), _factory.getY());
//		setSubAction(ActionType.STORE);
//		character.setHaul(this);
//
//		return false;
//	}
//
//	@Override
//	public boolean canBeResume() {
//		return false;
//	}
//
//	@Override
//	public String getLabel() {
//		return "refill " + _factory.getLabel() + _subAction;
//	}
//
//	@Override
//	public String getShortLabel() {
//		return "refill " + _factory.getLabel() + _subAction;
//	}
//}

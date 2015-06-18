//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.game.manager.JobManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.ItemBase;
//import org.smallbox.faraway.game.model.item.ItemFilter;
//import org.smallbox.faraway.game.model.job.JobTake;
//import org.smallbox.faraway.game.model.job.JobUse;
//import org.smallbox.faraway.game.model.job.JobUseInventory;
//import org.smallbox.faraway.game.model.room.StorageRoom;
//
//// TODO: change name by filter
//public class CharacterIsHungry implements CharacterCheck {
//
//	@Override
//	public boolean create(JobManager jobManager, CharacterModel character) {
//		if (character.getNeeds().isHungry()) {
//			ItemFilter consomableItemFilter = ItemFilter.createConsomableFilter();
//			consomableItemFilter.effectFood = true;
//
//			// Have item in inventory
//			ItemBase item = character.find(consomableItemFilter);
//			if (item != null) {
//				jobManager.addJob(JobUseInventory.create(character, item), character);
//				return true;
//			}
//
//			// Take item from storage
//			StorageRoom storage = Game.getRoomManager().findStorageContains(consomableItemFilter, character.getX(), character.getY());
//			if (storage != null) {
//				jobManager.addJob(JobTake.create(character, storage, consomableItemFilter), character);
//				return true;
//			}
//
//			// Looking for food dispenser
//			ItemFilter factoryFilter = ItemFilter.createFactoryFilter();
//			factoryFilter.effectFood = true;
//			item = Game.getWorldFinder().getNearest(factoryFilter, character);
//			if (item != null) {
//				jobManager.addJob(JobUse.create(item), character);
//				return true;
//			}
//		}
//		return false;
//	}
//}

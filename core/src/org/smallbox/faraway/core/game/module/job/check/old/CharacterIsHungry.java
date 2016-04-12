//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import JobModule;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.ItemBase;
//import org.smallbox.faraway.game.model.item.ItemFilter;
//import org.smallbox.faraway.game.model.job.JobTake;
//import org.smallbox.faraway.game.model.job.UseJob;
//import org.smallbox.faraway.game.model.job.JobUseInventory;
//import org.smallbox.faraway.game.model.room.StorageRoom;
//
//// TODO: change name by filter
//public class CharacterIsHungry implements CharacterCheck {
//
//    @Override
//    public boolean onGameInit(JobModule jobManager, CharacterModel characters) {
//        if (characters.getNeeds().isHungry()) {
//            ItemFilter consomableItemFilter = ItemFilter.createConsumableFilter();
//            consomableItemFilter.effectFood = true;
//
//            // Have item in inventory
//            ItemBase item = characters.find(consomableItemFilter);
//            if (item != null) {
//                jobManager.addJob(JobUseInventory.onGameInit(characters, item), characters);
//                return true;
//            }
//
//            // Take item from storage
//            StorageRoom storage = Game.getRoomManager().findStorageContains(consomableItemFilter, characters.getX(), characters.getY());
//            if (storage != null) {
//                jobManager.addJob(JobTake.onGameInit(characters, storage, consomableItemFilter), characters);
//                return true;
//            }
//
//            // Looking for food dispenser
//            ItemFilter factoryFilter = ItemFilter.createFactoryFilter();
//            factoryFilter.effectFood = true;
//            item = Game.getWorldFinder().getNearest(factoryFilter, characters);
//            if (item != null) {
//                jobManager.addJob(UseJob.onGameInit(item), characters);
//                return true;
//            }
//        }
//        return false;
//    }
//}

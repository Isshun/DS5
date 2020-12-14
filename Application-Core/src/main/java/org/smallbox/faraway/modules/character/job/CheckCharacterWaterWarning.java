//package org.smallbox.faraway.modules.character.job;
//
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
//import org.smallbox.faraway.core.module.world.model.ConsumableItem;
//import org.smallbox.faraway.core.module.world.model.ItemFilter;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.item.ItemFinderModule;
//import org.smallbox.faraway.modules.item.UsableItem;
//import org.smallbox.faraway.modules.job.JobModel;
//
///**
// * Created by Alex on 01/06/2015.
// */
//public class CheckCharacterWaterWarning extends CharacterCheck {
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel character) {
//        return character.getType().needs.water != null && character.getNeeds().get("drink") < character.getType().needs.water.warning;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getType().needs.water != null && character.getNeeds().get("drink") < character.getType().needs.water.warning;
//    }
//
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
//        ConsumableItem consumable = null;
//        ItemFilter consumableFilter = ItemFilter.createConsumableFilter();
//        consumableFilter.effectDrink = true;
//
//        // Get consumable on inventory
//        if (character.getInventory() != null && character.getInventory().matchFilter(consumableFilter)) {
//            consumable = character.getInventory();
//        }
//
////        // Get consumable on ground
////        ConsumableItem nearestConsumable = (ConsumableItem)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(consumableFilter, character);
////        if (nearestConsumable != null && nearestConsumable.hasFreeSlot()) {
////            consumable = nearestConsumable;
////        }
//
//        UsableItem item = null;
//        ItemFilter itemFilter = ItemFilter.createItemFilter();
//        itemFilter.effectDrink = true;
//
//        // Get drink provider
//        UsableItem nearestItem = (UsableItem)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(itemFilter, character);
//        if (nearestItem != null && nearestItem.hasFreeSlot()) {
//            item = nearestItem;
//        }
//
////        if (item != null && consumable != null) {
////            if (WorldHelper.getDistance(character.getParcel(), consumable.getParcel()) < WorldHelper.getDistance(character.getParcel(), item.getParcel())) {
////                return ConsumeJob.create(character, consumable);
////            } else {
////                return UseJob.create(character, item);
////            }
////        }
////
////        if (item != null) {
////            return UseJob.create(character, item);
////        }
////
////        if (consumable != null) {
////            return ConsumeJob.create(character, consumable);
////        }
//
//        return null;
//    }
//
//    @Override
//    public String getLabel() {
//        return "A soif";
//    }
//}

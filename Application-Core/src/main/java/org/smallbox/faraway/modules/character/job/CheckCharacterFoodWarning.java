//package org.smallbox.faraway.modules.character.job;
//
//import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
//import org.smallbox.faraway.core.module.world.model.ItemFilter;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.job.JobModel;
//
///**
// * Created by Alex on 01/06/2015.
// */
//public class CheckCharacterFoodWarning extends CharacterCheck {
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel character) {
//        return character.getType().needs.food != null && character.getNeeds().get("food") < character.getType().needs.food.warning;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getType().needs.food != null && character.getNeeds().get("food") < character.getType().needs.food.warning;
//    }
//
//    // TODO: change name by filter
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
//        ItemFilter filter = ItemFilter.createConsumableFilter();
//        filter.effectFood = true;
//
////        // Get consumable on inventory
////        if (character.getInventory() != null && character.getInventory().matchFilter(filter)) {
////            return ConsumeJob.create(character, character.getInventory());
////        }
//
////        // Get consumable on ground
////        ConsumableItem nearestItem = (ConsumableItem)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(filter, character);
////        if (nearestItem != null && nearestItem.hasFreeSlot()) {
////            return ConsumeJob.create(character, nearestItem);
////        }
//
//        return null;
//    }
//
//    @Override
//    public String getLabel() {
//        return "A faim";
//    }
//}

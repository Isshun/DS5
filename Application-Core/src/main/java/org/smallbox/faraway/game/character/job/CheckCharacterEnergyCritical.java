//package org.smallbox.faraway.modules.character.job;
//
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.modules.job.check.old.CharacterCheck;
//import org.smallbox.faraway.core.world.model.ItemFilter;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.item.ItemFinderModule;
//import org.smallbox.faraway.modules.item.UsableItem;
//import org.smallbox.faraway.modules.item.job.SleepJob;
//import org.smallbox.faraway.modules.job.JobModel;
//
///**
// * Created by Alex
// */
//public class CheckCharacterEnergyCritical extends CharacterCheck {
//    private final ItemFilter bedFilter;
//    private final ItemFilter consumableFilter;
//
//    public CheckCharacterEnergyCritical() {
//        bedFilter = ItemFilter.createItemFilter();
//        bedFilter.effectEnergy = true;
//        bedFilter.needItem = true;
//        bedFilter.needFreeSlot = true;
//
//        consumableFilter = ItemFilter.createItemFilter();
//        consumableFilter.effectEnergy = true;
//        consumableFilter.needConsumable = true;
//    }
//
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
//        // Go to nearest bed
//        UsableItem item = (UsableItem)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(bedFilter, character);
//        if (item != null) {
//            return new SleepJob(item.getParcel(), item);
//        }
//
////        // Use energy consumable
////        ConsumableItem consumable = (ConsumableItem) ((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(consumableFilter, character);
////        if (consumable != null) {
////            return ConsumeJob.create(character, consumable);
////        }
//
//        // Sleep on ground
//        return new SleepJob(character.getParcel());
//    }
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel character) {
//        return true;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getNeeds().get("energy") < character.getType().needs.energy.critical;
//    }
//
//    @Override
//    public String getLabel() {
//        return "Meurt de sommeil";
//    }
//}

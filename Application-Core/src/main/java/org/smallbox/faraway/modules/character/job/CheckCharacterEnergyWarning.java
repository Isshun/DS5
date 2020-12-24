//package org.smallbox.faraway.modules.character.job;
//
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
//import org.smallbox.faraway.core.module.world.model.ItemFilter;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.item.ItemFinderModule;
//import org.smallbox.faraway.modules.item.UsableItem;
//import org.smallbox.faraway.modules.item.job.SleepJob;
//import org.smallbox.faraway.modules.job.JobModel;
//
///**
// * Created by Alex
// */
//public class CheckCharacterEnergyWarning extends CharacterCheck {
//    private final ItemFilter bedFilter;
//    private final ItemFilter consumableFilter;
//
//    public CheckCharacterEnergyWarning() {
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
//    public boolean isJobLaunchable(CharacterModel character) {
//        ItemFinderModule finder = (ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class);
//        return finder.getNearest(bedFilter, character) != null || finder.getNearest(consumableFilter, character) != null;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getNeeds().get("energy") < character.getType().needs.energy.warning && character.getNeeds().get("energy") >= character.getType().needs.energy.critical;
//    }
//
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
//        // Get nearest bed
//        UsableItem item = (UsableItem)((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(bedFilter, character);
//        if (item != null) {
//            return new SleepJob(item.getParcel(), item);
//        }
//
////        // Get energy consumable
////        ConsumableItem consumable = (ConsumableItem) ((ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class)).getNearest(consumableFilter, character);
////        if (consumable != null) {
////            return ConsumeJob.create(character, consumable);
////        }
//
//        return null;
//    }
//
//    @Override
//    public String getLabel() {
//        return "A sommeil";
//    }
//}

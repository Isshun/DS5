//package org.smallbox.faraway.modules.character.job;
//
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.module.job.check.old.CharacterCheck;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.core.module.world.model.ItemFilter;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.item.ItemFinderModule;
//import org.smallbox.faraway.modules.item.UsableItem;
//import org.smallbox.faraway.modules.item.job.SleepJob;
//
///**
// * Created by Alex
// */
//public class CheckCharacterTimetableSleep extends CharacterCheck {
//    private final ItemFilter bedFilter;
//
//    public CheckCharacterTimetableSleep() {
//        bedFilter = ItemFilter.createItemFilter();
//        bedFilter.effectEnergy = true;
//        bedFilter.needItem = true;
//        bedFilter.needFreeSlot = true;
//    }
//
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
//        ItemFinderModule finder = (ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class);
//        UsableItem item = (UsableItem)finder.getNearest(bedFilter, character);
//        if (item != null) {
//            SleepJob job = new SleepJob(item.getParcel(), item);
//            job.setCharacterRequire(character);
//            return job;
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel character) {
//        ItemFinderModule finder = (ItemFinderModule) Application.moduleManager.getModule(ItemFinderModule.class);
//        return finder.getNearest(bedFilter, character) != null;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getTimetable().get(Application.gameManager.getGame().getHour()) == 1;
//    }
//}

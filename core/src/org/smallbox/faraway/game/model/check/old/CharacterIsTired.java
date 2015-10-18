//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import JobModule;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.ItemBase;
//import org.smallbox.faraway.game.model.item.ItemFilter;
//import org.smallbox.faraway.game.model.job.JobUse;
//
///**
// * Check if characters is tired then send to bed
// *
// */
//public class CharacterIsTired implements CharacterCheck {
//
//    @Override
//    public boolean onCreate(JobModule jobManager, CharacterModel characters) {
//        if (characters.getNeeds().isExhausted()) {
//            ItemFilter filter = ItemFilter.createUsableFilter();
//            filter.effectEnergy = true;
//
//            // Character has quarters
//            if (characters.getQuarter() != null) {
//                ItemBase item = characters.getQuarter().find(filter);
//                if (item != null) {
//                    jobManager.addJob(JobUse.onCreate(item, characters), characters);
//                    return true;
//                }
//            }
//
//            // No quarters or no usable bed in quarters
//            ItemBase item = Game.getWorldFinder().getNearest(filter, characters);
//            if (item != null) {
//                jobManager.addJob(JobUse.onCreate(item, characters), characters);
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//}

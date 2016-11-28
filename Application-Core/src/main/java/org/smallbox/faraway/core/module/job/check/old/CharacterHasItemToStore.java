//package org.smallbox.faraway.game.model.check.old;
//
//import JobModule;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.JobModel;
//import org.smallbox.faraway.game.model.job.StoreJob;
//
//// Character has item to store
//public class CharacterHasItemToStore implements CharacterCheck {
//
//    @Override
//    public boolean onGameInit(JobModule jobManager, CharacterModel characters) {
//        if (characters.getShoppingList().size() > 0) {
//            JobModel job = StoreJob.onGameInit(characters);
//            if (job != null) {
//                jobManager.addJob(job, characters);
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//}

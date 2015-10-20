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
//    public boolean onCreate(JobModule jobManager, CharacterModel characters) {
//        if (characters.getComponents().size() > 0) {
//            JobModel job = StoreJob.onCreate(characters);
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

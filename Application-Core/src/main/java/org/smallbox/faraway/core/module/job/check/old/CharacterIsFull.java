//package org.smallbox.faraway.game.model.check.old;
//
//import JobModule;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.JobModel;
//import org.smallbox.faraway.game.model.job.StoreJob;
//
//public class CharacterIsFull implements CharacterCheck {
//
//    @Override
//    public boolean onGameInit(JobModule jobManager, CharacterModel characters) {
//        if (characters.isFull()) {
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

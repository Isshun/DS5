//package org.smallbox.faraway.game.model.check;
//
//import org.smallbox.faraway.game.module.character.JobModule;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.onCheck.old.CharacterCheck;
//import org.smallbox.faraway.game.model.job.JobModel;
//
///**
// * Created by Alex on 01/06/2015.
// */
//public class CheckCook extends CharacterCheck {
//
//    public CheckCook(CharacterModel characters) {
//        super(characters);
//    }
//
//    @Override
//    public boolean onCreate(JobModule jobManager) {
//        int bestDistance = Integer.MAX_VALUE;
//        JobModel bestJob = null;
//        for (JobModel job: jobManager.getJobs()) {
//            if (job.isFree() && !job.isFinish() && "cook".equals(job.getElevation()) && job.getDistance(_character) < bestDistance) {
//                bestDistance = job.getDistance(_character);
//                bestJob = job;
//            }
//        }
//
//        if (bestJob != null) {
//            _character.setHaul(bestJob);
//            return true;
//        }
//
//        return false;
//    }
//}

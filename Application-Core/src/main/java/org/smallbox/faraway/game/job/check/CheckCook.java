//package org.smallbox.faraway.game.model.check;
//
//import JobModule;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.onCheck.old.CharacterCheck;
//import org.smallbox.faraway.game.model.job.JobModel;
//
///**
// * Created by Alex
// */
//public class CheckCook extends CharacterCheck {
//
//    public CheckCook(CharacterModel characters) {
//        super(characters);
//    }
//
//    @Override
//    public boolean onGameInit(JobModule jobManager) {
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

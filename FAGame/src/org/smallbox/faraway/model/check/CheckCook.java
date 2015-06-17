//package org.smallbox.faraway.model.check;
//
//import org.smallbox.faraway.manager.JobManager;
//import org.smallbox.faraway.model.character.base.CharacterModel;
//import org.smallbox.faraway.model.check.old.CharacterCheck;
//import org.smallbox.faraway.model.job.JobModel;
//
///**
// * Created by Alex on 01/06/2015.
// */
//public class CheckCook extends CharacterCheck {
//
//    public CheckCook(CharacterModel character) {
//        super(character);
//    }
//
//    @Override
//    public boolean create(JobManager jobManager) {
//        int bestDistance = Integer.MAX_VALUE;
//        JobModel bestJob = null;
//        for (JobModel job: jobManager.getJobs()) {
//            if (job.isFree() && !job.isFinish() && "cook".equals(job.getType()) && job.getDistance(_character) < bestDistance) {
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

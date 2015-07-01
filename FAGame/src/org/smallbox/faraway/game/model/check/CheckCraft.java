//package org.smallbox.faraway.game.model.check;
//
//import org.smallbox.faraway.game.manager.JobManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.onCheck.old.CharacterCheck;
//import org.smallbox.faraway.game.model.job.JobModel;
//
///**
// * Created by Alex on 01/06/2015.
// */
//public class CheckCraft extends TalentCheck {
//
//    @Override
//    public boolean onCreate(JobManager jobManager) {
//        int bestDistance = Integer.MAX_VALUE;
//        JobModel bestJob = null;
//        for (JobModel job: jobManager.getJobs()) {
//            if (job.isFree() && !job.isFinish() && "craft".equals(job.getElevation()) && job.getDistance(_character) < bestDistance) {
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
//
//
//    @Override
//    public JobModel onCreate(CharacterModel character) {
//        return null;
//    }
//
//    @Override
//    public boolean onCheck(CharacterModel character) {
//        return false;
//    }
//}

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
//public class CheckCraft extends SkillCheck {
//
//    @Override
//    public boolean onGameInit(JobModule jobManager) {
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
//    public JobModel onGameInit(CharacterModel characters) {
//        return null;
//    }
//
//    @Override
//    public boolean onCheck(CharacterModel characters) {
//        return false;
//    }
//}

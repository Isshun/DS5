//package org.smallbox.faraway.modules.job.check.joy;
//
//import org.apache.commons.lang3.NotImplementedException;
//import org.smallbox.faraway.modules.job.check.old.CharacterCheck;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//
///**
// * Created by Alex
// */
//public class CheckJoyTalk extends CharacterCheck {
//    @Override
//    public JobModel onCreateJob(CharacterModel character) {
////        return JobTalk.createGame(character, getBestCharacter(character));
//        return null;
//    }
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel character) {
////        return getBestCharacter(character) != null;
//        return false;
//    }
//
//    private CharacterModel getBestCharacter(CharacterModel character) {
//        throw new NotImplementedException("");
//
////        int bestDistance = Integer.MAX_VALUE;
////        CharacterModel bestCharacter = null;
////        for (CharacterModel friend: ModuleHelper.getCharacterModule().getCharacters()) {
////            if (friend != character && friend.isAlive() && (friend.getJob() == null || friend.getJob().isEntertainment())) {
////                PathModel path = Application.pathManager.getPath(character.getParcel(), friend.getParcel(), true, false);
////                if (path != null && path.getLength() < bestDistance) {
////                    bestDistance = path.getLength();
////                    bestCharacter = friend;
////                }
////            }
////        }
////        return bestCharacter;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel character) {
//        return character.getNeeds().get("relation") < character.getType().needs.relation.critical;
//    }
//}

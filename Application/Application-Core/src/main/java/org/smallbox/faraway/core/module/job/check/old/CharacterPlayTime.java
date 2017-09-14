//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.common.util.Constant;
//import JobModule;
//import org.smallbox.faraway.game.module.ServiceManager;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.item.UserItem;
//import org.smallbox.faraway.game.model.job.UseJob;
//
//// Play with random object
//public class CharacterPlayTime implements CharacterCheck {
//
//    @Override
//    public boolean onGameInit(JobModule jobManager, CharacterModel characters) {
//        if ((int)(Math.random() * 100) <= Constant.CHANCE_TO_GET_MEETING_AREA_WHEN_JOBLESS) {
//            return false;
//        }
//
//        UserItem toy = ModuleHelper.getWorldModule().getRandomToy(characters.getX(), characters.getY());
//        if (toy == null) {
//            return false;
//        }
//
//        jobManager.addJob(UseJob.onGameInit(toy, characters), characters);
//        return true;
//    }
//}

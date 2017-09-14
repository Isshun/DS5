//package org.smallbox.faraway.game.model.check;
//
//import JobModule;
//import org.smallbox.faraway.game.org.smallbox.faraway.core.module.room.model.org.smallbox.faraway.core.module.room.model.base.CharacterModel;
//import org.smallbox.faraway.game.model.isJobLaunchable.old.CharacterCheck;
//import org.smallbox.faraway.game.model.job.BaseJobModel;
//import org.smallbox.faraway.common.util.Log;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * Created by Alex on 06/07/2015.
// */
//public class CheckCharacterEntertainmentDepleted extends CharacterCheck {
//    @Override
//    public BaseJobModel createModules(CharacterModel model) {
//        List<CharacterCheck> joys = ModuleHelper.getJobModule().getEntertainments();
//
//        Collections.shuffle(joys);
//        for (CharacterCheck jobCheck: joys) {
//            if (jobCheck.isJobLaunchable(model)) {
//                return jobCheck.createModules(model);
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean isJobLaunchable(CharacterModel model) {
//        return true;
//    }
//
//    @Override
//    public boolean isJobNeeded(CharacterModel model) {
//        return org.smallbox.faraway.core.module.room.model.getType().needs.entertainment != null && org.smallbox.faraway.core.module.room.model.getNeeds().entertainment < org.smallbox.faraway.core.module.room.model.getType().needs.entertainment.warning;
//    }
//}

//package org.smallbox.faraway.game.model.check;
//
//import org.smallbox.faraway.game.module.character.JobModule;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.check.old.CharacterCheck;
//import org.smallbox.faraway.game.model.job.BaseJobModel;
//import org.smallbox.faraway.util.Log;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * Created by Alex on 06/07/2015.
// */
//public class CheckCharacterJoyDepleted extends CharacterCheck {
//    @Override
//    public BaseJobModel create(CharacterModel character) {
//        List<CharacterCheck> joys = JobModule.getInstance().getJoys();
//
//        Collections.shuffle(joys);
//        for (CharacterCheck jobCheck: joys) {
//            if (jobCheck.check(character)) {
//                return jobCheck.create(character);
//            }
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean check(CharacterModel character) {
//        return true;
//    }
//
//    @Override
//    public boolean need(CharacterModel character) {
//        return character.getType().needs.joy != null && character.getNeeds().joy < character.getType().needs.joy.warning;
//    }
//}

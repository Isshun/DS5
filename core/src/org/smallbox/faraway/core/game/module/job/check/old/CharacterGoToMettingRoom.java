//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.util.Constant;
//import JobModule;
//import org.smallbox.faraway.game.model.characters.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.MoveJob;
//import org.smallbox.faraway.game.model.room.Room;
//
//public class CharacterGoToMettingRoom implements CharacterCheck {
//
//    @Override
//    public boolean onCreate(JobModule jobManager, CharacterModel characters) {
//        Room room = Game.getRoomManager().getNearRoom(characters.getX(), characters.getY(), Room.Type.METTING);
//        if (room != null) {
//            jobManager.addJob(MoveJob.onCreate(characters, room.getX(), room.getY(), Constant.CHARACTER_STAY_IN_METTING_ROOM * Constant.DURATION_MULTIPLIER), characters);
//            return true;
//        }
//        return false;
//    }
//
//}

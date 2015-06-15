//package org.smallbox.faraway.model.check.old;
//
//import org.smallbox.faraway.Game;
//import org.smallbox.faraway.engine.util.Constant;
//import org.smallbox.faraway.manager.JobManager;
//import org.smallbox.faraway.model.character.CharacterModel;
//import org.smallbox.faraway.model.job.JobMove;
//import org.smallbox.faraway.model.room.Room;
//
//public class CharacterGoToMettingRoom implements CharacterCheck {
//
//	@Override
//	public boolean create(JobManager jobManager, CharacterModel character) {
//		Room room = Game.getRoomManager().getNearRoom(character.getX(), character.getY(), Room.Type.METTING);
//		if (room != null) {
//			jobManager.addJob(JobMove.create(character, room.getX(), room.getY(), Constant.CHARACTER_STAY_IN_METTING_ROOM * Constant.DURATION_MULTIPLIER), character);
//			return true;
//		}
//		return false;
//	}
//
//}

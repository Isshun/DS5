//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.util.Constant;
//import org.smallbox.faraway.game.manager.JobManager;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.job.JobMove;
//import org.smallbox.faraway.game.model.room.Room;
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

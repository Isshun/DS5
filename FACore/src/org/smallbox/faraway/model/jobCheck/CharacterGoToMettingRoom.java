package org.smallbox.faraway.model.jobCheck;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.character.Character;
import org.smallbox.faraway.model.job.JobMove;
import org.smallbox.faraway.model.room.Room;
import org.smallbox.faraway.engine.util.Constant;

public class CharacterGoToMettingRoom implements CharacterCheck {

	@Override
	public boolean create(JobManager jobManager, Character character) {
		Room room = Game.getRoomManager().getNeerRoom(character.getX(), character.getY(), Room.Type.METTING);
		if (room != null) {
			jobManager.addJob(JobMove.create(character, room.getX(), room.getY(), Constant.CHARACTER_STAY_IN_METTING_ROOM * Constant.DURATION_MULTIPLIER), character);
			return true;
		}
		return false;
	}

}

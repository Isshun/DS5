package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.job.JobMove;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.util.Constant;

public class CharacterGoToMettingRoom implements JobCharacterCheck {

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

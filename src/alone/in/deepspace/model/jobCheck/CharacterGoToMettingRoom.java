package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobMove;
import alone.in.deepspace.util.Constant;

public class CharacterGoToMettingRoom implements JobCheck {

	@Override
	public Job create(JobManager jobManager, Character character) {
		Room room = RoomManager.getInstance().getNeerRoom(character.getX(), character.getY(), Room.Type.METTING);
		if (room != null) {
			return JobMove.create(character, room.getX(), room.getY(), Constant.CHARACTER_STAY_IN_METTING_ROOM * Constant.DURATION_MULTIPLIER);
		}
		return null;
	}

}

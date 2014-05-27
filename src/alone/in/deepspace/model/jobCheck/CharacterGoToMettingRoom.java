package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.Room;
import alone.in.deepspace.util.Constant;

public class CharacterGoToMettingRoom implements JobCheck {

	@Override
	public void check(JobManager jobManager, Character character) {
		Room room = RoomManager.getInstance().getNeerRoom(character.getX(), character.getY(), Room.Type.METTING);
		if (room != null) {
			Job job = jobManager.createMovingJob(room.getX(), room.getY(), Constant.CHARACTER_STAY_IN_METTING_ROOM * Constant.DURATION_MULTIPLIER);
			if (job != null) {
				character.setJob(job);
				jobManager.addJob(job);
			}
		}
		return;
	}

}

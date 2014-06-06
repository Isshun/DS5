package alone.in.deepspace.model.jobCheck;

import java.util.List;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.RoomManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobGather;
import alone.in.deepspace.model.room.Room;

/**
 * Launch jobs if low food
 */
public class CheckGardenIsOk implements JobCheck {

	public Job create(JobManager jobManager, Character character) {
		List<Room> rooms = RoomManager.getInstance().getRoomList();
		for (Room room: rooms) {
			if (room.isGarden()) {
				List<WorldArea> areas = room.getAreas();
				for (WorldArea area: areas) {
					WorldResource res = area.getRessource();
					if (res != null && res.isMature()) {
						JobManager.getInstance().addJob(JobGather.create(res));
					}
				}
			}
		}
		return null;
	}
}

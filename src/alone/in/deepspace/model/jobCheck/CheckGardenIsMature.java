package alone.in.deepspace.model.jobCheck;

import java.util.List;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.model.item.WorldArea;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.job.JobGather;
import alone.in.deepspace.model.room.Room;

/**
 * Launch jobs if low food
 */
public class CheckGardenIsMature implements JobCheck {

	public void create(JobManager jobManager) {
		List<Room> rooms = Game.getRoomManager().getRoomList();
		for (Room room: rooms) {
			if (room.isGarden()) {
				List<WorldArea> areas = room.getAreas();
				for (WorldArea area: areas) {
					WorldResource res = area.getRessource();
					if (res != null && res.isMature() && res.hasNoJob()) {
						jobManager.addJob(JobGather.create(res));
					}
				}
			}
		}
	}
}

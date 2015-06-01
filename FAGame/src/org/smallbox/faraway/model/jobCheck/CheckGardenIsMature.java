package org.smallbox.faraway.model.jobCheck;

import org.smallbox.faraway.Game;
import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.model.item.WorldArea;
import org.smallbox.faraway.model.item.WorldResource;
import org.smallbox.faraway.model.job.JobGather;
import org.smallbox.faraway.model.room.Room;

import java.util.List;

/**
 * Launch jobs if low food
 */
public class CheckGardenIsMature implements Check {

	public void create(JobManager jobManager) {
		List<Room> rooms = Game.getRoomManager().getRoomList();
		for (Room room: rooms) {
			if (room.isGarden()) {
				List<WorldArea> areas = room.getAreas();
				for (WorldArea area: areas) {
					WorldResource res = area.getResource();
					if (res != null && res.isMature() && res.hasNoJob()) {
						jobManager.addJob(JobGather.create(res));
					}
				}
			}
		}
	}
}

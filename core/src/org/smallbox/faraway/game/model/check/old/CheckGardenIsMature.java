//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.game.module.character.JobModule;
//import org.smallbox.faraway.game.model.item.WorldArea;
//import org.smallbox.faraway.game.model.item.WorldResource;
//import org.smallbox.faraway.game.model.job.JobGather;
//import org.smallbox.faraway.game.model.onCheck.Check;
//import org.smallbox.faraway.game.model.room.Room;
//
//import java.util.List;
//
///**
// * Launch jobs if low food
// */
//public class CheckGardenIsMature implements Check {
//
//	public void onCreate(JobModule jobManager) {
//		List<Room> rooms = Game.getRoomManager().getRoomList();
//		for (Room room: rooms) {
//			if (room.isGarden()) {
//				List<WorldArea> areas = room.getParcels();
//				for (WorldArea area: areas) {
//					WorldResource res = area.getResource();
//					if (res != null && res.isMature() && res.hasNoJob()) {
//						jobManager.addJob(JobGather.onCreate(res));
//					}
//				}
//			}
//		}
//	}
//}

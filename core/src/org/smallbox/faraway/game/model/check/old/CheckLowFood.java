//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.game.manager.character.JobManager;
//import org.smallbox.faraway.game.manager.extra.ResourceManager;
//import org.smallbox.faraway.game.model.item.ItemFilter;
//import org.smallbox.faraway.game.model.item.UserItem;
//import org.smallbox.faraway.game.model.job.JobModel;
//import org.smallbox.faraway.game.model.job.JobUse;
//import org.smallbox.faraway.game.model.onCheck.Check;
//
///**
// * Launch jobs if low food
// */
//public class CheckLowFood implements Check {
//
//	private JobModel _job;
//
//	public void onCreate(JobManager jobManager) {
//		if (_job != null && _job.isFinish() == false) {
//			return;
//		}
//
//		if (ResourceManager.getInstance().isLowFood() == false) {
//			return;
//		}
//
//		// Search for food-factory
//		ItemFilter itemFilter = ItemFilter.createFactoryFilter();
//		itemFilter.effectFood = true;
//		UserItem item = Game.getWorldFinder().find(itemFilter);
//		if (item == null) {
//			return;
//		}
//
//		// Create job
//		jobManager.addJob(JobUse.onCreate(item));
//	}
//
//}

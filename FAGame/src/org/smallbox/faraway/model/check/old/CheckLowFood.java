//package org.smallbox.faraway.model.check.old;
//
//import org.smallbox.faraway.Game;
//import org.smallbox.faraway.manager.JobManager;
//import org.smallbox.faraway.manager.ResourceManager;
//import org.smallbox.faraway.model.item.ItemFilter;
//import org.smallbox.faraway.model.item.UserItem;
//import org.smallbox.faraway.model.job.JobModel;
//import org.smallbox.faraway.model.job.JobUse;
//import org.smallbox.faraway.model.check.Check;
//
///**
// * Launch jobs if low food
// */
//public class CheckLowFood implements Check {
//
//	private JobModel _job;
//
//	public void create(JobManager jobManager) {
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
//		jobManager.addJob(JobUse.create(item));
//	}
//
//}

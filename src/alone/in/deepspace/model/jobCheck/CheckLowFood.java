package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.model.item.ItemFilter;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobUse;

/**
 * Launch jobs if low food
 */
public class CheckLowFood implements JobCheck {

	private Job _job;

	public void create(JobManager jobManager) {
		if (_job != null && _job.isFinish() == false) {
			return;
		}
		
		if (ResourceManager.getInstance().isLowFood() == false) {
			return;
		}
		
		// Search for food-factory
		ItemFilter itemFilter = ItemFilter.createFactoryFilter();
		itemFilter.effectFood = true;
		UserItem item = Game.getWorldFinder().find(itemFilter);
		if (item == null) {
			return;
		}
		
		// Create job
		jobManager.addJob(JobUse.create(item));
	}

}

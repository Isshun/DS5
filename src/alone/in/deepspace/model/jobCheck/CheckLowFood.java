package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.UserItem;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.job.JobUse;

/**
 * Launch jobs if low food
 */
public class CheckLowFood implements JobCheck {

	private Job _job;

	public Job create(JobManager jobManager, Character character) {
		if (_job != null && _job.isFinish() == false) {
			return null;
		}
		
		if (ResourceManager.getInstance().isLowFood() == false) {
			return null;
		}
		
		// Search for food-factory
		ItemFilter itemFilter = new ItemFilter(true, false);
		itemFilter.food = true;
		UserItem item = ServiceManager.getWorldMap().find(itemFilter, true);
		if (item == null) {
			return null;
		}
		
		// Create job
		return JobUse.create(item);
	}

}

package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.UserItem;

/**
 * Launch jobs if low food
 */
public class CheckLowFood implements JobCheck {

	private Job _job;

	public void check(JobManager jobManager, Character character) {
		if (_job != null && _job.isFinish() == false) {
			return;
		}
		
		if (ResourceManager.getInstance().isLowFood() == false) {
			return;
		}
		
		// Search for food-factory
		ItemFilter itemFilter = new ItemFilter(true, false);
		itemFilter.food = true;
		UserItem item = ServiceManager.getWorldMap().find(itemFilter, true);
		if (item == null) {
			return;
		}
		
		// Create job
		Job job = jobManager.createUseJob(item);
		jobManager.addJob(job);
	}

}

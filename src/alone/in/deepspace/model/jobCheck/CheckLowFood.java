package alone.in.deepspace.model.jobCheck;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.UserItem;

// Launch jobs if low food
public class CheckLowFood implements JobCheck {

	private Job _job;

	public void check(JobManager jobManager) {
		if (_job != null && _job.isFinish() == false) {
			return;
		}
		
		if (ResourceManager.getInstance().isLowFood()) {
			// Filter for food-factory
			ItemFilter itemFilter = new ItemFilter(true, false);
			itemFilter.food = true;
			
			// Launch use job if needed item exist
			UserItem item = ServiceManager.getWorldMap().find(itemFilter);
			if (item != null) {
				_job = jobManager.createUseJob(item);
				if (_job != null) {
					jobManager.addJob(_job);
				}
			}
		}
	}

}

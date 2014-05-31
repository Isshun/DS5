package alone.in.deepspace.model.jobCheck;

import java.util.List;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.StorageItem;
import alone.in.deepspace.model.job.Job;

// Refill dispenser
public class CheckEmptyDispenser implements JobCheck {

	public Job create(JobManager jobManager, Character character) {
		List<StorageItem> dispensers = ServiceManager.getWorldMap().getDispensers();
		for (StorageItem dispenser: dispensers) {
			if (dispenser.needRefill() && dispenser.isWaitForRefill() == false) {
				JobManager.getInstance().addRefillJob(dispenser);
			}
		}
		return null;
	}

}

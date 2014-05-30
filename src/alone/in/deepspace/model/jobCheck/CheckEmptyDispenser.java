package alone.in.deepspace.model.jobCheck;

import java.util.List;

import alone.in.deepspace.manager.ItemFilter;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.Character;
import alone.in.deepspace.model.ItemInfo;
import alone.in.deepspace.model.Job;
import alone.in.deepspace.model.StorageItem;

// Refill dispenser
public class CheckEmptyDispenser implements JobCheck {

	public void check(JobManager jobManager, Character character) {
		List<StorageItem> dispensers = ServiceManager.getWorldMap().getDispensers();
		for (StorageItem dispenser: dispensers) {
			if (dispenser.needRefill() && dispenser.isWaitForRefill() == false) {
				JobManager.getInstance().addRefillJob(dispenser);
			}			
		}
	}

}

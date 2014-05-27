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
			if (dispenser.needRefill() && dispenser.isWaitRefill() == false) {
				// Looking for storage containing accepted item
				StorageItem storage = null;
				ItemFilter itemFilter = new ItemFilter(true, true); 
				for (ItemInfo neededItemInfo: dispenser.getInfo().onAction.itemAccept) {
					if (storage == null) {
						itemFilter.neededItem = neededItemInfo;
						storage = ServiceManager.getWorldMap().findStorageContains(itemFilter, dispenser.getX(), dispenser.getY());
					}
				}

				// Create jobs if needed item is available
				if (storage != null) {
					Job job = jobManager.createRefillJob(null, storage, itemFilter, dispenser);
					jobManager.addJob(job);
				}
			}			
		}
	}

}

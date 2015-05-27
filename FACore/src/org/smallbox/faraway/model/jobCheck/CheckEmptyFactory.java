package org.smallbox.faraway.model.jobCheck;

import org.smallbox.faraway.manager.JobManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.model.item.FactoryItem;

import java.util.List;

// Refill dispenser
public class CheckEmptyFactory implements Check {

	public void create(JobManager jobManager) {
		List<FactoryItem> factories = ServiceManager.getWorldMap().getFactories();
		for (FactoryItem factory: factories) {
			if (factory.needRefill() && factory.isWaitForRefill() == false) {
				JobManager.getInstance().addRefillJob(factory);
			}
		}
	}
	
}

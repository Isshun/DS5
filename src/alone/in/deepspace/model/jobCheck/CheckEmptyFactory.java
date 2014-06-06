package alone.in.deepspace.model.jobCheck;

import java.util.List;

import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.item.FactoryItem;

// Refill dispenser
public class CheckEmptyFactory implements JobCheck {

	public void create(JobManager jobManager) {
		List<FactoryItem> factories = ServiceManager.getWorldMap().getFactories();
		for (FactoryItem factory: factories) {
			if (factory.needRefill() && factory.isWaitForRefill() == false) {
				JobManager.getInstance().addRefillJob(factory);
			}
		}
	}
	
}

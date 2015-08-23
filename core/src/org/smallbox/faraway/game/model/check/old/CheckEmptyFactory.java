//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.manager.character.JobManager;
//import org.smallbox.faraway.game.manager.ServiceManager;
//import org.smallbox.faraway.game.model.item.FactoryItem;
//import org.smallbox.faraway.game.model.onCheck.Check;
//
//import java.util.List;
//
//// Refill dispenser
//public class CheckEmptyFactory implements Check {
//
//	public void onCreate(JobManager jobManager) {
//		List<FactoryItem> factories = Game.getWorldManager().getFactories();
//		for (FactoryItem factory: factories) {
//			if (factory.needRefill() && factory.isWaitForRefill() == false) {
//				JobManager.getInstance().addRefillJob(factory);
//			}
//		}
//	}
//
//}

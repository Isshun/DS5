//package org.smallbox.faraway.game.model.check.old;
//
//import org.smallbox.faraway.game.module.character.JobModule;
//import org.smallbox.faraway.game.module.ServiceManager;
//import org.smallbox.faraway.game.model.item.FactoryItem;
//import org.smallbox.faraway.game.model.onCheck.Check;
//
//import java.util.List;
//
//// Refill dispenser
//public class CheckEmptyFactory implements Check {
//
//	public void onCreate(JobModule jobManager) {
//		List<FactoryItem> factories = Game.getWorldManager().getFactories();
//		for (FactoryItem factory: factories) {
//			if (factory.needRefill() && factory.isWaitForRefill() == false) {
//				JobModule.getInstance().addRefillJob(factory);
//			}
//		}
//	}
//
//}

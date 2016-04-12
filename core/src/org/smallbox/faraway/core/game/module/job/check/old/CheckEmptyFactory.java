//package org.smallbox.faraway.game.model.check.old;
//
//import JobModule;
//import org.smallbox.faraway.game.module.ServiceManager;
//import org.smallbox.faraway.game.model.item.FactoryItem;
//import org.smallbox.faraway.game.model.onCheck.Check;
//
//import java.util.List;
//
//// Refill dispenser
//public class CheckEmptyFactory implements Check {
//
//    public void onGameInit(JobModule jobManager) {
//        List<FactoryItem> factories = ModuleHelper.getWorldModule().getFactories();
//        for (FactoryItem factory: factories) {
//            if (factory.needRefill() && factory.isWaitForRefill() == false) {
//                ModuleHelper.getJobModule().addRefillJob(factory);
//            }
//        }
//    }
//
//}

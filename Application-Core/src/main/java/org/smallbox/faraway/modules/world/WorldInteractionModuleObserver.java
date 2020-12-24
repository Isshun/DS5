//package org.smallbox.faraway.modules.world;
//
//import org.smallbox.faraway.GameEvent;
//import org.smallbox.faraway.core.engine.module.ModuleObserver;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//
//import java.util.Collection;
//
///**
// * Created by Alex
// */
//public interface WorldInteractionModuleObserver extends ModuleObserver {
//    default void actionPlan(int finalX, int finalY, int floor) {}
//    default void actionBuild(ParcelModel parcel) {}
//    default void onSelect(GameEvent event, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {}
//    default void onSelect(GameEvent event, Collection<ParcelModel> parcels) {}
//}

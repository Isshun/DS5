//package org.smallbox.faraway.client.gameAction;
//
//import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
//import org.smallbox.faraway.game.world.Parcel;
//import org.smallbox.faraway.modules.area.AreaModel;
//import org.smallbox.faraway.modules.area.AreaTypeInfo;
//import org.smallbox.faraway.modules.dig.DigJobFactory;
//import org.smallbox.faraway.modules.job.JobModule;
//
//@GameObject
//@AreaTypeInfo(label = "Dig", color = 0x80391eff)
//public class CancelArea extends AreaModel {
//
//    @Inject
//    private JobModule jobModule;
//
//    @Inject
//    private DigJobFactory digJobFactory;
//
//    @Override
//    public boolean isAccepted(ItemInfo itemInfo) {
//        return false;
//    }
//
//    @Override
//    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {
//    }
//
//    @Override
//    public void onParcelSelected(Parcel parcel) {
//        if (parcel.getRockInfo() != null) {
//            jobModule.add(digJobFactory.createJob(parcel));
//        }
//    }
//
//}

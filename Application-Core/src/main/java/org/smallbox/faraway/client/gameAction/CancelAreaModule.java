//package org.smallbox.faraway.client.gameAction;
//
//import org.apache.commons.collections4.CollectionUtils;
//import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.game.world.Parcel;
//import org.smallbox.faraway.modules.area.AreaModule;
//import org.smallbox.faraway.modules.area.AreaModuleBase;
//import org.smallbox.faraway.modules.dig.DigJob;
//import org.smallbox.faraway.modules.dig.DigJobFactory;
//import org.smallbox.faraway.modules.job.JobModel;
//import org.smallbox.faraway.modules.job.JobModule;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@GameObject
//public class CancelAreaModule extends AreaModuleBase<CancelArea> {
//    @Inject private JobModule jobModule;
//    @Inject private AreaModule areaModule;
//    @Inject private DigJobFactory digJobFactory;
//
//    @OnInit
//    public void init() {
//        areaModule.addAreaClass(CancelArea.class);
//    }
//
//    @Override
//    protected void onModuleUpdate(Game game) {
//        List<Parcel> parcelInDigArea = areaModule.getParcelsByType(CancelArea.class);
//        List<Parcel> parcelInDigJob = jobModule.getAll().stream()
//                .filter(job -> job instanceof DigJob)
//                .map(JobModel::getTargetParcel)
//                .collect(Collectors.toList());
//
//        // Create missing dig job
//        parcelInDigArea.stream()
//                .filter(parcel -> parcel.getRockInfo() != null)
//                .filter(parcel -> !CollectionUtils.containsAny(parcelInDigJob, parcel))
//                .forEach(parcel -> jobModule.add(digJobFactory.createJob(parcel)));
//    }
//
//    @Override
//    public CancelArea onNewArea() {
//        return new CancelArea();
//    }
//}

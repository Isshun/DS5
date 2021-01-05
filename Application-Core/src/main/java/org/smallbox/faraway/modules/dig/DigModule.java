package org.smallbox.faraway.modules.dig;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.AreaModuleBase;
import org.smallbox.faraway.modules.job.JobModel;
import org.smallbox.faraway.modules.job.JobModule;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@GameObject
public class DigModule extends AreaModuleBase<DigArea> {

    @Inject
    private JobModule jobModule;

    @Inject
    private AreaModule areaModule;

    @Inject
    private DigJobFactory digJobFactory;

    @OnInit
    public void init() {
        areaModule.addAreaClass(DigArea.class);
    }

    @Override
    protected void onModuleUpdate(Game game) {
        List<ParcelModel> parcelInDigArea = areaModule.getParcelsByType(DigArea.class);
        List<ParcelModel> parcelInDigJob = jobModule.getAll().stream()
                .filter(job -> job instanceof DigJob)
                .map(JobModel::getTargetParcel)
                .collect(Collectors.toList());

        // Create missing dig job
        parcelInDigArea.stream()
                .filter(parcel -> parcel.getRockInfo() != null)
                .filter(parcel -> CollectionUtils.notContains(parcelInDigJob, parcel))
                .forEach(parcel -> jobModule.add(digJobFactory.createJob(parcel)));
    }

    @Override
    public DigArea onNewArea() {
        return new DigArea();
    }
}

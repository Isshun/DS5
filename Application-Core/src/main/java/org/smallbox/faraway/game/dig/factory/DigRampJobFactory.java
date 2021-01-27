package org.smallbox.faraway.game.dig.factory;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.model.MovableModel;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.job.JobModel;
import org.smallbox.faraway.game.world.WorldHelper;

import java.util.Optional;

@GameObject
public class DigRampJobFactory extends DigJobFactory {
    @Inject private PathManager pathManager;

    @Override
    protected void digAction(JobModel job) {
        job.getTargetParcel().setRamp(MovableModel.Direction.LEFT);
        pathManager.refreshConnections(job.getTargetParcel());

        // Remove ground for upper parcel
        Optional.ofNullable(WorldHelper.getParcelOffset(job.getTargetParcel(), 0, 0, 1)).ifPresent(parcel -> {
            parcel.setGroundInfo(null);
            pathManager.refreshConnections(parcel);
        });
    }
}

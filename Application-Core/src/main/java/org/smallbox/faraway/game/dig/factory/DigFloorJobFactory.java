package org.smallbox.faraway.game.dig.factory;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.job.JobModel;

@GameObject
public class DigFloorJobFactory extends DigJobFactory {
    @Inject private PathManager pathManager;

    @Override
    protected void digAction(JobModel job) {
        job.getTargetParcel().setGroundInfo(null);
        pathManager.refreshConnections(job.getTargetParcel());
    }
}

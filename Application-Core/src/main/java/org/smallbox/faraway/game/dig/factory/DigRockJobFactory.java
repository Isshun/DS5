package org.smallbox.faraway.game.dig.factory;

import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.gameAction.OnGameMapChange;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.job.JobModel;

@GameObject
public class DigRockJobFactory extends DigJobFactory {
    @Inject private DependencyManager dependencyManager;
    @Inject private ConsumableModule consumableModule;
    @Inject private PathManager pathManager;

    @Override
    protected void digAction(JobModel job) {
        if (job.getTargetParcel().getRockInfo() != null) {
            job.getTargetParcel().getRockInfo().actions.stream()
                    .filter(action -> action.type == ItemInfo.ItemInfoAction.ActionType.MINE)
                    .flatMap(action -> action.products.stream())
                    .forEach(product -> consumableModule.addConsumable(product.item, product.quantity, job.getTargetParcel()));
            job.getTargetParcel().setRockInfo(null);
            pathManager.refreshConnections(job.getTargetParcel());

//            // Remove ground for upper parcel
//            Optional.ofNullable(WorldHelper.getParcelOffset(job.getTargetParcel(), 0, 0, 1)).ifPresent(parcel -> {
//                parcel.setGroundInfo(null);
//                pathManager.refreshConnections(parcel);
//            });

            dependencyManager.callMethodAnnotatedBy(OnGameMapChange.class);
        }
    }
}

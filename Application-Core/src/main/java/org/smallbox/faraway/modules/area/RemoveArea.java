package org.smallbox.faraway.modules.area;

import org.smallbox.faraway.client.GameActionManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

@GameObject
@AreaTypeInfo(label = "Remove area", color = 0xa8a8a8ff)
public class RemoveArea extends AreaModel {

    @Inject
    private GameActionManager areaModule;

    @Override
    public boolean isAccepted(ItemInfo itemInfo) {
        return false;
    }

    @Override
    public void setAccept(ItemInfo itemInfo, boolean isAccepted) {

    }

    @Override
    public void execute(ParcelModel parcel) {
        areaModule.removeArea(parcel);
    }

}

package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.StorageArea;

import java.util.Queue;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaInfoStorageController extends AbsInfoLuaController<AreaModel> {

    @BindModule
    private AreaModule areaModule;

    @Override
    protected void onDisplayUnique(AreaModel area) {
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
    }

    @Override
    public AreaModel getObjectOnParcel(ParcelModel parcel) {
        AreaModel area = areaModule.getArea(parcel);
        return area instanceof StorageArea ? area : null;
    }
}

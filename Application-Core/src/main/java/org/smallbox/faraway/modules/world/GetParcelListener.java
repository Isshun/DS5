package org.smallbox.faraway.modules.world;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Collection;

/**
 * Created by Alex on 10/11/2015.
 */
public interface GetParcelListener {
    void onGetParcel(Collection<ParcelModel> parcelsDo);
}

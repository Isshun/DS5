package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.util.Collection;

/**
 * Created by Alex on 10/11/2015.
 */
public interface GetParcelListener {
    void onGetParcel(Collection<ParcelModel> parcelsDo);
}

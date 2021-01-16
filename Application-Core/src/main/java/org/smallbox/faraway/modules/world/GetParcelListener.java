package org.smallbox.faraway.modules.world;

import org.smallbox.faraway.core.module.world.model.Parcel;

import java.util.Collection;

public interface GetParcelListener {
    void onGetParcel(Collection<Parcel> parcelsDo);
}

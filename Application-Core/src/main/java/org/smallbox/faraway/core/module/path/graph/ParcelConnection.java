package org.smallbox.faraway.core.module.path.graph;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.module.world.model.Parcel;

public class ParcelConnection implements Connection<Parcel> {
    private final Parcel _fromParcel;
    private final Parcel _toParcel;

    public ParcelConnection(Parcel fromParcel, Parcel toParcel) {
        _fromParcel = fromParcel;
        _toParcel = toParcel;
    }

    @Override
    public float getCost() {
// TODO
        //        if (_toParcel.getItem() != null) {
//            return 10;
//        }
//        if (_toParcel.hasPlant()) {
//            return 5;
//        }

        return _fromParcel.x == _toParcel.x || _fromParcel.y == _toParcel.y ? 1 : 3;
    }

    @Override
    public Parcel getFromNode() {
        return _fromParcel;
    }

    @Override
    public Parcel getToNode() {
        return _toParcel;
    }

    @Override
    public String toString() {
        return _fromParcel + " -> " + _toParcel;
    }
}
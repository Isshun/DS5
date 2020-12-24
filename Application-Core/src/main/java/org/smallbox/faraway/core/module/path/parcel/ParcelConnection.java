package org.smallbox.faraway.core.module.path.parcel;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.module.world.model.ParcelModel;

public class ParcelConnection implements Connection<ParcelModel> {
    private final ParcelModel   _fromParcel;
    private final ParcelModel   _toParcel;

    public ParcelConnection(ParcelModel fromParcel, ParcelModel toParcel) {
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
    public ParcelModel getFromNode() {
        return _fromParcel;
    }

    @Override
    public ParcelModel getToNode() {
        return _toParcel;
    }

    @Override
    public String toString() {
        return _fromParcel + " -> " + _toParcel;
    }
}
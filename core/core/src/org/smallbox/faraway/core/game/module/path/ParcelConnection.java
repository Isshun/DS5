package org.smallbox.faraway.core.game.module.path;

import com.badlogic.gdx.ai.pfa.Connection;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

/**
 * Created by Alex on 21/07/2015.
 */
public class ParcelConnection implements Connection<ParcelModel> {
    private final ParcelModel   _fromParcel;
    private final ParcelModel   _toParcel;

    public ParcelConnection(ParcelModel fromParcel, ParcelModel toParcel) {
        _fromParcel = fromParcel;
        _toParcel = toParcel;
    }

    @Override
    public float getCost() {
        if (_toParcel.getItem() != null) {
            return 10;
        }
        if (_toParcel.getResource() != null) {
            return 5;
        }

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
}

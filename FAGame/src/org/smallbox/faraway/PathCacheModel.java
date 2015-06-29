package org.smallbox.faraway;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.game.model.item.ParcelModel;

/**
 * Created by Alex on 29/06/2015.
 */
public class PathCacheModel {
    private final GraphPath<ParcelModel>    _path;
    private final ParcelModel               _fromParcel;
    private final ParcelModel               _toParcel;
    private boolean                         _isValid = true;

    public PathCacheModel(ParcelModel fromParcel, ParcelModel toParcel, GraphPath<ParcelModel> path) {
        _path = path;
        _fromParcel = fromParcel;
        _toParcel = toParcel;
    }

    public void invalidate() {
        _isValid = false;
    }

    public boolean isValid() {
        return _isValid;
    }

    public GraphPath<ParcelModel> getPath() {
        return _path;
    }
}

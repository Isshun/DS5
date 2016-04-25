package org.smallbox.faraway.core.game.module.character.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

/**
 * Created by Alex on 30/10/2015.
 */
public class PathModel {
    private ParcelModel                     _currentParcel;
    private final ParcelModel               _firstParcel;
    private final ParcelModel               _lastParcel;
    private final int                       _length;
    private int                             _index;
    private final GraphPath<ParcelModel>    _nodes;

    public static PathModel create(GraphPath<ParcelModel> nodes) {
        if (nodes != null) {
            return new PathModel(nodes);
        }
        return null;
    }

    private PathModel(GraphPath<ParcelModel> nodes) {
        _nodes = nodes;
        _currentParcel = nodes.getCount() > 1 ? nodes.get(1) : null;
        _firstParcel = nodes.getCount() > 0 ? nodes.get(0) : null;
        _lastParcel = nodes.getCount() > 0 ? nodes.get(nodes.getCount()-1) : null;
        _length = nodes.getCount();
        _index = 1;
    }

    public int          getLength() { return _length; }
    public ParcelModel  getLastParcel() { return _lastParcel; }
    public ParcelModel  getFirstParcel() { return _firstParcel; }
    public ParcelModel  getCurrentParcel() { return _currentParcel; }
    public GraphPath<ParcelModel> getNodes() { return _nodes; }

    public boolean next() {
        if (++_index < _length) {
            _currentParcel = _nodes.get(_index);
            return true;
        }
        return false;
    }

    public boolean isValid() {
        for (ParcelModel parcel: _nodes) {
            if (!parcel.isWalkable()) {
                return false;
            }
        }
        return true;
    }
}
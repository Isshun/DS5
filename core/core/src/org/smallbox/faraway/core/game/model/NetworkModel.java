package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.core.game.module.world.model.NetworkObjectModel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Alex on 04/11/2015.
 */
public class NetworkModel {
    private final NetworkInfo       _info;
    private Set<NetworkObjectModel> _objects = new HashSet<>();
    private double                  _quantity;
    private double                  _maxQuantity;

    public NetworkModel(NetworkInfo info) {
        _info = info;
    }

    public NetworkInfo                      getInfo() { return _info; }
    public Collection<NetworkObjectModel>   getObjects() { return _objects; }
    public int                              getSize() { return _objects.size(); }
    public double                           getQuantity() { return _quantity; }
    public double                           getMaxQuantity() { return _maxQuantity; }
    public boolean                          contains(NetworkObjectModel object) { return _objects.contains(object); }

    public void addObject(NetworkObjectModel object) {
        object.setNetwork(this);
        _objects.add(object);
    }

    public void addObjects(Collection<NetworkObjectModel> objects) {
        objects.forEach(object -> object.setNetwork(this));
        _objects.addAll(objects);
    }

    public boolean isEmpty() {
        return _objects.isEmpty();
    }

    public void flush() {
        _quantity = 0;
        _maxQuantity = 0;
        for (NetworkObjectModel object: _objects) {
            _quantity += object.getQuantity();
            _maxQuantity += object.getMaxQuantity();
        }

        double ratio = _quantity / _maxQuantity;
        _objects.forEach(object -> object.setQuantity(object.getMaxQuantity() * ratio));
    }
}
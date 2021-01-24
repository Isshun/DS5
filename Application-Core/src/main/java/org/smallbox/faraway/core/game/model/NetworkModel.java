package org.smallbox.faraway.core.game.model;

import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.modelInfo.NetworkInfo;
import org.smallbox.faraway.core.world.model.NetworkItem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NetworkModel {
    private final NetworkInfo _info;
    private final Set<NetworkItem> _objects = new HashSet<>();
    private double                  _quantity;
    private double                  _maxQuantity;

    public NetworkModel(NetworkInfo info) {
        _info = info;
    }

    public NetworkInfo                      getInfo() { return _info; }
    public Collection<NetworkItem>   getObjects() { return _objects; }
    public int                              getSize() { return _objects.size(); }
    public double                           getQuantity() { return _quantity; }
    public double                           getMaxQuantity() { return _maxQuantity; }
    public boolean                          contains(NetworkItem object) { return _objects.contains(object); }

    public void addObject(NetworkItem object) {
        object.setNetwork(this);
        _objects.add(object);
        _quantity += object.getQuantity();
        _maxQuantity += object.getMaxQuantity();
        flush();
    }

    public void addObjects(Collection<NetworkItem> objects) {
        objects.forEach(object -> {
            object.setNetwork(this);
            _quantity += object.getQuantity();
            _maxQuantity += object.getMaxQuantity();
        });
        _objects.addAll(objects);
        flush();
    }

    public boolean isEmpty() {
        return _objects.isEmpty();
    }

    private void flush() {
        double ratio = _maxQuantity > 0 ? _quantity / _maxQuantity : 0;
        _objects.forEach(object -> object.setQuantity(object.getMaxQuantity() * ratio));
    }

    public void addQuantity(double quantity) {
        _quantity = Math.min(_quantity + quantity, _maxQuantity);
        flush();
    }

    public void removeQuantity(double quantity) {
        _quantity = Math.max(_quantity - quantity, 0);
        flush();
    }

    public boolean accept(ItemInfo item) {
        if (_info.items != null) {
            for (ItemInfo acceptedItem: _info.items) {
                if (item.instanceOf(acceptedItem)) {
                    return true;
                }
            }
        }
        return false;
    }
}
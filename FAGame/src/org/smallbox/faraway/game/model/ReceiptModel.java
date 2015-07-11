package org.smallbox.faraway.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.job.JobCraft;

import java.util.*;

/**
 * Created by Alex on 07/06/2015.
 */
public class ReceiptModel {
    private static class PotentialConsumable {
        public ConsumableModel          consumable;
        public int                      distance;

        public PotentialConsumable(ConsumableModel consumable, int distance) {
            this.consumable = consumable;
            this.distance = distance;
        }
    }

    private final Map<ItemInfo, Integer>        _infoComponents;
    private final List<PotentialConsumable>     _potentialComponents = new ArrayList<>();
    private final List<OrderModel>              _orders = new ArrayList<>();
    private OrderModel                          _currentOrder;
    private OrderModel                          _nextOrder;
    private final ItemInfo.ItemInfoReceipt      _receiptInfo;
    private final ItemModel                     _factory;
    private int                                 _totalDistance;
    private boolean                             _isRunning;

    public ReceiptModel(ItemModel factory, ItemInfo.ItemInfoReceipt receiptInfo) {
        _factory = factory;
        _receiptInfo = receiptInfo;

        // Add components to receipt
        _infoComponents = new HashMap<>();
        for (ItemInfo.ItemComponentInfo componentInfo: _receiptInfo.components) {
            _infoComponents.put(componentInfo.itemInfo, componentInfo.quantity);
        }

        // Get potential consumables on WorldManager
        _potentialComponents.clear();
        Game.getWorldManager().getConsumables().stream()
                .filter(consumable -> _infoComponents.containsKey(consumable.getInfo()))
                .filter(consumable -> consumable.getParcel().isWalkable())
                .forEach(consumable -> {
                    GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_factory.getParcel(), consumable.getParcel());
                    if (path != null) {
                        _potentialComponents.add(new PotentialConsumable(consumable, path.getCount()));
                    }
                });
        Collections.sort(_potentialComponents, (c1, c2) -> c2.distance - c1.distance);
    }

    public void reset() {
        close();

        // Looking for best nearest consumables
        _totalDistance = 0;
        for (Map.Entry<ItemInfo, Integer> entry: _infoComponents.entrySet()) {
            int quantityLeft = entry.getValue();
            for (PotentialConsumable potentialConsumable: _potentialComponents) {
                if (quantityLeft > 0 && potentialConsumable.consumable.getLock() == null && potentialConsumable.consumable.getInfo() == entry.getKey()) {
                    if (potentialConsumable.consumable.getQuantity() > quantityLeft) {
                        _orders.add(new OrderModel(potentialConsumable.consumable, quantityLeft));
                    } else {
                        _orders.add(new OrderModel(potentialConsumable.consumable, potentialConsumable.consumable.getQuantity()));
                    }
                    _totalDistance += potentialConsumable.distance;
                    quantityLeft -= potentialConsumable.consumable.getQuantity();
                }
            }
        }
    }

    public void close() {
        _orders.forEach(neededComponent -> neededComponent.consumable.lock(null));
        _orders.clear();
    }

    public ItemInfo.ItemInfoReceipt getInfo() {
        return _receiptInfo;
    }

    public boolean hasComponentsOnMap() {
        for (Map.Entry<ItemInfo, Integer> entry: _infoComponents.entrySet()) {
            int totalQuantity = 0;
            for (PotentialConsumable potentialConsumable: _potentialComponents) {
                if (potentialConsumable.consumable.getInfo() == entry.getKey() && potentialConsumable.consumable.getLock() == null) {
                    totalQuantity += potentialConsumable.consumable.getQuantity();
                }
            }
            if (totalQuantity < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean isComplete() {
        for (OrderModel order: _orders) {
            if (order.status != OrderModel.Status.STORED) {
                return false;
            }
        }
        return true;
    }

    public OrderModel getCurrentOrder() {
        return _currentOrder;
    }

    public OrderModel nextOrder() {
        if (_currentOrder != null) {
            int index = _orders.indexOf(_currentOrder);
            if (index + 1 < _orders.size()) {
                _currentOrder = _orders.get(index + 1);
            }
            if (index + 2 < _orders.size()) {
                _nextOrder = _orders.get(index + 2);
            }
        }
        return _currentOrder;
    }

    public int getTotalDistance() {
        return _totalDistance;
    }

    public void addConsumable(ConsumableModel consumable) {
        for (ItemInfo info: _infoComponents.keySet()) {
            if (consumable.getInfo() == info) {
                GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_factory.getParcel(), consumable.getParcel());
                if (path != null) {
                    _potentialComponents.add(new PotentialConsumable(consumable, path.getCount()));
                    Collections.sort(_potentialComponents, (c1, c2) -> c2.distance - c1.distance);
                    return;
                }
            }
        }
    }

    public void removeConsumable(ConsumableModel consumable) {
        for (PotentialConsumable potentialConsumable: _potentialComponents) {
            if (potentialConsumable.consumable == consumable) {
                _potentialComponents.remove(potentialConsumable);
                Collections.sort(_potentialComponents, (c1, c2) -> c2.distance - c1.distance);
                return;
            }
        }
    }

    public void start(JobCraft job) {
        for (OrderModel order: _orders) {
            order.consumable.lock(job);
        }
        _currentOrder = _orders.get(0);
        _nextOrder = _orders.size() > 1 ? _orders.get(1) : null;
    }

    public static class OrderModel {
        public enum Status { NONE, CARRY, STORED }
        public final ConsumableModel    consumable;
        public final int                quantity;
        public Status                   status;

        public OrderModel(ConsumableModel consumable, int quantity) {
            this.consumable = consumable;
            this.quantity = quantity;
        }
    }

    public List<OrderModel> getOrders() {
        return _orders;
    }

    public void closeCarryingOrders() {
        for (OrderModel order: _orders) {
            if (order.status == OrderModel.Status.CARRY) {
                order.status = OrderModel.Status.STORED;
            }
        }
    }

    public OrderModel getNextOrder() {
        return _nextOrder;
    }
}

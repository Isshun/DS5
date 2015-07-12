package org.smallbox.faraway.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.job.BaseBuildJobModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobCraft;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 07/06/2015.
 */
public class ReceiptModel {
    private List<ItemInfo.ItemProductInfo> _products;
    private List<ItemInfo.ItemComponentInfo> _componentsInfo;

    public List<ItemInfo.ItemProductInfo> getProductsInfo() {
        return _products;
    }

    private static class PotentialConsumable {
        public ConsumableModel          consumable;
        public int                      distance;

        public PotentialConsumable(ConsumableModel consumable, int distance) {
            this.consumable = consumable;
            this.distance = distance;
        }
    }

    private final List<PotentialConsumable>     _potentialComponents = new ArrayList<>();
    private final List<OrderModel>              _orders = new ArrayList<>();
    private OrderModel                          _currentOrder;
    private OrderModel                          _nextOrder;
    private final MapObjectModel                _factory;
    private int                                 _totalDistance;

    public static ReceiptModel createFromComponentInfo(MapObjectModel buildItem, List<ItemInfo.ItemComponentInfo> componentsInfo) {
        ReceiptModel receipt = new ReceiptModel(buildItem);
        receipt.scanComponents(componentsInfo);
        return receipt;
    }

    public static ReceiptModel createFromReceiptInfo(ItemModel factory, ItemInfo.ItemInfoReceipt receiptInfo) {
        ReceiptModel receipt = new ReceiptModel(factory);
        receipt._products = receiptInfo.products;
        receipt.scanComponents(receiptInfo.components);
        return receipt;
    }

    // Get potential consumables on WorldManager
    private void scanComponents(List<ItemInfo.ItemComponentInfo> componentsInfo) {
        _componentsInfo = componentsInfo;

        // Add components to receipt
        Map<ItemInfo, Integer> componentsDistance = new HashMap<>();
        for (ItemInfo.ItemComponentInfo componentInfo: componentsInfo) {
            componentsDistance.put(componentInfo.itemInfo, componentInfo.quantity);
        }

        _potentialComponents.clear();
        Game.getWorldManager().getConsumables().stream()
                .filter(consumable -> componentsDistance.containsKey(consumable.getInfo()))
                .filter(consumable -> consumable.getParcel().isWalkable())
                .forEach(consumable -> {
                    GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_factory.getParcel(), consumable.getParcel());
                    if (path != null) {
                        _potentialComponents.add(new PotentialConsumable(consumable, path.getCount()));
                    }
                });
        Collections.sort(_potentialComponents, (c1, c2) -> c2.distance - c1.distance);
    }

    public ReceiptModel(MapObjectModel factory) {
        _factory = factory;
    }

    public void reset() {
        close();

        // Looking for best nearest consumables
        _totalDistance = 0;
        for (ItemInfo.ItemComponentInfo componentInfo: _componentsInfo) {
            int quantityLeft = componentInfo.quantity;
            for (PotentialConsumable potentialConsumable: _potentialComponents) {
                if (quantityLeft > 0 && potentialConsumable.consumable.getLock() == null && potentialConsumable.consumable.getInfo() == componentInfo.itemInfo) {
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

    public boolean hasComponentsOnMap() {
        for (ItemInfo.ItemComponentInfo componentInfo: _componentsInfo) {
            int totalQuantity = 0;
            for (PotentialConsumable potentialConsumable: _potentialComponents) {
                if (potentialConsumable.consumable.getInfo() == componentInfo.itemInfo && potentialConsumable.consumable.getLock() == null) {
                    totalQuantity += potentialConsumable.consumable.getQuantity();
                }
            }
            if (totalQuantity < componentInfo.quantity) {
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
        for (ItemInfo.ItemComponentInfo componentInfo: _componentsInfo) {
            if (consumable.getInfo() == componentInfo.itemInfo) {
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

    public void start(BaseBuildJobModel job) {
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

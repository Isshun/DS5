package org.smallbox.faraway.core.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.core.game.module.world.model.ReceiptGroupInfo;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.ItemModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.job.model.abs.BaseBuildJobModel;
import org.smallbox.faraway.core.game.module.path.PathManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 07/06/2015.
 */
public class OldReceiptModel {
    private List<ReceiptGroupInfo.ReceiptOutputInfo>      _products;
    private List<ReceiptGroupInfo.ReceiptInputInfo>    _componentsInfo;

    public List<ReceiptGroupInfo.ReceiptOutputInfo> getProductsInfo() {
        return _products;
    }

    private static class PotentialConsumable {
        public ConsumableModel consumable;
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
    private final MapObjectModel _factory;
    private int                                 _totalDistance;

//    public static OldReceiptModel createFromComponentInfo(MapObjectModel buildItem, List<ItemInfo.ItemComponentInfo> componentsInfo) {
//        OldReceiptModel receipt = new OldReceiptModel(buildItem);
//        receipt.scanComponents(componentsInfo);
//        return receipt;
//    }

    public static OldReceiptModel createFromReceiptInfo(ItemModel item, ReceiptGroupInfo.ReceiptInfo receiptInfo) {
        OldReceiptModel receipt = new OldReceiptModel(item);
        receipt._products = receiptInfo.outputs;
        receipt.scanComponents(receiptInfo.inputs);
        return receipt;
    }

    // Get potential consumables on WorldModule
    private void scanComponents(List<ReceiptGroupInfo.ReceiptInputInfo> componentsInfo) {
    }

    public OldReceiptModel(MapObjectModel factory) {
        _factory = factory;
    }

    public void reset() {
        close();

        // Looking for best nearest consumables
        _totalDistance = 0;
        if (_componentsInfo != null) {
            for (ReceiptGroupInfo.ReceiptInputInfo componentInfo : _componentsInfo) {
                int quantityLeft = componentInfo.quantity;
                for (PotentialConsumable potentialConsumable : _potentialComponents) {
                    if (quantityLeft > 0 && potentialConsumable.consumable.getLock() == null && potentialConsumable.consumable.getInfo() == componentInfo.item) {
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
    }

    public void close() {
        _orders.forEach(neededComponent -> neededComponent.consumable.lock(null));
        _orders.clear();
    }

    public boolean hasComponentsOnMap() {
        if (_componentsInfo != null) {
            for (ReceiptGroupInfo.ReceiptInputInfo componentInfo: _componentsInfo) {
                int totalQuantity = 0;
                for (PotentialConsumable potentialConsumable : _potentialComponents) {
                    if (potentialConsumable.consumable.getInfo() == componentInfo.item && potentialConsumable.consumable.getLock() == null) {
                        totalQuantity += potentialConsumable.consumable.getQuantity();
                    }
                }
                if (totalQuantity < componentInfo.quantity) {
                    return false;
                }
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
        if (_componentsInfo != null) {
            for (ReceiptGroupInfo.ReceiptInputInfo componentInfo : _componentsInfo) {
                if (consumable.getInfo() == componentInfo.item) {
                    GraphPath<ParcelModel> path = PathManager.getInstance().getPath(_factory.getParcel(), consumable.getParcel());
                    if (path != null) {
                        _potentialComponents.add(new PotentialConsumable(consumable, path.getCount()));
                        Collections.sort(_potentialComponents, (c1, c2) -> c2.distance - c1.distance);
                        return;
                    }
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
        for (OrderModel order : _orders) {
            order.consumable.lock(job);
        }
        _currentOrder = _orders.size() > 0 ? _orders.get(0) : null;
        _nextOrder = _orders.size() > 1 ? _orders.get(1) : null;
    }

    public static class OrderModel {
        public enum Status { NONE, CARRY, STORED }
        public final ConsumableModel    consumable;
        public final int                quantity;
        public Status                   status = Status.NONE;

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

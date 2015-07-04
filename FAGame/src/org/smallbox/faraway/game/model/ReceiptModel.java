package org.smallbox.faraway.game.model;

import com.badlogic.gdx.ai.pfa.GraphPath;
import org.smallbox.faraway.PathManager;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.item.ConsumableModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.job.JobCraft;

import java.util.*;

/**
 * Created by Alex on 07/06/2015.
 */
public class ReceiptModel {
    private static class PotentialConsumable {
        public ConsumableModel consumable;
        public int distance;

        public PotentialConsumable(ConsumableModel consumable, int distance) {
            this.consumable = consumable;
            this.distance = distance;
        }
    }

    public static class NeededComponent {
        public final ConsumableModel consumable;
        public final int quantity;
        public boolean inFactory;

        public NeededComponent(ConsumableModel consumable, int quantity) {
            this.consumable = consumable;
            this.inFactory = false;
            this.quantity = quantity;
        }
    }

    private final Map<ItemInfo, Integer>        _infoComponents;
    private final List<PotentialConsumable>     _potentialComponents = new ArrayList<>();
    private final List<NeededComponent>         _components = new ArrayList<>();
    private final ItemInfo.ItemInfoReceipt      _receiptInfo;
    private final ItemModel                     _factory;
    private int                                 _totalDistance;
    private boolean                             _isRunning;

    public ReceiptModel(ItemModel factory, ItemInfo.ItemInfoReceipt receiptInfo) {
        _factory = factory;
        _receiptInfo = receiptInfo;
        reset();

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
        _components.forEach(neededComponent -> neededComponent.consumable.lock(null));
        _components.clear();
    }

    private void searchBestConsumables() {
        _totalDistance = 0;
        _components.forEach(neededComponent -> neededComponent.consumable.lock(null));
        _components.clear();
        for (Map.Entry<ItemInfo, Integer> entry: _infoComponents.entrySet()) {
            int quantityLeft = entry.getValue();
            for (PotentialConsumable potentialConsumable: _potentialComponents) {
                if (quantityLeft > 0 && potentialConsumable.consumable.getLock() == null && potentialConsumable.consumable.getInfo() == entry.getKey()) {
                    if (potentialConsumable.consumable.getQuantity() > quantityLeft) {
                        _components.add(new NeededComponent(potentialConsumable.consumable, quantityLeft));
                    } else {
                        _components.add(new NeededComponent(potentialConsumable.consumable, potentialConsumable.consumable.getQuantity()));
                    }
                    _totalDistance += potentialConsumable.distance;
                    quantityLeft -= potentialConsumable.consumable.getQuantity();
                }
            }
        }
    }

    public ItemInfo.ItemInfoReceipt getInfo() {
        return _receiptInfo;
    }

    public boolean hasComponentsOnMap() {
        for (Map.Entry<ItemInfo, Integer> entry: _infoComponents.entrySet()) {
            int totalQuantity = 0;
            for (PotentialConsumable potentialConsumable: _potentialComponents) {
                if (potentialConsumable.consumable.getInfo() == entry.getKey()) {
                    totalQuantity += potentialConsumable.consumable.getQuantity();
                }
            }
            if (totalQuantity < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasComponentsInFactory() {
        for (NeededComponent neededConsumable: _components) {
            if (!neededConsumable.inFactory) {
                return false;
            }
        }
        return true;
    }

    public NeededComponent getCurrentComponent() {
        for (NeededComponent neededConsumable: _components) {
            if (!neededConsumable.inFactory) {
                return neededConsumable;
            }
        }
        return null;
    }

    public void nextComponent() {
        for (NeededComponent neededConsumable: _components) {
            if (!neededConsumable.inFactory) {
                neededConsumable.inFactory = true;
                return;
            }
        }
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
        searchBestConsumables();
        for (NeededComponent neededComponent: _components) {
            neededComponent.consumable.lock(job);
        }
    }

}

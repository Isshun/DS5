package org.smallbox.faraway.game.character.model;

import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.consumable.ConsumableItem;
import org.smallbox.faraway.game.character.model.base.CharacterExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CharacterInventoryExtra extends CharacterExtra {

    private final Map<ItemInfo, Integer> _inventory = new ConcurrentHashMap<>();

    public CharacterInventoryExtra(CharacterModel character) {
        super(character);
    }

    public int                          getInventoryQuantity(ItemInfo itemInfo) {
        for (Map.Entry<ItemInfo, Integer> entry: _inventory.entrySet()) {
            if (entry.getKey().instanceOf(itemInfo)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public Map<ItemInfo, Integer> getAll() { return _inventory; }

    public void addInventory(ItemInfo itemInfo, int quantity) {
        int inventoryQuantity = _inventory.getOrDefault(itemInfo, 0);
        if (inventoryQuantity + quantity > 0) {
            if (inventoryQuantity + quantity < 0) {
                throw new GameException(CharacterModel.class, "Character inventory quantity cannot be < 0");
            }

            _inventory.put(itemInfo, inventoryQuantity + quantity);
        } else {
            _inventory.remove(itemInfo);
        }
    }

    public ConsumableItem takeInventory(ItemInfo itemInfo) {
        return takeInventory(itemInfo, getInventoryQuantity(itemInfo));
    }

    public ConsumableItem takeInventory(ItemInfo itemInfo, int needQuantity) {
        int availableQuantity = getInventoryQuantity(itemInfo);
        int quantityToRemove = Math.min(needQuantity, availableQuantity);

        // Delete consumable from character inventory
        if (needQuantity == availableQuantity) {
            _inventory.remove(itemInfo);
        }

        // Or update quantity
        else {
            _inventory.put(itemInfo, availableQuantity - needQuantity);
        }

        return new ConsumableItem(itemInfo, quantityToRemove);
    }

    public void addInventory(String itemName, int quantity) {
        addInventory(DependencyManager.getInstance().getDependency(DataManager.class).getItemInfo(itemName), quantity);
    }

    public void clear() {
        _inventory.clear();
    }
}

package org.smallbox.faraway.game.character.model;

import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.character.model.base.CharacterExtra;
import org.smallbox.faraway.game.character.model.base.CharacterModel;
import org.smallbox.faraway.game.consumable.Consumable;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class CharacterInventoryExtra extends CharacterExtra {

    private final Collection<Consumable> inventory = new LinkedBlockingQueue<>();

    public CharacterInventoryExtra(CharacterModel character) {
        super(character);
    }

    public int getInventoryQuantity(ItemInfo itemInfo) {
        return inventory.stream().filter(consumable -> consumable.getInfo() == itemInfo).mapToInt(Consumable::getTotalQuantity).sum();
    }

    public Collection<Consumable> getAll() {
        return inventory;
    }

    public Consumable addInventory(ItemInfo itemInfo, int quantity) {
        Consumable consumable = new Consumable(itemInfo, quantity);
        inventory.add(consumable);
        return consumable;
    }

    public Consumable addInventory(Consumable consumable) {
        consumable.setParcel(null);
        inventory.add(consumable);
        return consumable;
    }

    public Consumable takeInventory(Consumable inventoryConsumable) {
        inventory.remove(inventoryConsumable);
        return inventoryConsumable;
    }

    public void addInventory(String itemName, int quantity) {
        addInventory(DependencyManager.getInstance().getDependency(DataManager.class).getItemInfo(itemName), quantity);
    }

    public void updateInventory() {
        inventory.removeIf(consumable -> consumable.getTotalQuantity() <= 0);
    }
}

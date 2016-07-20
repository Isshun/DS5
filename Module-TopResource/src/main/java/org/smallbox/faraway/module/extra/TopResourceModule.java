package org.smallbox.faraway.module.extra;

import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.StructureModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 05/07/2015.
 */
public class TopResourceModule extends GameModule {
    private static class ConsumableCollection {
        public ItemInfo                 info;
        public List<ConsumableModel>    consumables = new ArrayList<>();
        public int                      count;

        public void update() {
            count = 0;
            for (ConsumableModel consumable: consumables) {
                count += consumable.getQuantity();
            }
        }
    }

    private Map<ItemInfo, ConsumableCollection> _consumablesCollection = new HashMap<>();
    private List<ConsumableModel>           _drinks = new ArrayList<>();
    private int                             _drinkCount;
    private int                             _waterNetwork = 100;
    private List<ConsumableModel>           _foods = new ArrayList<>();
    private int                             _foodCount;

    public int                      getConsumableCount(String name) { return getConsumableCount(Data.getData().getItemInfo(name)); }

    public int                      getConsumableCount(ItemInfo info) {
        int quantity = _consumablesCollection.containsKey(info) ? _consumablesCollection.get(info).count : 0;
        for (CharacterModel character: ModuleHelper.getCharacterModule().getCharacters()) {
            if (character.getInventory() != null && character.getInventory().getInfo() == info) {
                quantity += character.getInventoryQuantity();
            }
        }
        return quantity;
    }

    public List<ConsumableModel>    getFoods() { return _foods; }
    public int                      getFoodCount() { return _foodCount; }
    public List<ConsumableModel>    getDrinks() { return _drinks; }
    public int                      getDrinkCount() { return _drinkCount + _waterNetwork; }

    @Override
    protected void onGameStart(Game game) {
        _consumablesCollection.clear();
        ModuleHelper.getWorldModule().getConsumables().forEach(this::onAddConsumable);
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
        _foodCount = 0;
        for (ConsumableModel consumable: _foods) {
            _foodCount += consumable.getQuantity();
        }

        _drinkCount = 0;
        for (ConsumableModel consumable: _drinks) {
            _drinkCount += consumable.getQuantity();
        }

        _consumablesCollection.values().forEach(ConsumableCollection::update);
    }

    @Override
    public void onAddConsumable(ConsumableModel consumable) {
        ItemInfo info = consumable.getInfo();
        if (!_consumablesCollection.containsKey(info)) {
            _consumablesCollection.put(info, new ConsumableCollection());
        }
        _consumablesCollection.get(info).consumables.add(consumable);

        if (info.actions != null) {
            for (ItemInfo.ItemInfoAction actionInfo: info.actions) {
                if (actionInfo.effects != null && actionInfo.effects.food > 0) {
                    _foods.add(consumable);
                }
            }
        }
    }

    @Override
    public void onRemoveConsumable(ConsumableModel consumable) {
        ItemInfo info = consumable.getInfo();
        if (_consumablesCollection.containsKey(info)) {
            _consumablesCollection.get(info).consumables.remove(consumable);
        }
        _foods.remove(consumable);
    }
}

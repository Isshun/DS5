//package org.smallbox.faraway.module.extra;
//
//import org.smallbox.faraway.core.engine.module.GameModule;
//import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.Data;
//import org.smallbox.faraway.core.module.character.model.base.CharacterModel;
//import org.smallbox.faraway.core.world.model.ConsumableItem;
//import org.smallbox.faraway.core.world.model.MapObjectModel;
//import org.smallbox.faraway.core.world.model.StructureItem;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Alex
// */
//public class TopResourceModule extends GameModule {
//    private static class ConsumableCollection {
//        public ItemInfo                 info;
//        public List<ConsumableItem>    consumables = new ArrayList<>();
//        public int                      count;
//
//        public void update() {
//            count = 0;
//            for (ConsumableItem consumable: consumables) {
//                count += consumable.getQuantity();
//            }
//        }
//    }
//
//    private Map<ItemInfo, ConsumableCollection> _consumablesCollection = new HashMap<>();
//    private List<ConsumableItem>           _drinks = new ArrayList<>();
//    private int                             _drinkCount;
//    private int                             _waterNetwork = 100;
//    private List<ConsumableItem>           _foods = new ArrayList<>();
//    private int                             _foodCount;
//
//    public int                      getConsumableCount(String name) { return getConsumableCount(Application.data.getItemInfo(name)); }
//
//    public int                      getConsumableCount(ItemInfo info) {
//        int quantity = _consumablesCollection.containsKey(info) ? _consumablesCollection.get(info).count : 0;
//        for (CharacterModel character: ModuleHelper.getCharacterModule().getCharacters()) {
//            if (character.getInventory() != null && character.getInventory().getInfo() == info) {
//                quantity += character.getInventoryQuantity();
//            }
//        }
//        return quantity;
//    }
//
//    public List<ConsumableItem>    getFoods() { return _foods; }
//    public int                      getFoodCount() { return _foodCount; }
//    public List<ConsumableItem>    getDrinks() { return _drinks; }
//    public int                      getDrinkCount() { return _drinkCount + _waterNetwork; }
//
//    @Override
//    protected void onGameStart(Game game) {
//        _consumablesCollection.clear();
//        ModuleHelper.getWorldModule().getConsumables().forEach(this::onAddConsumable);
//    }
//
//    @Override
//    protected void onGameUpdate(Game game, int tick) {
//        _foodCount = 0;
//        for (ConsumableItem consumable: _foods) {
//            _foodCount += consumable.getQuantity();
//        }
//
//        _drinkCount = 0;
//        for (ConsumableItem consumable: _drinks) {
//            _drinkCount += consumable.getQuantity();
//        }
//
//        _consumablesCollection.values().forEach(ConsumableCollection::update);
//    }
//
//    @Override
//    public void onAddConsumable(ConsumableItem consumable) {
//        ItemInfo info = consumable.getInfo();
//        if (!_consumablesCollection.containsKey(info)) {
//            _consumablesCollection.put(info, new ConsumableCollection());
//        }
//        _consumablesCollection.get(info).consumables.add(consumable);
//
//        if (info.actions != null) {
//            for (ItemInfo.ItemInfoAction actionInfo: info.actions) {
//                if (actionInfo.effects != null && actionInfo.effects.food > 0) {
//                    _foods.add(consumable);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onRemoveConsumable(ConsumableItem consumable) {
//        ItemInfo info = consumable.getInfo();
//        if (_consumablesCollection.containsKey(info)) {
//            _consumablesCollection.get(info).consumables.remove(consumable);
//        }
//        _foods.remove(consumable);
//    }
//}

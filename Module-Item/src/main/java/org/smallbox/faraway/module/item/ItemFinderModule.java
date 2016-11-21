package org.smallbox.faraway.module.item;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.model.PathModel;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ItemFilter;
import org.smallbox.faraway.core.game.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.module.item.item.ItemModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFinderModule extends GameModule {
    @BindModule
    private ItemModule  _items;

    // TODO: setJob item
    // TODO: isJobLaunchable path
    public MapObjectModel getNearest(ItemFilter filter, CharacterModel character) {
        if (filter.needItem) {
            int bestDistance = Integer.MAX_VALUE;
            ItemModel bestItem = null;
            for (ItemModel item: _items.getItems()) {
                if (item.matchFilter(filter)) {
                    PathModel path = Application.pathManager.getPath(character.getParcel(), item.getParcel(), true, false);
                    if (path != null && path.getLength() < bestDistance) {
                        bestDistance = path.getLength();
                        bestItem = item;
                    }
                }
            }
            return bestItem;
        }

//        if (filter.needConsumable) {
//            int bestDistance = Integer.MAX_VALUE;
//            ConsumableModel bestConsumable = null;
//            for (ConsumableModel consumable: ModuleHelper.getWorldModule().getConsumables()) {
//                if (consumable.getJob() == null && consumable.matchFilter(filter)) {
//                    PathModel path = Application.pathManager.getPath(character.getParcel(), consumable.getParcel(), true, false);
//                    if (path != null && path.getLength() < bestDistance) {
//                        bestDistance = path.getLength();
//                        bestConsumable = consumable;
//                    }
//                }
//            }
//            return bestConsumable;
//        }

        return null;
    }

    public MapObjectModel getRandomNearest(ItemFilter filter, CharacterModel character) {
        return getRandomNearest(filter, character.getParcel());
    }

    public MapObjectModel getRandomNearest(ItemFilter filter, ParcelModel fromParcel) {
//        return _items.getItems().stream()
//                .skip((long) (Math.random() * _items.getItems().size() - 1))
//                .findFirst()
//                .orElse(null);

        List<ItemModel> list = new ArrayList<>(_items.getItems());

        // Get matching items
        int start = (int) (Math.random() * list.size());
        int length = list.size();
        int bestDistance = Integer.MAX_VALUE;
        Map<MapObjectModel, Integer> ObjectsMatchingFilter = new HashMap<>();
        for (int i = 0; i < length; i++) {
            MapObjectModel mapObject = list.get((i + start) % length);
            if (mapObject.matchFilter(filter)) {
                PathModel path = Application.pathManager.getPath(fromParcel, mapObject.getParcel(), false, false);
                if (path != null) {
                    ObjectsMatchingFilter.put(mapObject, path.getLength());
                    if (bestDistance > path.getLength()) {
                        bestDistance = path.getLength();
                    }
                }
            }
        }

        // Take first item at acceptable distance
        for (Map.Entry<MapObjectModel, Integer> entry: ObjectsMatchingFilter.entrySet()) {
            if (entry.getValue() <= bestDistance + Application.configurationManager.game.maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }
}
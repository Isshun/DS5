package org.smallbox.faraway.game.item;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.SuperGameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.path.PathManager;
import org.smallbox.faraway.core.world.model.ItemFilter;
import org.smallbox.faraway.core.world.model.MapObjectModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.character.model.PathModel;
import org.smallbox.faraway.game.character.model.base.CharacterModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameObject
public class ItemFinderModule extends SuperGameModule {
    @Inject private PathManager pathManager;
    @Inject private ItemModule itemModule;
    @Inject private ApplicationConfig applicationConfig;

    // TODO: setJob item
    // TODO: isJobLaunchable path
    public MapObjectModel getNearest(ItemFilter filter, CharacterModel character) {
        if (filter.needItem) {
            int bestDistance = Integer.MAX_VALUE;
            UsableItem bestItem = null;
            for (UsableItem item: itemModule.getAll()) {
                if (item.matchFilter(filter)) {
                    PathModel path = pathManager.getPath(character.getParcel(), item.getParcel(), true, false, true);
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
//            ConsumableItem bestConsumable = null;
//            for (ConsumableItem consumable: ModuleHelper.getWorldModule().getConsumables()) {
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

    public MapObjectModel getRandomNearest(ItemFilter filter, Parcel fromParcel) {
//        return _items.getItems().stream()
//                .skip((long) (Math.random() * _items.getItems().size() - 1))
//                .findFirst()
//                .orElse(null);

        List<UsableItem> list = new ArrayList<>(itemModule.getAll());

        // Get matching items
        int start = (int) (Math.random() * list.size());
        int length = list.size();
        int bestDistance = Integer.MAX_VALUE;
        Map<MapObjectModel, Integer> ObjectsMatchingFilter = new HashMap<>();
        for (int i = 0; i < length; i++) {
            MapObjectModel mapObject = list.get((i + start) % length);
            if (mapObject.matchFilter(filter)) {
                PathModel path = pathManager.getPath(fromParcel, mapObject.getParcel(), false, false, true);
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
            if (entry.getValue() <= bestDistance + applicationConfig.game.maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }
}
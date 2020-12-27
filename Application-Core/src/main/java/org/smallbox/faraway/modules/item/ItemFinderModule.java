package org.smallbox.faraway.modules.item;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.core.module.path.PathManager;
import org.smallbox.faraway.core.module.world.model.ItemFilter;
import org.smallbox.faraway.core.module.world.model.MapObjectModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.model.PathModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@GameObject
public class ItemFinderModule extends GameModule {

    @Inject
    private PathManager pathManager;

    @Inject
    private ItemModule _items;

    @Inject
    private ApplicationConfigService applicationConfigService;

    // TODO: setJob item
    // TODO: isJobLaunchable path
    public MapObjectModel getNearest(ItemFilter filter, CharacterModel character) {
        if (filter.needItem) {
            int bestDistance = Integer.MAX_VALUE;
            UsableItem bestItem = null;
            for (UsableItem item: _items.getItems()) {
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

    public MapObjectModel getRandomNearest(ItemFilter filter, ParcelModel fromParcel) {
//        return _items.getItems().stream()
//                .skip((long) (Math.random() * _items.getItems().size() - 1))
//                .findFirst()
//                .orElse(null);

        List<UsableItem> list = new ArrayList<>(_items.getItems());

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
            if (entry.getValue() <= bestDistance + applicationConfigService.getGameInfo().maxNearDistance) {
                return entry.getKey();
            }
        }

        return null;
    }

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }
}
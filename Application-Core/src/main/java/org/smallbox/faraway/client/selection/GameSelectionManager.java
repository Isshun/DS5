package org.smallbox.faraway.client.selection;

import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.layer.LayerManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.game.area.AreaModuleBase;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.game.consumable.ConsumableModule;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.plant.PlantModule;
import org.smallbox.faraway.game.structure.StructureModule;
import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.util.log.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@GameObject
public class GameSelectionManager extends GameManager {
    @Inject private LayerManager layerManager;
    @Inject private GameActionManager gameActionManager;
    @Inject private CharacterModule characterModule;
    @Inject private ConsumableModule consumableModule;
    @Inject private ItemModule itemModule;
    @Inject private StructureModule structureModule;
    @Inject private PlantModule plantModule;
    @Inject private DependencyManager dependencyManager;
    @Inject private MainPanelController mainPanelController;
    @Inject private Viewport viewport;

    private Collection<AreaModuleBase> specializedAreaModules;

    @OnInit
    private void onInit() {
        specializedAreaModules = dependencyManager.getSubTypesOf(AreaModuleBase.class);
    }

    public <T extends ObjectModel> void setSelected(Collection<T> selected) {
        if (selected != null) {
            _selected.clear();
            _selected.addAll(selected);
        }
    }

    public void select(ObjectModel object) {
        Log.info("Select item: " + object.getClass().getSimpleName());

        _selected.clear();
        _selected.add(object);

        gameActionManager.callActions(OnGameSelectAction.class, object);
    }

    public void select(Collection<? extends ObjectModel> objects) {
        objects.forEach(object -> {
            Log.info("Select item: " + object.getClass().getSimpleName());
            gameActionManager.callActions(OnGameSelectAction.class, object);
        });

        _selected.clear();
        _selected.addAll(objects);
    }

    public boolean selectContains(ObjectModel object) {
        return _selected != null && _selected.contains(object);
    }

//    public void registerSelectionParcelListener(SelectionParcelListener selectionParcelListener) {
//        selectionParcelListeners.add(selectionParcelListener);
//    }

    public <T extends ObjectModel> T getSelected(Class<T> cls) {
        return _selected.size() == 1 ? _selected.stream().filter(cls::isInstance).map(cls::cast).findFirst().orElse(null) : null;
    }

    public void clear() {
        _selected.clear();
        mainPanelController.openLast();
    }

    public interface OnSelectionListener {
        boolean onSelection(List<Parcel> parcels);
    }

    private Parcel _lastParcel;
    private OnSelectionListener _selectionListener;
    private final Collection<ObjectModel> _selected = new ConcurrentLinkedQueue<>();

    public OnSelectionListener getSelectionListener() {
        return _selectionListener;
    }

    public void setSelectionListener(OnSelectionListener selectionListener) {
        _selectionListener = selectionListener;
    }

    public Collection<? extends ObjectModel> getSelected() {
        return _selected;
    }

    // Unique parcel
    public void select(int mapX, int mapY) {
        select(mapX, mapY, mapX, mapY);
    }

    // Square selection
    public void select(int fromMapX, int fromMapY, int toMapX, int toMapY) {
        List<Parcel> parcelList = WorldHelper.getParcelInRect(fromMapX, fromMapY, toMapX, toMapY, viewport.getFloor());
        Log.info("Click on map for parcels: %s", parcelList);

        List<ObjectModel> objects = new ArrayList<>();

        // Call GameActionManager
        if (gameActionManager.hasAction()) {
            parcelList.forEach(parcel -> gameActionManager.selectParcel(parcel));
        }

        // Display info
        else {

            parcelList.forEach(parcel -> {
                List<AreaModuleBase> matchingAreaModules = specializedAreaModules.stream().filter(areaModuleBase -> areaModuleBase.hasArea(parcel)).collect(Collectors.toList());

                characterModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().ifPresent(objects::add);
                consumableModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().ifPresent(objects::add);
                itemModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().ifPresent(objects::add);
                structureModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().ifPresent(objects::add);
                plantModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().ifPresent(objects::add);
                Optional.ofNullable(CollectionUtils.isNotEmpty(matchingAreaModules) ? matchingAreaModules.get(0).getArea(parcel) : null).ifPresent(objects::add);
            });

            if (CollectionUtils.isNotEmpty(objects)) {
                if (_selected.size() == 1 && objects.contains(_selected.iterator().next())) {
                    int index = objects.indexOf(_selected.iterator().next());
                    select(objects.get((index + 1) % objects.size()));
                } else if (fromMapX == toMapX && fromMapY == toMapY) {
                    select(objects.get(0));
                } else {
                    select(objects);
                }
            } else {
                select(parcelList);
            }
        }
    }

}

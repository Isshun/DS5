package org.smallbox.faraway.client.selection;

import org.apache.commons.lang3.ObjectUtils;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.dependencyInjector.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ConsumableItem;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModuleBase;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.modules.consumable.ConsumableModule;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.item.UsableItem;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.log.Log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@GameObject
public class GameSelectionManager extends GameManager {

    @Inject
    private LayerManager layerManager;

    @Inject
    private GameActionManager gameActionManager;

    @Inject
    private CharacterModule characterModule;

    @Inject
    private ConsumableModule consumableModule;

    @Inject
    private ItemModule itemModule;

    @Inject
    private StructureModule structureModule;

//    private Collection<SelectionParcelListener> selectionParcelListeners = new ConcurrentLinkedQueue<>();
    private Collection<SelectionAreaListener> selectionAreaListeners = new ConcurrentLinkedQueue<>();
    private Collection<AreaModuleBase> specializedAreaModules;

    @OnInit
    private void init() {
        specializedAreaModules = DependencyInjector.getInstance().getSubTypesOf(AreaModuleBase.class);
    }

    public <T extends ObjectModel> void setSelected(Collection<T> selected) {
        if (selected != null) {
            _selected.clear();
            _selected.addAll(selected);
        }
    }

    public <T extends ObjectModel> void select(T object) {
        Log.info("Select item: " + object.getClass().getSimpleName());

        _selected.clear();
        _selected.add(object);

        gameActionManager.callActions(OnGameSelectAction.class, object);
    }

    public boolean selectContains(ObjectModel object) {
        return _selected != null && _selected.contains(object);
    }

//    public void registerSelectionParcelListener(SelectionParcelListener selectionParcelListener) {
//        selectionParcelListeners.add(selectionParcelListener);
//    }

    public void registerSelectionAreaListener(SelectionAreaListener selectionAreaListener) {
        selectionAreaListeners.add(selectionAreaListener);
    }

    public <T extends ObjectModel> T getSelected(Class<T> cls) {
        return _selected.size() == 1 ? _selected.stream().filter(cls::isInstance).map(cls::cast).findFirst().orElse(null) : null;
    }

    public interface OnSelectionListener {
        boolean onSelection(List<ParcelModel> parcels);
    }

    private ParcelModel                     _lastParcel;
    private Queue<AbsInfoLuaController<?>> _lastControllers;
    private LuaController _selectionPreController;
    private OnSelectionListener _selectionListener;
    private Collection<AbsInfoLuaController<?>> _infoControllers;
    private Map<AbsInfoLuaController<?>, AbsInfoLuaController<?>> _infoSubControllers;
    private final Collection<ObjectModel> _selected = new ConcurrentLinkedQueue<>();

    public GameSelectionManager() {
        _lastControllers = new ConcurrentLinkedQueue<>();
        _infoSubControllers = new ConcurrentHashMap<>();
        _infoControllers = new ConcurrentLinkedQueue<>();
    }

    public void registerSelection(AbsInfoLuaController<?> controller) {
        _infoControllers.add(controller);
    }

    public void registerSelection(AbsInfoLuaController<?> controller, AbsInfoLuaController<?> parent) {
        _infoSubControllers.put(controller, parent);
    }

    public void registerSelectionPre(LuaController controller) {
        _selectionPreController = controller;
    }

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
        ParcelModel parcel = WorldHelper.getParcel(mapX, mapY, layerManager.getViewport().getFloor());
        Log.info("Click on map at parcel: %s", parcel);
        if (parcel != null) {

            if (gameActionManager.hasAction()) {
                gameActionManager.selectParcel(parcel);
            } else {
                List<AreaModuleBase> matchingAreaModules = specializedAreaModules.stream().filter(areaModuleBase -> areaModuleBase.hasArea(parcel)).collect(Collectors.toList());
                AreaModel area = CollectionUtils.isNotEmpty(matchingAreaModules) ? matchingAreaModules.get(0).getArea(parcel) : null;
                CharacterModel character = characterModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().orElse(null);
                ConsumableItem consumable = consumableModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().orElse(null);
                UsableItem item = itemModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().orElse(null);
                StructureItem structure = structureModule.getAll().stream().filter(c -> c.getParcel() == parcel).findFirst().orElse(null);

                select(ObjectUtils.firstNonNull(area, character, consumable, item, structure, parcel));
            }
        }
    }

    // Square selection
    public void select(int fromMapX, int fromMapY, int toMapX, int toMapY) {
        List<ParcelModel> parcelList = WorldHelper.getParcelInRect(fromMapX, fromMapY, toMapX, toMapY, layerManager.getViewport().getFloor());
        Log.info("Click on map for parcels: %s", parcelList);

        gameActionManager.selectParcels(parcelList);
//            if (parcelList != null) {
//
//                if (_selectionListener != null) {
//                    if (_selectionListener.onSelection(parcelList)) {
//                        _selectionListener = null;
//                    }
//                }
//
//                else {
//                    _selected = null;
//                    doSelectionMultiple(parcelList);
//                }
//            }
    }

    private void doSelectionUnique(ParcelModel parcel) {
        if (_selectionPreController != null) {
            _selectionPreController.setVisible(true);
        }

        // Click sur nouvelle parcel
        if (_lastParcel != parcel) {
            _lastParcel = parcel;

            _lastControllers.clear();
            _infoControllers.stream()
                    .filter(controller -> controller.getObjectOnParcel(parcel) != null)
                    .forEach(controller -> _lastControllers.add(controller));

            if (CollectionUtils.isNotEmpty(_lastControllers)) {
                displayController(_lastControllers.peek(), Collections.singletonList(parcel));
            }
        }

        // Click sur la même parcel que précédement
        else {
            if (CollectionUtils.isNotEmpty(_lastControllers)) {
                _lastControllers.add(_lastControllers.poll());
                displayController(_lastControllers.peek(), Collections.singletonList(parcel));
            }
        }

    }

    private void doSelectionMultiple(List<ParcelModel> parcelList) {
        if (_selectionPreController != null) {
            _selectionPreController.setVisible(true);
        }

        _infoControllers.stream()
                .filter(controller -> parcelList.stream().anyMatch(parcel -> controller.getObjectOnParcel(parcel) != null))
                .findFirst()
                .ifPresent(controller -> displayController(controller, parcelList));
    }

    private void displayController(AbsInfoLuaController<?> controller, Collection<ParcelModel> parcels) {

        // Display sub controller
        _infoSubControllers.entrySet().stream()
                .filter(entry -> entry.getValue() == controller)
                .peek(entry -> entry.getKey().setVisible(false))
                .filter(entry -> parcels.stream().anyMatch(parcel -> entry.getKey().getObjectOnParcel(parcel) != null))
                .findFirst()
                .ifPresent(entry -> entry.getKey().displayToto(parcels));

        // Display controller
        controller.displayToto(parcels);

    }

}

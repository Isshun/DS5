package org.smallbox.faraway.client;

import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.common.ParcelCommon;
import org.smallbox.faraway.common.dependencyInjector.GameObject;
import org.smallbox.faraway.common.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Alex on 16/07/2017.
 */
@GameObject
public class SelectionManager {

    public <T extends ObjectModel> void setSelected(Queue<T> selected) {
        _selected = selected;
    }

    public boolean selectContains(ObjectModel object) {
        return _selected != null && _selected.contains(object);
    }

    public interface OnSelectionListener {
        boolean onSelection(List<ParcelCommon> parcels);
    }

    private ParcelCommon                     _lastParcel;
    private Queue<AbsInfoLuaController<?>> _lastControllers;
    private LuaController _selectionPreController;
    private OnSelectionListener _selectionListener;
    private Collection<AbsInfoLuaController<?>> _infoControllers;
    private Map<AbsInfoLuaController<?>, AbsInfoLuaController<?>> _infoSubControllers;
    private Collection<? extends ObjectModel> _selected;

    public SelectionManager() {
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

    public void select(int fromMapX, int fromMapY, int toMapX, int toMapY) {
//
//        // Square selection
//        if (fromMapX != toMapX || fromMapY != toMapY) {
//            List<ParcelModel> parcelList = WorldHelper.getParcelInRect(fromMapX, fromMapY, toMapX, toMapY, ApplicationClient.layerManager.getViewport().getFloor());
//            Log.info("Click on map for parcels: %s", parcelList);
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
//        }
//
//        // Unique parcel
//        else {
//            ParcelModel parcel = WorldHelper.getParcel(fromMapX, fromMapY, ApplicationClient.layerManager.getViewport().getFloor());
//            Log.info("Click on map at parcel: %s", parcel);
//            if (parcel != null) {
//
//                if (_selectionListener != null) {
//                    if (_selectionListener.onSelection(Collections.singletonList(parcel))) {
//                        _selectionListener = null;
//                    }
//                }
//
//                else {
//                    _selected = null;
//                    doSelectionUnique(parcel);
//                }
//            }
//        }

    }

    private void doSelectionUnique(ParcelCommon parcel) {
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

    private void doSelectionMultiple(List<ParcelCommon> parcelList) {
        if (_selectionPreController != null) {
            _selectionPreController.setVisible(true);
        }

        _infoControllers.stream()
                .filter(controller -> parcelList.stream().anyMatch(parcel -> controller.getObjectOnParcel(parcel) != null))
                .findFirst()
                .ifPresent(controller -> displayController(controller, parcelList));
    }

    private void displayController(AbsInfoLuaController<?> controller, Collection<ParcelCommon> parcels) {

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

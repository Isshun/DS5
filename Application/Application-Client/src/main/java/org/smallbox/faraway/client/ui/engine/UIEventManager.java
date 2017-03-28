package org.smallbox.faraway.client.ui.engine;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIDropDown;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.util.CollectionUtils;
import org.smallbox.faraway.util.Log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class UIEventManager {

    private Map<View, OnDragListener>       _onDragListeners;
    private Map<View, OnClickListener>      _onClickListeners;
    private Map<View, OnClickListener>      _onRightClickListeners;
    private Map<View, OnClickListener>      _onMouseWheelUpListeners;
    private Map<View, OnClickListener>      _onMouseWheelDownListeners;
    private Map<View, OnFocusListener>      _onFocusListeners;
    private Map<View, OnKeyListener>        _onKeysListeners;
    private UIDropDown                      _currentDropDown;
    private Map<View, Object>               _dropViews;
    private Collection<AbsInfoLuaController<?>> _infoControllers;
    private Map<AbsInfoLuaController<?>, AbsInfoLuaController<?>> _infoSubControllers;
    private ParcelModel                     _lastParcel;
    private Queue<AbsInfoLuaController<?>>  _lastControllers;
    private LuaController                   _selectionPreController;

    public UIEventManager() {
        _lastControllers = new ConcurrentLinkedQueue<>();
        _infoSubControllers = new ConcurrentHashMap<>();
        _infoControllers = new ConcurrentLinkedQueue<>();
        _onDragListeners = new ConcurrentSkipListMap<>();
        _onClickListeners = new ConcurrentSkipListMap<>();
        _onRightClickListeners = new ConcurrentSkipListMap<>();
        _onFocusListeners = new ConcurrentSkipListMap<>();
        _onKeysListeners = new ConcurrentSkipListMap<>();
        _onMouseWheelUpListeners = new ConcurrentSkipListMap<>();
        _onMouseWheelDownListeners = new ConcurrentSkipListMap<>();
        _dropViews = new ConcurrentSkipListMap<>();
    }

    public void setOnFocusListener(View view, OnFocusListener onFocusListener) {
        if (onFocusListener == null) {
            _onFocusListeners.remove(view);
        } else {
            _onFocusListeners.put(view, onFocusListener);
        }
    }

    public void setOnClickListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onClickListeners.remove(view);
        } else {
            _onClickListeners.put(view, onClickListener);
        }
    }

    public void setOnDragListener(View view, OnDragListener onClickListener) {
        if (onClickListener == null) {
            _onDragListeners.remove(view);
        } else {
            _onDragListeners.put(view, onClickListener);
        }
    }

    public void setOnMouseWheelUpListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onMouseWheelUpListeners.remove(view);
        } else {
            _onMouseWheelUpListeners.put(view, onClickListener);
        }
    }

    public void setOnMouseWheelDownListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onMouseWheelDownListeners.remove(view);
        } else {
            _onMouseWheelDownListeners.put(view, onClickListener);
        }
    }

    public void setOnRightClickListener(View view, OnClickListener onClickListener) {
        if (onClickListener == null) {
            _onRightClickListeners.remove(view);
        } else {
            _onRightClickListeners.put(view, onClickListener);
        }
    }

    private OnSelectionListener _selectionListener;

    public void addDropZone(View view) {
        _dropViews.put(view, view);
    }

    public Map<View, Object> getDropViews() {
        return _dropViews;
    }

    public OnSelectionListener getSelectionListener() {
        return _selectionListener;
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

    public interface OnSelectionListener {
        boolean onSelection(List<ParcelModel> parcels);
    }

    public void setSelectionListener(OnSelectionListener selectionListener) {
        _selectionListener = selectionListener;
    }

    public abstract static class OnDragListener {
        public View hoverView;

        public abstract void onDrag(GameEvent event);
        public abstract void onDrop(GameEvent event, View dropView);
        public abstract void onHover(GameEvent event, View dropView);
        public abstract void onHoverExit(GameEvent event, View dropView);
    }

    public OnDragListener drag(GameEvent event, int x, int y) {
        for (Map.Entry<View, OnDragListener> entry: _onDragListeners.entrySet()) {
            if (entry.getKey().contains(x, y)) {
                entry.getValue().onDrag(event);
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean click(GameEvent event, int x, int y) {
        View bestView = null;
        int bestDepth = -1;
        for (View view: _onClickListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && view.contains(x, y) && view.getDeep() > bestDepth) {
                bestDepth = view.getDeep();
                bestView = view;
            }
        }

        if (_currentDropDown != null) {
            _currentDropDown.setOpen(false);
            _currentDropDown = null;
        }

        // Click on UI
        if (bestView != null) {
            bestView.click(event);
//            _onClickListeners.get(bestView).onClick();
            return true;
        }

        // Click on map
        if (Application.gameManager.isRunning()) {

            int fromMapX = ApplicationClient.layerManager.getViewport().getWorldPosX(ApplicationClient.inputManager.getTouchDownX());
            int fromMapY = ApplicationClient.layerManager.getViewport().getWorldPosY(ApplicationClient.inputManager.getTouchDownY());
            int toMapX = ApplicationClient.layerManager.getViewport().getWorldPosX(ApplicationClient.inputManager.getTouchDragX());
            int toMapY = ApplicationClient.layerManager.getViewport().getWorldPosY(ApplicationClient.inputManager.getTouchDragY());

            // Square selection
            if (fromMapX != toMapX || fromMapY != toMapY) {
                List<ParcelModel> parcelList = WorldHelper.getParcelInRect(fromMapX, fromMapY, toMapX, toMapY, ApplicationClient.layerManager.getViewport().getFloor());
                Log.info("Click on map for parcels: %s", parcelList);
                if (parcelList != null) {

                    if (_selectionListener != null) {
                        if (_selectionListener.onSelection(parcelList)) {
                            _selectionListener = null;
                        }
                    }

                    else {
                        ApplicationClient.selected = null;
                        doSelectionMultiple(parcelList);
                    }
                }
            }

            // Unique parcel
            else {
                ParcelModel parcel = WorldHelper.getParcel(fromMapX, fromMapY, ApplicationClient.layerManager.getViewport().getFloor());
                Log.info("Click on map at parcel: %s", parcel);
                if (parcel != null) {

                    if (_selectionListener != null) {
                        if (_selectionListener.onSelection(Collections.singletonList(parcel))) {
                            _selectionListener = null;
                        }
                    }

                    else {
                        ApplicationClient.selected = null;
                        doSelectionUnique(parcel);
                    }
                }
            }
        }

        return false;
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

    public boolean rightClick(GameEvent event, int x, int y) {
        for (View view: _onRightClickListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onRightClickListeners.get(view).onClick(event);
                return true;
            }
        }
        return false;
    }

    public boolean mouseWheelUp(GameEvent event, int x, int y) {
        for (View view: _onMouseWheelUpListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onMouseWheelUpListeners.get(view).onClick(event);
                return true;
            }
        }
        return false;
    }

    public boolean mouseWheelDown(GameEvent event, int x, int y) {
        for (View view: _onMouseWheelDownListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onMouseWheelDownListeners.get(view).onClick(event);
                return true;
            }
        }
        return false;
    }

    public boolean hasClickListener(View view) {
        return _onClickListeners.containsKey(view);
    }

    public boolean keyRelease(int key) {
        for (View view: _onKeysListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && hasFocus(view)) {
                _onKeysListeners.get(view).onKeyRelease(view, key);
                return true;
            }
        }
        return false;
    }

    private boolean hasFocus(View view) {
        return true;
    }

    public void onMouseMove(int x, int y) {
        ApplicationClient.uiManager.getViews().stream()
                .filter(view -> view.isVisible() && view.isActive() && hasVisibleHierarchy(view))
                .forEach(view -> {
                    if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                        view.onEnter();
                    } else if (view.isFocus()) {
                        view.onExit();
                    }
                });
    }

    public boolean has(int x, int y) {
        for (View view: _onClickListeners.keySet()) {
            if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasVisibleHierarchy(View view) {
        while (view != null) {
            if (!view.isVisible()) {
                return false;
            }
            if (!view.isActive()) {
                return false;
            }
            view = view.getParent();
        }
        return true;
    }

    public void removeOnKeyListener(View view) {
        _onKeysListeners.remove(view);
    }

    public void removeOnClickListener(View view) {
        _onClickListeners.remove(view);
    }

    public void removeOnFocusListener(View view) {
        _onFocusListeners.remove(view);
    }

    public void clear() {
        _onClickListeners.clear();
        _onRightClickListeners.clear();
        _onFocusListeners.clear();
        _onKeysListeners.clear();
        _onMouseWheelDownListeners.clear();
        _onMouseWheelUpListeners.clear();
    }

    public void removeListeners(Collection<View> views) {
        views.forEach(this::removeListeners);
    }

    public void removeListeners(View view) {
        _dropViews.remove(view);
        _onDragListeners.remove(view);
        _onRightClickListeners.remove(view);
        _onRightClickListeners.remove(view);
        _onClickListeners.remove(view);
        _onFocusListeners.remove(view);
        _onKeysListeners.remove(view);
        _onMouseWheelDownListeners.remove(view);
        _onMouseWheelUpListeners.remove(view);
    }

    public void setOnKeyListener(View view, OnKeyListener onKeyListener) {
        _onKeysListeners.put(view, onKeyListener);
    }

    public void setCurrentDropDown(UIDropDown dropDown) {
        _currentDropDown = dropDown;
    }
}
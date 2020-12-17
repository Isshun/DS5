package org.smallbox.faraway.client.ui.engine;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.EventManager;
import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.manager.InputManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIDropDown;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Predicate;

@ApplicationObject
public class UIEventManager implements EventManager {

    private Map<View, OnDragListener>       _onDragListeners;
    private Map<View, OnClickListener>      _onClickListeners;
    private Map<View, OnClickListener>      _onRightClickListeners;
    private Map<View, OnClickListener>      _onMouseWheelUpListeners;
    private Map<View, OnClickListener>      _onMouseWheelDownListeners;
    private Map<View, OnFocusListener>      _onFocusListeners;
    private Map<View, OnKeyListener>        _onKeysListeners;
    private UIDropDown                      _currentDropDown;
    private Map<View, Object>               _dropViews;
    private OnDragListener                  _dragListener;

    @Inject
    private LayerManager layerManager;

    @Inject
    private SelectionManager selectionManager;

    @Inject
    private InputManager inputManager;

    @Inject
    private UIManager uiManager;

    public UIEventManager() {
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


    public void addDropZone(View view) {
        _dropViews.put(view, view);
    }

    public Map<View, Object> getDropViews() {
        return _dropViews;
    }

    public abstract static class OnDragListener {
        public View hoverView;

        public abstract void onDrag(int x, int y);
        public abstract void onDrop(int x, int y, View dropView);
        public abstract void onHover(int x, int y, View dropView);
        public abstract void onHoverExit(int x, int y, View dropView);
    }

    public OnDragListener drag(int x, int y) {
        for (Map.Entry<View, OnDragListener> entry: _onDragListeners.entrySet()) {
            if (entry.getKey().contains(x, y)) {
                entry.getValue().onDrag(x, y);
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean click(int x, int y) {
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
            bestView.click(x, y);
//            _onClickListeners.get(bestView).onClick();
            return true;
        }

        // Click on map
        if (Application.gameManager.isRunning()) {
            selectionManager.select(
                    layerManager.getViewport().getWorldPosX(inputManager.getTouchDownX()),
                    layerManager.getViewport().getWorldPosY(inputManager.getTouchDownY()),
                    layerManager.getViewport().getWorldPosX(inputManager.getTouchDragX()),
                    layerManager.getViewport().getWorldPosY(inputManager.getTouchDragY())
            );
        }

        return false;
    }

    @Override
    public boolean onMousePress(int x, int y, int button) {

        if (has(x, y)) {
            return true;
        }

        if (button == Input.Buttons.LEFT) {
            _dragListener = drag(x, y);
            if (_dragListener != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onMouseRelease(int x, int y, int button) {

        // On drag drop
        if (button == Input.Buttons.LEFT && _dragListener != null) {
            Map.Entry<View, Object> dropViewEntry = ApplicationClient.uiEventManager.getDropViews().entrySet().stream()
                    .filter(entry -> entry.getKey().contains(x, y))
                    .findAny().orElse(null);
            if (dropViewEntry != null) {
                _dragListener.onHoverExit(x, y, dropViewEntry.getKey());
                _dragListener.onDrop(x, y, dropViewEntry.getKey());
            }
            _dragListener = null;
            return true;
        }

        // Cleat UiEventManager selection listener when right button is clicked
        if (button == Input.Buttons.RIGHT && selectionManager.getSelectionListener() != null) {
            selectionManager.setSelectionListener(null);
        }

        if (button == Input.Buttons.LEFT && ApplicationClient.uiEventManager.click(x, y)) {
            return true;
        }

        if (button == Input.Buttons.RIGHT && ApplicationClient.uiEventManager.rightClick(x, y)) {
            return true;
        }

        if (button == Input.Buttons.FORWARD && ApplicationClient.uiEventManager.mouseWheelUp(x, y)) {
            return true;
        }

        if (button == Input.Buttons.BACK && ApplicationClient.uiEventManager.mouseWheelDown(x, y)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onMouseMove(int x, int y, boolean pressed) {


        // On drag hover
        if (_dragListener != null) {
            Map.Entry<View, Object> dropViewEntry = ApplicationClient.uiEventManager.getDropViews().entrySet().stream()
                    .filter(entry -> entry.getKey().contains(x, y))
                    .findAny().orElse(null);
            if (dropViewEntry != null) {
                if (_dragListener.hoverView != null) {
                    _dragListener.onHoverExit(x, y, _dragListener.hoverView);
                }
                _dragListener.hoverView = dropViewEntry.getKey();
                _dragListener.onHover(x, y, dropViewEntry.getKey());
            } else if (_dragListener.hoverView != null) {
                _dragListener.onHoverExit(x, y, _dragListener.hoverView);
                _dragListener.hoverView = null;
            }
            return false;
        }

        uiManager.getViews().stream()
                .filter(view -> view.isVisible() && view.isActive() && hasVisibleHierarchy(view))
                .forEach(view -> {
                    if (hasVisibleHierarchy(view) && view.contains(x, y)) {
                        view.onEnter();
                    } else if (view.isFocus()) {
                        view.onExit();
                    }
                });

        return false;
    }

    public boolean rightClick(int x, int y) {
        for (View view: _onRightClickListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onRightClickListeners.get(view).onClick(x, y);
                return true;
            }
        }
        return false;
    }

    public boolean mouseWheelUp(int x, int y) {
        for (View view: _onMouseWheelUpListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onMouseWheelUpListeners.get(view).onClick(x, y);
                return true;
            }
        }
        return false;
    }

    public boolean mouseWheelDown(int x, int y) {
        for (View view: _onMouseWheelDownListeners.keySet()) {
            if (view.isActive() && hasVisibleHierarchy(view) && view.contains(x, y)) {
                _onMouseWheelDownListeners.get(view).onClick(x, y);
                return true;
            }
        }
        return false;
    }

    public boolean hasClickListener(View view) {
        return _onClickListeners.containsKey(view);
    }

    public OnClickListener getClickListener(View view) {
        return _onClickListeners.get(view);
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
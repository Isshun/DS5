package org.smallbox.faraway.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.RootView;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIDropDown;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.smallbox.faraway.core.engine.GameEventListener.*;

public class UIManager {

    private Map<String, LuaValue> _styles = new ConcurrentHashMap<>();
    private UIEventManager.OnDragListener _dragListener;

    public void clearViews() {
        _rootViews.clear();
    }

    public void addDropsDowns(UIDropDown view) {
        _dropsDowns.add(view);
    }

    public Collection<RootView> getRootViews() {
        return _rootViews;
    }

    public Collection<View> getSubViews() {
        return _subViews.keySet();
    }

    public Collection<View> getViews() {
        return _views;
    }

    public List<View> findByGroup(String group) {
        return _groups.get(group);
    }

    public void removeView(View view) {
        _views.remove(view);
    }

    public void addStyle(String id, LuaValue style) {
        _styles.put(id, style);
    }

    public LuaValue getStyle(String id) {
        return _styles.get(id);
    }

    private static class ContextEntry {
        public String                   label;
        public OnClickListener listener;

        public ContextEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private long                        _lastLeftClick;
    private int                         _update;
    private UIFrame                     _context;

    private Queue<RootView>             _rootViews = new LinkedBlockingQueue<>();
    private Map<View, String>           _subViews = new ConcurrentHashMap<>();
    private Set<View>                   _views = new ConcurrentHashSet<>();
    private Queue<UIDropDown>           _dropsDowns = new ConcurrentLinkedQueue<>();
    private Queue<Integer>              _visibleViews = new ConcurrentLinkedQueue<>();
    private Map<String, List<View>>     _groups = new ConcurrentHashMap<>();

    public UIManager() {
        _context = new UIFrame(null);
        _context.setVisible(false);
    }

    public void addRootView(RootView rootView) {

        _rootViews.add(rootView);

        fixRootAndSubViews();

//        if (_rootViews.stream().noneMatch(rootView -> rootView.getView().getName().equals(view.getName()))) {
//
//            addViewRecurse(rootView.getView());
//        } else {
//            Log.warning("rootview already exists: " + view.getName());
//        }
//
//        _subViews.forEach((subView, parentName) -> {
//            if (rootView.getName() != null && rootView.getName().equals(parentName)) {
//                rootView.addView(subView);
//            }
//        });
    }

    private void fixRootAndSubViews() {
        _subViews.forEach((subView, parentName) -> {
            _views.stream()
                    .filter(view -> parentName.equals(view.getName()))
                    .findAny()
                    .ifPresent(view -> {
                        if (view.getViews().stream().noneMatch(v -> subView == v)) {
                            view.addView(subView);
                            subView.setParent(view);
                        }
                    });
            _rootViews.stream()
                    .map(RootView::getView)
                    .filter(view -> parentName.equals(view.getName()))
                    .findAny()
                    .ifPresent(view -> {
                        if (view.getViews().stream().noneMatch(v -> subView == v)) {
                            view.addView(subView);
                            subView.setParent(view);
                        }
                    });
        });
    }

    public void addSubView(View subViewToAdd, String parentName) {
        if (_subViews.keySet().stream().noneMatch(subView -> subView.getName().equals(subViewToAdd.getName()))) {
            _subViews.put(subViewToAdd, parentName);
        }

        fixRootAndSubViews();
    }

    public void addView(View view) {
        if (CollectionUtils.notContains(_views, view)) {
            if (view.getPath() != null && _views.stream().noneMatch(v -> view.getPath().equals(v.getPath()))) {
                _views.add(view);
            }
        }
    }

//    public void reload() {
//        _visibleViews.removeAllViews();
//        _rootViews.stream()
//                .filter(view -> view.getViews() != null)
//                .forEach(view -> view.getViews().stream()
//                        .filter(View::isVisible)
//                        .forEach(subview -> _visibleViews.add(subview.getId())));
//        _rootViews.removeAllViews();
//        _dropsDowns.removeAllViews();
//        ApplicationClient.uiEventManager.removeAllViews();
//    }
//
//    public void restore() {
//        _rootViews.stream()
//                .filter(view -> view.getViews() != null)
//                .forEach(view -> view.getViews().stream()
//                        .filter(subview -> _visibleViews.contains(subview.getId()))
//                        .forEach(subview -> subview.setVisible(true)));
//    }

    public boolean onKeyEvent(Action action, int key, Modifier modifier) {
//        if (action == Action.RELEASED) {
//            if (checkKeyboard(new GameEvent(key), key)) {
//                return false;
//            }
//        }

        return false;
    }

    /**
     *
     * @param event
     * @param action
     * @param button
     * @param x
     * @param y
     * @param rightPressed
     * @return L'evenement est consommé
     */
    public boolean onMouseEvent(GameEvent event, Action action, MouseButton button, int x, int y, boolean rightPressed) {
        for (ModuleBase module: Application.moduleManager.getModules()) {
            if (!event.consumed && module.isLoaded() && module.onMouseEvent(action, button, x, y)) {
                return true;
            }
        }

        // On drag hover
        if (!event.consumed && action == Action.MOVE && _dragListener != null) {
            Map.Entry<View, Object> dropViewEntry = ApplicationClient.uiEventManager.getDropViews().entrySet().stream()
                    .filter(entry -> entry.getKey().contains(event.mouseEvent.x, event.mouseEvent.y))
                    .findAny().orElse(null);
            if (dropViewEntry != null) {
                if (_dragListener.hoverView != null) {
                    _dragListener.onHoverExit(event, _dragListener.hoverView);
                }
                _dragListener.hoverView = dropViewEntry.getKey();
                _dragListener.onHover(event, dropViewEntry.getKey());
            } else if (_dragListener.hoverView != null) {
                _dragListener.onHoverExit(event, _dragListener.hoverView);
                _dragListener.hoverView = null;
            }
            return false;
        }

        if (!event.consumed && action == Action.MOVE) {
            ApplicationClient.uiEventManager.onMouseMove(x, y);
            return false;
        }

        if (!event.consumed && action == Action.PRESSED && ApplicationClient.uiEventManager.has(x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.PRESSED && button == MouseButton.LEFT) {
            _dragListener = ApplicationClient.uiEventManager.drag(event, x, y);
            if (_dragListener != null) {
                return true;
            }
        }

        // On drag drop
        if (!event.consumed && action == Action.RELEASED && button == MouseButton.LEFT && _dragListener != null) {
            Map.Entry<View, Object> dropViewEntry = ApplicationClient.uiEventManager.getDropViews().entrySet().stream()
                    .filter(entry -> entry.getKey().contains(event.mouseEvent.x, event.mouseEvent.y))
                    .findAny().orElse(null);
            if (dropViewEntry != null) {
                _dragListener.onHoverExit(event, dropViewEntry.getKey());
                _dragListener.onDrop(event, dropViewEntry.getKey());
            }
            _dragListener = null;
            return true;
        }

        // Cleat UiEventManager selection listener when right button is clicked
        if (action == Action.RELEASED && button == MouseButton.RIGHT && ApplicationClient.uiEventManager.getSelectionListener() != null) {
            ApplicationClient.uiEventManager.setSelectionListener(null);
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.LEFT && ApplicationClient.uiEventManager.click(event, x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.RIGHT && ApplicationClient.uiEventManager.rightClick(event, x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.WHEEL_UP && ApplicationClient.uiEventManager.mouseWheelUp(event, x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.WHEEL_DOWN && ApplicationClient.uiEventManager.mouseWheelDown(event, x, y)) {
            return true;
        }

//        if (action == Action.RELEASED && button == MouseButton.LEFT) {
//            if (ApplicationClient.uiEventManager.rightClick(x, y)) {
//                return true;
//            } else {
//                Application.notify(observer -> observer.onKeyPress(Key.ESCAPE));
//                return false;
//            }
//        }

        return false;
    }

    public void onWindowEvent(Action action) {
    }

//    public void    onMouseWheel(int delta, int x, int y) {
//        _viewport.setScale(delta, x, y);
//    }

    public void onRefresh(int frame) {
        _update = frame;

//        // Collect views
//        ApplicationClient.uiEventManager.removeListeners(
//                ApplicationClient.uiEventManager.getClickListeners().keySet().stream()
//                        .filter(view -> !_rootViews.contains(view.getRootView()))
//                        .collect(Collectors.toList()));

    }

    // TODO
    public void draw(GDXRenderer renderer, boolean gameRunning) {
        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;

        _rootViews.stream()
                .filter(view -> view.isVisible() && (gameRunning || !view.inGame()) && (view.getModule() == null || view.getModule().isLoaded()))
                .forEach(view -> view.draw(renderer, 0, 0));
        _dropsDowns.forEach(view -> view.drawDropDown(renderer, 0, 0));
    }

    private void openContextMenu(ContextEntry[] entries, int x, int y) {
        _context.setVisible(true);
        _context.removeAllViews();
        _context.setPosition(x + 16, y + 16);
        _context.setBackgroundColor(Color.BLUE);

        int index = 0;
        for (ContextEntry entry: entries) {
            UILabel lbEntry = new UILabel(null);
            lbEntry.setSize(100, 20);
            lbEntry.setTextSize(14);
            lbEntry.setText(entry.label);
            lbEntry.setOnClickListener(entry.listener);
            lbEntry.setTextAlign(View.Align.CENTER_VERTICAL);
            lbEntry.setPosition(4, index++ * 20);
            _context.addView(lbEntry);
        }

        _context.setSize(100, index * 20);
    }

    public View findById(String id) {
        if (id.contains(" ")) {
            String[] ids = id.split(" ");
            if (ids.length >= 1) {
                View view = findById(ids[0]);
                for (int i = 1; i < ids.length; i++) {
                    if (view != null) {
                        view = view.findById(ids[i]);
                    }
                }
                return view;
            }
        }

        else {
            int resId = id.hashCode();
            for (RootView view : _rootViews) {
//                if (view.getId() == resId) {
//                    return view;
//                }
                View v = view.getView().findById(resId);
                if (v != null) {
                    return v;
                }
            }
        }

        return null;
    }

    public boolean isVisible(String id) {
        View view = findById(id);
        return view != null && view.isVisible();
    }
}
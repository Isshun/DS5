package org.smallbox.faraway.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.views.widgets.UIDropDown;
import org.smallbox.faraway.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static org.smallbox.faraway.core.engine.GameEventListener.*;

public class UIManager {

    public void addRootView(View view) {
        _rootViews.add(view);
    }

    public void addView(View view) {
        _views.add(view);

        String group = view.getGroup();
        if (group != null) {
            if (!_groups.containsKey(group)) {
                _groups.put(group, new ArrayList<>());
            }
            _groups.get(group).add(view);
        }
    }

    public void clearViews() {
        _rootViews.clear();
    }

    public void addDropsDowns(UIDropDown view) {
        _dropsDowns.add(view);
    }

    public Collection<View> getRootViews() {
        return _rootViews;
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

    private static class ContextEntry {
        public String                   label;
        public OnClickListener          listener;

        public ContextEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private long                        _lastLeftClick;
    private int                         _update;
    private UIFrame                     _context;
    private PriorityBlockingQueue<View> _rootViews = new PriorityBlockingQueue<>(200, new Comparator<View>() {
        @Override
        public int compare(View v1, View v2) {
            return v1.getLayer() - v2.getLayer();
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    });
    private Queue<View>                 _views = new ConcurrentLinkedQueue<>();
    private Queue<UIDropDown>           _dropsDowns = new ConcurrentLinkedQueue<>();
    private Queue<Integer>              _visibleViews = new ConcurrentLinkedQueue<>();
    private Map<String, List<View>>     _groups = new ConcurrentHashMap<>();

    public UIManager() {
        _context = new UIFrame(null);
        _context.setVisible(false);
    }

    public void reload() {
        _visibleViews.clear();
        _rootViews.stream()
                .filter(view -> view.getViews() != null)
                .forEach(view -> view.getViews().stream()
                        .filter(View::isVisible)
                        .forEach(subview -> _visibleViews.add(subview.getId())));
        _rootViews.clear();
        _dropsDowns.clear();
        Application.uiEventManager.clear();
    }

    public void restore() {
        _rootViews.stream()
                .filter(view -> view.getViews() != null)
                .forEach(view -> view.getViews().stream()
                        .filter(subview -> _visibleViews.contains(subview.getId()))
                        .forEach(subview -> subview.setVisible(true)));
    }

    public void onKeyEvent(Action action, Key key, Modifier modifier) {
        if (action == Action.RELEASED) {
            if (checkKeyboard(new GameEvent(key), key)) {
                return;
            }
        }
    }

    public boolean onMouseEvent(GameEvent event, Action action, MouseButton button, int x, int y, boolean rightPressed) {
        for (ModuleBase module: Application.moduleManager.getModules()) {
            if (!event.consumed && module.isLoaded() && module.onMouseEvent(action, button, x, y)) {
                return true;
            }
        }

        if (!event.consumed && action == Action.MOVE) {
            Application.uiEventManager.onMouseMove(x, y);
            return false;
        }

        if (!event.consumed && action == Action.PRESSED && Application.uiEventManager.has(x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.LEFT && Application.uiEventManager.click(event, x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.RIGHT && Application.uiEventManager.rightClick(event, x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.WHEEL_UP && Application.uiEventManager.mouseWheelUp(event, x, y)) {
            return true;
        }

        if (!event.consumed && action == Action.RELEASED && button == MouseButton.WHEEL_DOWN && Application.uiEventManager.mouseWheelDown(event, x, y)) {
            return true;
        }

//        if (action == Action.RELEASED && button == MouseButton.LEFT) {
//            if (Application.uiEventManager.rightClick(x, y)) {
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
//        Application.uiEventManager.removeListeners(
//                Application.uiEventManager.getClickListeners().keySet().stream()
//                        .filter(view -> !_rootViews.contains(view.getRootView()))
//                        .collect(Collectors.toList()));

        Application.notify(observer -> observer.onRefreshUI(frame));
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

    public boolean checkKeyboard(GameEvent event, Key key) {
        for (ModuleBase module: Application.moduleManager.getModules()) {
            if (module.isLoaded() && module.onKey(event, key)) {
                return true;
            }
        }

        return false;
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
            for (View view : _rootViews) {
                if (view.getId() == resId) {
                    return view;
                }
                View v = view.findById(resId);
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
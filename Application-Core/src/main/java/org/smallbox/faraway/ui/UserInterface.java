package org.smallbox.faraway.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static org.smallbox.faraway.core.engine.GameEventListener.*;

public class UserInterface {
    public void addView(View view) {
        _views.add(view);
//        Collections.sort(_views, (v1, v2) -> v1.getLevel() - v2.getLevel());
    }

    public void clearViews() {
        _views.clear();
    }

    public void addDropsDowns(UIDropDown view) {
        _dropsDowns.add(view);
    }

    public Collection<View> getViews() {
        return _views;
    }

    private static class ContextEntry {
        public String                   label;
        public OnClickListener          listener;

        public ContextEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private static UserInterface        _self;
    private long                        _lastLeftClick;
    private int                         _update;
    private UIFrame                     _context;
    private Collection<View>             _views = new LinkedBlockingQueue<>();
    private Collection<UIDropDown>       _dropsDowns = new LinkedBlockingQueue<>();
    private Collection<Integer>         _visibleViews = new LinkedBlockingQueue<>();

    public static UserInterface getInstance() {
        if (_self == null) {
            _self = new UserInterface();
        }
        return _self;
    }

    public UserInterface() {
        _context = new UIFrame(null);
        _context.setVisible(false);
    }

    public void reload() {
        _visibleViews.clear();
        _views.stream()
                .filter(view -> view.getViews() != null)
                .forEach(view -> view.getViews().stream()
                        .filter(View::isVisible)
                        .forEach(subview -> _visibleViews.add(subview.getId())));
        _views.clear();
        _dropsDowns.clear();
        UIEventManager.getInstance().clear();
    }

    public void restore() {
        _views.stream()
                .filter(view -> view.getViews() != null)
                .forEach(view -> view.getViews().stream()
                        .filter(subview -> _visibleViews.contains(subview.getId()))
                        .forEach(subview -> subview.setVisible(true)));
    }

    public void onKeyEvent(Action action, Key key, Modifier modifier) {
        if (action == Action.RELEASED) {
            if (checkKeyboard(key)) {
                return;
            }
        }
    }

    public boolean onMouseEvent(Action action, MouseButton button, int x, int y, boolean rightPressed) {
        for (ModuleBase module: ModuleManager.getInstance().getModules()) {
            if (module.isLoaded() && module.onMouseEvent(action, button, x, y)) {
                return true;
            }
        }

        if (action == Action.MOVE) {
            UIEventManager.getInstance().onMouseMove(x, y);
            return false;
        }

        if (action == Action.PRESSED && UIEventManager.getInstance().has(x, y)) {
            return true;
        }

        if (action == Action.RELEASED && button == MouseButton.LEFT && UIEventManager.getInstance().click(x, y)) {
            return true;
        }

        if (action == Action.RELEASED && button == MouseButton.RIGHT && UIEventManager.getInstance().rightClick(x, y)) {
            return true;
        }

        if (action == Action.RELEASED && button == MouseButton.WHEEL_UP && UIEventManager.getInstance().mouseWheelUp(x, y)) {
            return true;
        }

        if (action == Action.RELEASED && button == MouseButton.WHEEL_DOWN && UIEventManager.getInstance().mouseWheelDown(x, y)) {
            return true;
        }

//        if (action == Action.RELEASED && button == MouseButton.LEFT) {
//            if (UIEventManager.getInstance().rightClick(x, y)) {
//                return true;
//            } else {
//                Application.getInstance().notify(observer -> observer.onKeyPress(Key.ESCAPE));
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
//        UIEventManager.getInstance().removeListeners(
//                UIEventManager.getInstance().getClickListeners().keySet().stream()
//                        .filter(view -> !_views.contains(view.getRootView()))
//                        .collect(Collectors.toList()));

        Application.getInstance().notify(observer -> observer.onRefreshUI(frame));
    }

    public void draw(GDXRenderer renderer, boolean gameRunning) {
        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.5f;

        _views.stream()
                .filter(view -> view.isVisible() && (gameRunning || !view.inGame()) && (view.getModule() == null || view.getModule().isLoaded()))
                .forEach(view -> view.draw(renderer, 0, 0));
        _dropsDowns.forEach(view -> view.drawDropDown(renderer, 0, 0));
    }

    public boolean checkKeyboard(Key key) {
        for (ModuleBase module: ModuleManager.getInstance().getModules()) {
            if (module.isLoaded() && module.onKey(key)) {
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
            for (View view : _views) {
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
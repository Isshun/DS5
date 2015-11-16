package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.util.ArrayList;
import java.util.List;

import static org.smallbox.faraway.core.engine.GameEventListener.*;

public class UserInterface {
    private ArrayList<Integer> visibleViews;

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
    public List<View>                   _views = new ArrayList<>();

    public static UserInterface getInstance() {
        if (_self == null) {
            _self = new UserInterface();
        }
        return _self;
    }

    public UserInterface() {
        _context = new UIFrame();
        _context.setVisible(false);
    }

    // Used by lua modules
    public UILabel                  createLabel() { return new UILabel(); }
    public UIImage                  createImage() { return new UIImage(-1, -1); }
    public View                     createView() { return new UIFrame(-1, -1); }
    public UIGrid                   createGrid() { return new UIGrid(-1, -1); }
    public UIList                   createList() { return new UIList(-1, -1); }

    public void reload() {
        visibleViews = new ArrayList<>();
        _views.stream()
                .filter(view -> view.getViews() != null)
                .forEach(view -> view.getViews().stream()
                        .filter(View::isVisible)
                        .forEach(subview -> visibleViews.add(subview.getId())));
        _views.clear();
        UIEventManager.getInstance().clear();
    }

    public void restore() {
        _views.stream()
                .filter(view -> view.getViews() != null)
                .forEach(view -> view.getViews().stream()
                        .filter(subview -> visibleViews.contains(subview.getId()))
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
        for (GameModule module: ModuleManager.getInstance().getModules()) {
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

        if (action == Action.RELEASED && button == MouseButton.LEFT) {
            if (UIEventManager.getInstance().rightClick(x, y)) {
                return true;
            } else {
                Application.getInstance().notify(observer -> observer.onKeyPress(Key.ESCAPE));
                return false;
            }
        }

        return false;
    }

    public void onWindowEvent(Action action) {
    }

//    public void    onMouseWheel(int delta, int x, int y) {
//        _viewport.setScale(delta, x, y);
//    }

    public void onRefresh(int update) {
        _update = update;

        Application.getInstance().notify(GameObserver::onRefreshUI);
    }

    public void draw(GDXRenderer renderer, boolean gameRunning) {
        _views.stream().filter(view -> view.isVisible() && (gameRunning || !view.inGame()) && (view.getModule() == null || view.getModule().isLoaded())).forEach(view -> view.draw(renderer, 0, 0));
    }

    public boolean checkKeyboard(Key key) {
        for (GameModule module: ModuleManager.getInstance().getModules()) {
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
            UILabel lbEntry = new UILabel(100, 20);
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
        int resId = id.hashCode();
        for (View view: _views) {
            if (view.getId() == resId) {
                return view;
            }
            View v = view.findById(resId);
            if (v != null) {
                return v;
            }
        }
        return null;
    }
}
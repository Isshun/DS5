package org.smallbox.faraway.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.ClientLuaModuleManager;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.RootView;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIDropDown;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;

import static org.smallbox.faraway.core.engine.GameEventListener.Action;
import static org.smallbox.faraway.core.engine.GameEventListener.Modifier;

@ApplicationObject
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

    public Map<String, RootView> getMenuViews() {
        return _menuViews;
    }

    public Collection<View> getViews() {
        return _views;
    }

    public List<View> findByGroup(String group) {
        return _groups.get(group);
    }

    public void removeView(View view) {
        _views.remove(view);

        if (view.getParent() != null) {
            view.getParent().getViews().remove(view);
        }
    }

    public void addStyle(String id, LuaValue style) {
        _styles.put(id, style);
    }

    public LuaValue getStyle(String id) {
        return _styles.get(id);
    }

    public void addMenuView(RootView view) {
        _menuViews.put(view.getName(), view);
    }

    public void refresh(LuaController controller, String fileName) {
        DependencyInjector.getInstance().getDependency(UIManager.class).getMenuViews().remove(controller.getRootView().getName());
        DependencyInjector.getInstance().getDependency(UIManager.class).getRootViews().removeIf(rootView -> rootView.getView() == controller.getRootView());
        DependencyInjector.getInstance().getDependency(ClientLuaModuleManager.class).loadLuaFile(fileName);
        DependencyInjector.getInstance().getDependency(LuaControllerManager.class).initController(controller);
        Arrays.stream(controller.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(AfterApplicationLayerInit.class)).forEach(method -> {
            try {
                method.setAccessible(true);
                method.invoke(controller);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        controller.setVisible(true);
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

    private Map<String, RootView>       _menuViews = new ConcurrentHashMap<>();
    private Queue<RootView>             _rootViews = new LinkedBlockingQueue<>();
    private Map<View, String>           _subViews = new ConcurrentHashMap<>();
    private Set<View>                   _views = new ConcurrentSkipListSet<>();
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
     * @param action
     * @param button
     * @param x
     * @param y
     * @param rightPressed
     * @return L'evenement est consommé
     */
    public boolean onMouseEvent(Action action, int button, int x, int y, boolean rightPressed) {

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
            for (RootView view : _rootViews) {
                View v = view.getView().findById(id);
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
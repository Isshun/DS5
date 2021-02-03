package org.smallbox.faraway.client.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.apache.commons.collections4.CollectionUtils;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.client.lua.ClientLuaModuleManager;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.ui.event.OnClickListener;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.extra.Align;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.ui.widgets.UIDropDown;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.*;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.util.log.Log;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static org.smallbox.faraway.client.input.GameEventListener.Action;
import static org.smallbox.faraway.client.input.GameEventListener.Modifier;

@ApplicationObject
public class UIManager {
    @Inject private ClientLuaModuleManager clientLuaModuleManager;
    @Inject private LuaControllerManager luaControllerManager;
    @Inject private DependencyManager dependencyManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private GameManager gameManager;

    private final Map<String, LuaValue> _styles = new ConcurrentHashMap<>();
    private UIEventManager.OnDragListener _dragListener;

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

    public void clearViews() {
        uiEventManager.clear();

        _views.clear();
        _menuViews.clear();
        _rootViews.clear();
        _subViews.clear();
    }

    public void reloadViews() {
        luaControllerManager.getControllers().forEach(controller -> {
            Log.info("Reload lua: " + luaControllerManager.getFileName(controller.getClass().getCanonicalName()));
            clientLuaModuleManager.doLoadFile(new File(luaControllerManager.getFileName(controller.getClass().getCanonicalName())));
            luaControllerManager.initController(controller);
        });
    }

    private void reloadStyles() {
        clientLuaModuleManager.loadStyles();
    }

    public Map<String, LuaValue> getStyles() {
        return _styles;
    }

    public void refreshApplication() {
        dependencyManager.getSubTypesOf(LuaController.class).forEach(controller -> {
            callMethodAnnotatedBy(controller, OnInit.class);
            callMethodAnnotatedBy(controller, OnApplicationLayerInit.class);
            callMethodAnnotatedBy(controller, AfterApplicationLayerInit.class);
        });
    }

    public void refreshGame() {
        if (gameManager.isRunning()) {
            dependencyManager.getSubTypesOf(LuaController.class).forEach(controller -> {
                callMethodAnnotatedBy(controller, OnGameLayerInit.class);
                callMethodAnnotatedBy(controller, AfterGameLayerInit.class);
                callMethodAnnotatedBy(controller, OnGameStart.class);
            });
        }
    }

    @GameShortcut(key = Input.Keys.F1)
    public void refreshUI() {
        clearViews();
        reloadStyles();
        reloadViews();
        refreshApplication();
        refreshGame();
    }

    private void callMethodAnnotatedBy(LuaController controller, Class<? extends Annotation> cls) {
        Arrays.stream(controller.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(cls)).forEach(method -> {
            try {
                method.setAccessible(true);
                method.invoke(controller);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

//
//    public void refresh(LuaController controller) {
//        _menuViews.remove(controller.getRootView().getId());
//        _rootViews.removeIf(rootView -> rootView.getView() == controller.getRootView());
//        _subViews.keySet().stream().filter(view -> StringUtils.equals(view.getId(), controller.getRootView().getId())).findFirst().ifPresent(_subViews::remove);
//        clientLuaModuleManager.loadLuaFile(controller.getFileName());
//        luaControllerManager.initController(controller);
//        Arrays.stream(controller.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(AfterApplicationLayerInit.class)).forEach(method -> {
//            try {
//                method.setAccessible(true);
//                method.invoke(controller);
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        });
//        Arrays.stream(controller.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(AfterGameLayerInit.class)).forEach(method -> {
//            try {
//                method.setAccessible(true);
//                method.invoke(controller);
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        });
//        controller.setVisible(true);
//    }

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
    private final UIFrame                     _context;

    private final Map<String, RootView>       _menuViews = new ConcurrentHashMap<>();
    private final Queue<RootView>             _rootViews = new LinkedBlockingQueue<>();
    private final Map<View, String>           _subViews = new ConcurrentHashMap<>();
    private final Set<View>                   _views = new ConcurrentSkipListSet<>();
    private final Queue<UIDropDown>           _dropsDowns = new ConcurrentLinkedQueue<>();
    private final Queue<Integer>              _visibleViews = new ConcurrentLinkedQueue<>();
    private final Map<String, List<View>>     _groups = new ConcurrentHashMap<>();

    public String getSubViewParent(View subView) {
        return _subViews.get(subView);
    }

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
                    .filter(view -> parentName.equals(view.getId()))
                    .findFirst()
                    .flatMap(CompositeView::instanceOf)
                    .ifPresent(compositeView -> {
                        if (compositeView.getViews().stream().noneMatch(v -> subView == v)) {
                            compositeView.addView(subView);
                            subView.setParent(compositeView);
                        }
                    });
//            _rootViews.stream()
//                    .map(RootView::getView)
//                    .filter(view -> parentName.equals(view.getId()))
//                    .findAny()
//                    .flatMap(CompositeView::instanceOf)
//                    .ifPresent(compositeView -> {
//                        if (compositeView.getViews().stream().noneMatch(v -> subView == v)) {
//                            compositeView.addView(subView);
//                            subView.setParent(compositeView);
//                        }
//                    });
        });

        fixRootAndSubViews2();
    }

    private void fixRootAndSubViews2() {
//        _views.stream()
//                .filter(view -> view instanceof CompositeView)
//                .map(view -> (CompositeView) view)
//                .forEach(this::fixRootAndSubViews);

        _rootViews.stream()
                .map(RootView::getView)
                .forEach(this::fixRootAndSubViews);
    }

    private void fixRootAndSubViews(CompositeView compositeView) {
        List<View> subs = _subViews.entrySet().stream()
                .filter(entry -> entry.getValue().equals(compositeView.getId()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(subs)) {
//            compositeView.removeAllViews();
            subs.forEach(compositeView::addView);
        }
    }

    public void addSubView(View subViewToAdd, String parentName) {
        if (_subViews.keySet().stream().noneMatch(subView -> subView.getId().equals(subViewToAdd.getId()))) {
            _subViews.put(subViewToAdd, parentName);
        }

        fixRootAndSubViews();
    }

    public void addView(View view) {
        if (!CollectionUtils.containsAny(_views, view)) {
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
    public void draw(BaseRenderer renderer, boolean gameRunning) {
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
        _context.getStyle().setBackgroundColor(Color.BLUE);

        int index = 0;
        for (ContextEntry entry: entries) {
            UILabel lbEntry = new UILabel(null);
            lbEntry.setSize(100, 20);
            lbEntry.setTextSize(14);
            lbEntry.setText(entry.label);
            lbEntry.getEvents().setOnClickListener(entry.listener);
            lbEntry.setTextAlign(Align.CENTER_VERTICAL);
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
                    if (view instanceof CompositeView) {
                        view = ((CompositeView)view).find(ids[i]);
                    }
                }
                return view;
            }
        }

        else {
            for (RootView view : _rootViews) {
                if (view.getView() instanceof CompositeView) {
                    View v = view.getView().find(id);
                    if (v != null) {
                        return v;
                    }
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
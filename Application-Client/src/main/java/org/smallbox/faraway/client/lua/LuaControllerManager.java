package org.smallbox.faraway.client.lua;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.ObjectUtils;
import org.smallbox.faraway.client.GameClientObserver;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.OnGameLayerInit;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationObject
public class LuaControllerManager implements GameObserver {

    private final Map<String, View>         _viewByControllerName = new HashMap<>();
    private Map<String, LuaController>      _controllers;
    private long                            _lastUpdate;

    public void setControllerView(String controllerName, View view) { _viewByControllerName.put(controllerName, view); }

    public Map<String, LuaController> getControllers() {
        return _controllers;
    }

    @OnGameLayerInit
    public void onGameInitLayers() {

        // Get controllers from DI
        _controllers = DependencyInjector.getInstance().getSubTypesOf(LuaController.class).stream()
                .collect(Collectors.toConcurrentMap(LuaController::getCanonicalName, o -> o));

        // Bind rootView to controller
        _controllers.values().forEach(this::bindRootViewToController);

        // Bind sub controllers
        _controllers.values().forEach(this::bindControllerSubControllers);

        // Bind lua field
        _controllers.values().forEach(this::bindFieldsForController);

        // Bind action methods
        _controllers.values().forEach(this::bindMethodsForController);

        // Call OnReloadUI
        _controllers.values().forEach(GameClientObserver::onReloadUI);
    }

    @Override
    public void onGameUpdate(Game game) {
        if (System.currentTimeMillis() - _lastUpdate > 100) {
            _lastUpdate = System.currentTimeMillis();
            _controllers.forEach((clsName, controller) -> controller.controllerUpdate());
        }
    }

    private void bindRootViewToController(LuaController controller) {
        String controllerClassName = controller.getClass().getCanonicalName();
        View rootView = _viewByControllerName.get(controllerClassName);
        Objects.requireNonNull(rootView, "Unable to find rootView for controller " + controllerClassName);
        controller.setRootView(rootView);
    }

    /**
     * Bind controller fields
     *
     * @param controller Controller receiving binding
     */
    private void bindFieldsForController(LuaController controller) {
        Log.debug(LuaControllerManager.class, "Inject fields for controller: " + controller.getClass().getSimpleName());
        Arrays.stream(controller.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(BindLua.class))
                .forEach(field -> {
                    try {
                        Log.debug(LuaControllerManager.class, "Bind field: " + field.getName());
                        View view = ObjectUtils.firstNonNull(
                                controller.getRootView().findById(field.getName()),
                                controller.getRootView().findById(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()))
                        );

                        if (view != null) {
                            Log.debug(LuaControllerManager.class, "LuaController: Bind view %s", view.getName());
                            field.setAccessible(true);
                            field.set(controller, view);
                        } else {
                            throw new GameException(LuaControllerManager.class, "Unable to bind field: " + field.getName() + " in controller: " + controller.getClass().getName());
                        }

                    } catch (IllegalAccessException e) {
                        throw new GameException(LuaControllerManager.class, e);
                    }
                });
    }

    /**
     * Bind sub controllers
     *
     * @param controller Controller receiving binding
     */
    private void bindControllerSubControllers(LuaController controller) {
        for (Class cls = controller.getClass(); cls != null; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getAnnotation(BindLuaController.class) != null) {
                    field.setAccessible(true);
                    _controllers.entrySet().stream()
                            .filter(entry -> entry.getKey().equals(field.getType().getName()))
                            .map(Map.Entry::getValue)
                            .findAny()
                            .ifPresent(subController -> {
                                try {
                                    Log.debug(LuaControllerManager.class, "LuaController: Bind sub controller %s", subController.getClass().getSimpleName());
                                    field.set(controller, subController);
                                } catch (IllegalAccessException e) {
                                    throw new GameException(LuaControllerManager.class, e);
                                }
                            });
                }
            }
        }
    }

    /**
     * Bind controller methods
     *
     * @param controller Controller receiving binding
     */
    private void bindMethodsForController(LuaController controller) {
        for (Method method: controller.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(BindLuaAction.class) != null) {
                method.setAccessible(true);

                View view = controller.getRootView().findByAction(method.getName());
                if (view != null) {
                    Log.debug(LuaControllerManager.class, "LuaController: Bind method %s", method.getName());
                    view.setOnClickListener((int x, int y) -> {
                        try {
                            Log.debug(LuaControllerManager.class, "Method: %s", method.getName());
                            Log.debug(LuaControllerManager.class, "View: %s", view.getName());
                            method.invoke(controller, view);
                        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                            throw new GameException(LuaControllerManager.class, e);
                        }
                    });
                }
            }
        }
    }
}

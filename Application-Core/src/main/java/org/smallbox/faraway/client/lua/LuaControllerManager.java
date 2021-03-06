package org.smallbox.faraway.client.lua;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.ObjectUtils;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLayerBegin;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerBegin;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationObject
public class LuaControllerManager {
    @Inject private DependencyManager dependencyManager;

    private final Map<String, CompositeView> _viewByControllerName = new HashMap<>();
    private final Map<String, String> fileNameByControllerName = new HashMap<>();
    private Map<String, LuaController> _controllers;
    private long _lastUpdate;

    public void setControllerView(String controllerName, CompositeView view, String fileName) {
        _viewByControllerName.put(controllerName, view);
        fileNameByControllerName.put(controllerName, fileName);
    }

    public String getFileName(String controllerName) {
        return fileNameByControllerName.get(controllerName);
    }

    public CompositeView getView(String controllerName) {
        return _viewByControllerName.get(controllerName);
    }

    @OnGameLayerBegin
    @OnApplicationLayerBegin
    public void onGameInitLayers() {
        _controllers = dependencyManager.getSubTypesOf(LuaController.class).stream().collect(Collectors.toConcurrentMap(LuaController::getCanonicalName, o -> o));
        _controllers.values().forEach(this::initController);
    }

    public Collection<LuaController> getControllers() {
        return _controllers.values();
    }

    public void initController(LuaController controller) {
        // Bind rootView to controller
        bindRootViewToController(controller);

        // Bind sub controllers
        bindControllerSubControllers(controller);

        // Bind lua field
        bindFieldsForController(controller);

        // Bind action methods
        bindMethodsForController(controller);
    }

    private void bindRootViewToController(LuaController controller) {
        String controllerClassName = controller.getClass().getCanonicalName();
        CompositeView rootView = _viewByControllerName.get(controllerClassName);
        if (rootView != null) {
            controller.setRootView(rootView);
        } else {
            Log.warning("Unable to find rootView for controller " + controllerClassName);
        }
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
                        CompositeView rootView = controller.getRootView();
                        if (rootView != null) {
                            View view = ObjectUtils.firstNonNull(
                                    controller.getRootView().find(field.getName()),
                                    controller.getRootView().find(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()))
                            );

                            if (view != null) {
                                Log.debug(LuaControllerManager.class, "LuaController: Bind view %s", view.getId());
                                field.setAccessible(true);
                                field.set(controller, view);
                            } else {
                                Log.warning("Unable to bind field: " + field.getName() + " in controller: " + controller.getClass().getName());
                            }
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
        if (controller.getRootView() != null) {
            for (Method method : controller.getClass().getDeclaredMethods()) {
                if (method.getAnnotation(BindLuaAction.class) != null) {
                    method.setAccessible(true);

                    View view = controller.getRootView().findByAction(method.getName());
                    if (view != null) {
                        Log.debug(LuaControllerManager.class, "LuaController: Bind method %s", method.getName());
                        view.getEvents().setOnClickListener(() -> {
                            try {
                                Log.debug(LuaControllerManager.class, "Method: %s", method.getName());
                                Log.debug(LuaControllerManager.class, "View: %s", view.getId());
                                if (method.getParameterCount() == 0) {
                                    method.invoke(controller);
                                }
                                if (method.getParameterCount() == 1) {
                                    method.invoke(controller, view);
                                }
                            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                                throw new GameException(LuaControllerManager.class, e);
                            }
                        });
                    }
                }
            }
        }
    }

}

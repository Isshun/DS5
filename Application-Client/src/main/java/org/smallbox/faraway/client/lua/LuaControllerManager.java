package org.smallbox.faraway.client.lua;

import com.google.common.base.CaseFormat;
import org.reflections.Reflections;
import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaAction;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alex on 26/04/2016.
 */
public class LuaControllerManager implements GameObserver {

    private Map<String, LuaController>      _controllers = new HashMap<>();
    private Map<String, View>               _viewByControllerName = new HashMap<>();

    public void setControllerView(String controllerName, View view) { _viewByControllerName.put(controllerName, view); }
    public Map<String, LuaController> getControllers() { return _controllers; }

    @Override
    public void onGameStart(Game game) {
        ApplicationClient.luaModuleManager.init();

        // Invoke controllers
        _controllers = new Reflections("org.smallbox.faraway").getSubTypesOf(LuaController.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .collect(Collectors.toMap(Class::getCanonicalName, this::invokeController));

        // Bind sub controllers
        _controllers.values().forEach(this::bindControllerSubControllers);

        // Bind lua field and action methods
        _controllers.values().stream()
                .filter(controller -> controller.getRootView() != null)
                .forEach(controller -> {
                    bindControllerFields(controller, controller.getRootView());
                    bindControllerMethods(controller, controller.getRootView());
                });

        // Bind game observers to controllers
        _controllers.values().forEach(Application::addObserver);

        // Register to DependencyInjector
        _controllers.values().forEach(Application.dependencyInjector::register);

        Application.notify(GameObserver::onReloadUI);
    }

    @Override
    public void onInjectDependency(Object object) {
        for (Field field: object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BindLuaController.class)) {
                LuaController controller = _controllers.entrySet().stream()
                        .filter(entry -> entry.getValue().getClass() == field.getType())
                        .map(Map.Entry::getValue)
                        .findAny()
                        .orElse(null);
                if (controller != null) {
                    try {
                        field.setAccessible(true);
                        field.set(object, controller);
                    } catch (IllegalAccessException e) {
                        Log.error(e);
                    }
                } else {
                    Log.error("DependencyInjector: cannot find controller: " + field.getType());
                }
            }
        }
    }

    /**
     * Invoke controllers
     *
     * @param cls Controller to invoke
     */
    private LuaController invokeController(Class<? extends LuaController> cls) {
        Log.info("LuaController: Invoke controller %s", cls.getName());
        try {
            Constructor constructor = cls.getConstructor();
            constructor.setAccessible(true);
            LuaController controller = (LuaController) constructor.newInstance();
            controller.setRootView(_viewByControllerName.get(cls.getCanonicalName()));
            return controller;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Bind controller fields
     *
     * @param controller Controller receiving binding
     * @param rootView Root view
     */
    private void bindControllerFields(LuaController controller, View rootView) {
        for (Field field : controller.getClass().getDeclaredFields()) {
            if (field.getAnnotation(BindLua.class) != null) {
                field.setAccessible(true);
                try {
                    // Bind by field name
                    {
                        View view = rootView.findById(field.getName());
                        if (view != null) {
                            Log.info("LuaController: Bind view %s", view.getName());
                            field.set(controller, view);
                        }
                    }
                    // Bind by lower underscore converted field name
                    {
                        View view = rootView.findById(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
                        if (view != null) {
                            Log.info("LuaController: Bind view %s", view.getName());
                            field.set(controller, view);
                        }
                    }
                    if (field.get(controller) == null) {
                        Log.error("Unable to bind field: " + field.getName() + " in controller: " + controller.getClass().getName());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Bind sub controllers
     *
     * @param controller Controller receiving binding
     */
    private void bindControllerSubControllers(LuaController controller) {
        String packageName = controller.getClass().getPackage().getName();

        for (Field field : controller.getClass().getDeclaredFields()) {
            if (field.getAnnotation(BindLuaController.class) != null) {
                field.setAccessible(true);
                _controllers.entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith(packageName) && entry.getKey().endsWith(field.getType().getName()))
                        .map(Map.Entry::getValue)
                        .findAny()
                        .ifPresent(subController -> {
                            try {
                                Log.info("LuaController: Bind sub controller %s", subController.getClass().getName());
                                field.set(controller, subController);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
            }
        }
    }

    /**
     * Bind controller methods
     *
     * @param controller Controller receiving binding
     * @param rootView Root view
     */
    private void bindControllerMethods(LuaController controller, View rootView) {
        for (Method method: controller.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(BindLuaAction.class) != null) {
                method.setAccessible(true);

                View view = rootView.findByAction(method.getName());
                if (view != null) {
                    Log.info("LuaController: Bind method %s", method.getName());
                    view.setOnClickListener((GameEvent event) -> {
                        try {
                            Log.info("Method: %s", method.getName());
                            Log.info("View: %s", view.getName());
                            method.invoke(controller, view);
                        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                            Log.error(e);
                        }
                    });
                }
            }
        }
    }
}

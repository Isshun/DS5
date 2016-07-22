package org.smallbox.faraway.core;

import com.google.common.base.CaseFormat;
import org.reflections.Reflections;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.BindLuaAction;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Alex on 26/04/2016.
 */
public class LuaControllerManager {
    private static LuaControllerManager     _self;

    private Map<String, LuaController>      _controllers = new HashMap<>();
    private Map<String, View>               _viewByControllerName = new HashMap<>();

    public static LuaControllerManager getInstance() {
        if (_self == null) {
            _self = new LuaControllerManager();
        }
        return _self;
    }

    public void setControllerView(String controllerName, View view) { _viewByControllerName.put(controllerName, view); }

    /**
     * Inject controllers to modules
     */
    public void init() {
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

        // Bind controller to module
        ModuleManager.getInstance().getModules().forEach(this::bindControllerToModule);
    }

    public void create() {
        _controllers.values().forEach(LuaController::create);
    }

    public void gameStart() {
        _controllers.values().forEach(LuaController::gameStart);
    }

    /**
     * Bind controllers to modules
     *
     * @param module Module receiving binding
     */
    private void bindControllerToModule(ModuleBase module) {
        for (Field field: module.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BindLuaController.class)) {
                _controllers.entrySet().stream()
                        .filter(entry -> entry.getValue().getClass() == field.getType())
                        .map(Map.Entry::getValue)
                        .findAny()
                        .ifPresent(controller -> {
                            try {
                                field.setAccessible(true);
                                field.set(module, controller);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
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
                    view.setOnClickListener(() -> {
                        try {
                            method.invoke(controller);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }
}

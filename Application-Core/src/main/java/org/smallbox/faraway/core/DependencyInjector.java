package org.smallbox.faraway.core;

import org.smallbox.faraway.BindManager;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.BindLuaController;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.util.Log;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Alex on 24/07/2016.
 */
public class DependencyInjector {
    private Set<Object> _objectPool = new HashSet<>();
    private static DependencyInjector _self;
    private boolean _init = false;

    public <T> T create(Class<T> cls) {
        try {
            T object = cls.newInstance();
            register(object);
            return object;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void register(Object targetObject) {
        _objectPool.add(targetObject);
        if (_init) {
            injectDependencies(targetObject);
        }
    }

    public void injectDependencies() {
        if (_init) {
            Log.error("injectDependencies should be called only once");
        }
        _init = true;
        _objectPool.forEach(this::injectDependencies);
    }

    private void injectDependencies(Object object) {
        Log.info("Inject dependency to: " + object.getClass().getName());
        injectManagers(object);
        injectModules(object, ModuleManager.getInstance().getGameModules());
        injectControllers(object, LuaControllerManager.getInstance().getControllers());
    }

    private void injectManagers(Object targetObject) {
        for (Field field: targetObject.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                BindManager bindManager = field.getAnnotation(BindManager.class);
                if (bindManager != null) {
                    Log.debug(String.format("Try to inject %s to %s", field.getType().getSimpleName(), targetObject.getClass().getSimpleName()));
                    boolean hasBeenFound = false;
                    for (Object object: _objectPool) {
                        if (field.getType() == object.getClass()) {
                            field.set(targetObject, object);
                            hasBeenFound = true;
                        }
                    }
                    if (!hasBeenFound) {
                        Log.error("DependencyInjector: cannot find module: " + field.getType());
                    }
                }
            } catch (IllegalAccessException e) {
                Log.error(e);
            }
        }
    }

    private void injectControllers(Object object, Map<String, LuaController> controllers) {
        for (Field field: object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(BindLuaController.class)) {
                LuaController controller = controllers.entrySet().stream()
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

    private void injectModules(Object object, List<? extends ModuleBase> loadedModules) {
        for (Field field: object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                BindModule bindModule = field.getAnnotation(BindModule.class);
                if (bindModule != null) {
                    Log.debug(String.format("Try to inject %s to %s", field.getType().getSimpleName(), object.getClass().getSimpleName()));
                    ModuleBase module = getModuleDependency(loadedModules, field.getType());
                    if (module != null) {
                        field.set(object, module);
                    } else {
                        Log.error("DependencyInjector: cannot find module: " + field.getType());
                    }
                }
            } catch (IllegalAccessException e) {
                Log.error(e);
            }
        }
    }

    private ModuleBase getModuleDependency(List<? extends ModuleBase> loadedModules, Class cls) {
        for (ModuleBase module: loadedModules) {
            if (cls.isInstance(module)) {
                return module;
            }
        }
        return null;
    }
}

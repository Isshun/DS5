package org.smallbox.faraway.core.dependencyInjector;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.handler.ComponentHandler;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Alex on 24/07/2016.
 */
// TODO: injection des field sur les superclass
public class DependencyInjector {
    private final Map<Class<?>, ComponentHandler> _handlers;
    private Set<Object> _objectPool = new HashSet<>();
    private boolean _init = false;
    private static final DependencyInjector _self = new DependencyInjector();

    public static DependencyInjector getInstance() { return _self; }

    public DependencyInjector() {
        // Find and create handlers
        _handlers = new Reflections("org.smallbox.faraway").getSubTypesOf(ComponentHandler.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .collect(Collectors.toMap(cls -> cls, cls -> {
                    try {
                        return (ComponentHandler) cls.newInstance();
                    } catch ( IllegalAccessException | InstantiationException e) {
                        Log.error(e);
                        throw new RuntimeException(e);
                    }
                }));
    }

    public <T> T create(Class<T> cls) {
        try {
            T object = cls.newInstance();

            if (object instanceof GameObserver) {
                Application.addObserver((GameObserver)object);
            }

            // TODO: DI
//            if (object instanceof GameClientObserver) {
//                ApplicationClient.addObserver((GameClientObserver) object);
//            }

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
        injectModules(object, Application.moduleManager.getGameModules());

        Application.notify(observer -> observer.onInjectDependency(object));
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

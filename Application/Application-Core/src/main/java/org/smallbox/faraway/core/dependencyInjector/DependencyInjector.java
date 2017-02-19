package org.smallbox.faraway.core.dependencyInjector;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.handler.ComponentHandler;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
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
    private HashMap<Class<?>, Object> _models = new HashMap<>();
    private ShortcutBindingStrategyInterface _shortcutBindingStrategy;

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

            T object = _models.containsKey(cls) ? (T) _models.get(cls) : cls.newInstance();

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
        injectShortcut(object);

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

    // TODO: methode appelée plusieurs fois (2)
    private void injectShortcut(Object object) {
        for (Method method: object.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            GameShortcut gameShortcut = method.getAnnotation(GameShortcut.class);
            if (gameShortcut != null) {
                Log.debug(String.format("Try to inject %s to %s", method.getName(), object.getClass().getSimpleName()));
                _shortcutBindingStrategy.onShortcutBindingStrategy(gameShortcut.key(), () -> {
                    try {
                        method.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
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

    public void registerModel(Object model) {
        _models.put(model.getClass(), model);
    }

    public interface ShortcutBindingStrategyInterface {
        void onShortcutBindingStrategy(GameEventListener.Key key, Runnable runnable);
    }

    public void setShortcutBindingStrategy(ShortcutBindingStrategyInterface shortcutBindingStrategy) {
        _shortcutBindingStrategy = shortcutBindingStrategy;
    }

    public interface RegisterModelCallback<T> {
        T getModel() throws IOException;
    }

    public <T> void registerModel(Class<T> cls, RegisterModelCallback callback) {
        try {
            _models.put(cls, callback.getModel());
        } catch (IOException e) {
            Log.error(e);
        }
    }
}

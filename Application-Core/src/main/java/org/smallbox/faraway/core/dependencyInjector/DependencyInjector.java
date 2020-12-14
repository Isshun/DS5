package org.smallbox.faraway.core.dependencyInjector;

import org.apache.commons.lang3.ObjectUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static com.badlogic.gdx.utils.JsonValue.ValueType.object;

/**
 * Created by Alex on 24/07/2016.
 */
// TODO: injection des field sur les superclass
@SuppressWarnings("Duplicates")
public class DependencyInjector {
    private final Collection<Object> _gameShortcut = new LinkedBlockingQueue<>();
    private Set<Object> _objectPool = new HashSet<>();
    private Map<Class, Object> _objectPoolByClass = new ConcurrentHashMap<>();
    private Set<Object> _gameObjectPool = new HashSet<>();
    private Map<Class, Object> _gameObjectPoolByClass = new ConcurrentHashMap<>();
    private boolean _init = false;
    private boolean _initGame = false;
    private static final DependencyInjector _self = new DependencyInjector();
    private HashMap<Class<?>, Object> _models = new HashMap<>();
    private ApplicationClientInterface _clientInterface;

    public Collection<Object> getObjects() { return _objectPool; }
    public Collection<Object> getGameObjects() { return _gameObjectPool; }

    public <T> T getObject(Class<T> cls) {
        T applicationObject = (T) _objectPoolByClass.get(cls);
        if (applicationObject != null) {
            return applicationObject;
        }
        return (T) _gameObjectPoolByClass.get(cls);
    }

    public static DependencyInjector getInstance() { return _self; }

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
            throw new GameException(DependencyInjector.class, "Cannot create dependency: " + cls.getSimpleName());
        }
    }

    /**
     * Register object to dependency injector
     *
     * @param component to register
     */
    public void register(Object component) {

        // Register game object
        if (component.getClass().isAnnotationPresent(GameObject.class)) {
            assert !_gameObjectPoolByClass.containsKey(component.getClass()) : component.getClass() + " already register in DI";

            if (_initGame) {
                throw new RuntimeException("Cannot call register after DI init except for game scope objects: " + component.getClass());
            }

            _gameObjectPool.add(component);
            _gameObjectPoolByClass.put(component.getClass(), component);

            return;
        }

        // Register application object
        if (component.getClass().isAnnotationPresent(ApplicationObject.class)) {
            assert !_objectPoolByClass.containsKey(component.getClass()) : component.getClass() + " already register in DI";

            if (_init) {
                throw new RuntimeException("Cannot call register after DI init except for game scope objects: " + component.getClass());
            }

            _objectPool.add(component);
            _objectPoolByClass.put(component.getClass(), component);

            return;
        }

//        throw new RuntimeException("Cannot call register for class: " + component.getClass());
        Log.warning("Cannot call register for class: " + component.getClass());
    }

    /**
     * injectDependencies
     */
    public void injectDependencies() {

        if (_init) {
            throw new RuntimeException("Cannot call injectDependencies after DI init");
        }

        _init = true;

        _objectPool.forEach(host -> {
            Log.verbose("Inject dependency to: " + host.getClass().getName());
            doInjectComponents(host);
            doInjectConfig(host);
            doInjectShortcut(host);
            doInjectInject(host);
            Application.notify(observer -> observer.onInjectDependency(object));
        });
    }

    /**
     * injectGameDependencies
     */
    public void injectGameDependencies() {

        if (_initGame) {
            throw new RuntimeException("Cannot call injectGameDependencies after DI init game");
        }

        _initGame = true;

        List<Object> objects = new ArrayList<>();
        objects.addAll(_objectPool);
        objects.addAll(_gameObjectPool);
        objects.forEach(host -> {
            Log.verbose("Inject dependency to: " + host.getClass().getName());
            doInjectGame(host);
            doInjectConfig(host);
            doInjectShortcut(host);
            doInjectInject(host);
            Application.notify(observer -> observer.onInjectDependency(object));
        });
    }

    private void doInjectGame(Object host) {
        for (Field field: host.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(BindComponent.class)) {
                    Log.verbose(String.format("Inject [%s] to game object [%s]", field.getType().getSimpleName(), host.getClass().getSimpleName()));
                    for (Object object: _gameObjectPool) {
                        if (field.getType().isInstance(object)) {
                            field.set(host, object);
                        }
                    }
                    for (Object object: _objectPool) {
                        if (field.getType().isInstance(object)) {
                            field.set(host, object);
                        }
                    }
                    if (field.get(host) == null) {
                        throw new GameException(DependencyInjector.class, "DependencyInjector: cannot inject type: " + field.getType());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }
    }

    private void doInjectConfig(Object host) {
        for (Field field: host.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(BindConfig.class) && field.get(host) == null) {
                    field.set(host, field.getType().newInstance());
                }
            } catch (IllegalAccessException | InstantiationException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }
    }

    // TODO: methode appelée plusieurs fois (2)
    private void doInjectShortcut(Object host) {
        if (_clientInterface != null) {
            if (!_gameShortcut.contains(host)) {
                _gameShortcut.add(host);
                for (Method method : host.getClass().getDeclaredMethods()) {
                    method.setAccessible(true);
                    GameShortcut gameShortcut = method.getAnnotation(GameShortcut.class);
                    if (gameShortcut != null) {

                        Log.verbose(String.format("Try to inject %s to %s", method.getName(), host.getClass().getSimpleName()));
                        _clientInterface.onShortcutBinding(host.getClass().getName() + "." + method.getName(), gameShortcut.key(), () -> {
                            try {
                                method.invoke(host);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        }
    }

    // TODO: replace inject components / config by this method and rename it
    private void doInjectInject(Object host) {
        for (Field field: host.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Inject.class)) {
                    Log.verbose(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));
                    Objects.requireNonNull(_objectPoolByClass.get(field.getType()), "Unable to find field to inject: " + field.getType().getName());
                    field.set(host, _objectPoolByClass.get(field.getType()));
                }
            } catch (IllegalAccessException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }
    }

    private void doInjectComponents(Object host) {
        Set<Class> missing = new HashSet<>();

        for (Field field: host.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                BindComponent bindComponent = field.getAnnotation(BindComponent.class);
                if (bindComponent != null) {
                    Log.verbose(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));
                    for (Object object: _objectPool) {
                        if (field.getType().isInstance(object)) {
                            field.set(host, object);
                        }
                    }
                    if (field.get(host) == null) {
                        missing.add(field.getType());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }

        if (!missing.isEmpty()) {
            missing.forEach(cls -> Log.warning("Missing dependency: " + cls.getName()));
//            throw new GameException(DependencyInjector.class, "DependencyInjector: missing dependencies");
        }
    }

//    private void doInjectModules(Object host) {
//        for (Field field: host.getClass().getDeclaredFields()) {
//            try {
//                field.setAccessible(true);
//                BindModule bindModule = field.getAnnotation(BindModule.class);
//                if (bindModule != null) {
//                    Log.verbose(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));
//
//                    ModuleBase gameModule = null;
//                    if (Application.gameManager != null && Application.gameManager.getGame() != null) {
//                        gameModule = getModuleDependency(Application.gameManager.getGame().getModules(), field.getType());
//                    }
//
//                    ModuleBase applicationModule = getModuleDependency(Application.moduleManager.getApplicationModules(), field.getType());
//                    if (gameModule != null) {
//                        field.set(host, gameModule);
//                    } else if (applicationModule != null) {
//                        field.set(host, applicationModule);
//                    } else {
////                        throw new GameException(DependencyInjector.class, "DependencyInjector: cannot find module", field.getType(), host.getClass().getSimpleName());
//                    }
//                }
//            } catch (IllegalAccessException e) {
//                throw new GameException(DependencyInjector.class, e);
//            }
//        }
//    }

    private ModuleBase getModuleDependency(Collection<? extends ModuleBase> loadedModules, Class cls) {
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

    public interface ApplicationClientInterface {
        void onShortcutBinding(String label, int key, Runnable runnable);
    }

    public void setClientInterface(ApplicationClientInterface clientInterface) {
        _clientInterface = clientInterface;
    }

    public interface RegisterModelCallback<T> {
        T getModel() throws IOException;
    }

    public <T> T registerModel(Class<T> cls, RegisterModelCallback<T> callback) {
        try {
            T model = callback.getModel();

            callInitMethod(model);

            _models.put(cls, model);
            return model;
        } catch (IOException e) {
            throw new GameException(DependencyInjector.class, e);
        }
    }

    private <T> void callInitMethod(T model) {

        for (Method method: model.getClass().getDeclaredMethods()) {
            try {
                method.setAccessible(true);
                if (method.isAnnotationPresent(OnInit.class)) {
                    method.invoke(model);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }

    }
}

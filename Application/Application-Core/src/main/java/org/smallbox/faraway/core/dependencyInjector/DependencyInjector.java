package org.smallbox.faraway.core.dependencyInjector;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import static com.badlogic.gdx.utils.JsonValue.ValueType.object;

/**
 * Created by Alex on 24/07/2016.
 */
// TODO: injection des field sur les superclass
public class DependencyInjector {
    private final Collection<Object> _gameShortcut = new LinkedBlockingQueue<>();
    private Set<Object> _objectPool = new HashSet<>();
    private boolean _init = false;
    private static final DependencyInjector _self = new DependencyInjector();
    private HashMap<Class<?>, Object> _models = new HashMap<>();
    private ApplicationClientInterface _clientInterface;

    public Collection<Object> getObjects() { return _objectPool; }

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

    public void register(Object component) {
        _objectPool.add(component);

        if (_init) {
//            throw new GameException(this.getClass(), "Cannot call register after init");
            injectDependencies(component);
        }
    }

    public void injectDependencies() {
        injectDependencies(null);
    }

    public void injectDependencies(Object component) {
//        if (!_init) {
        _init = true;

        if (component != null) {
            injectDependencies(component, null);
        }

        _objectPool.forEach(host -> injectDependencies(host, component));
//        } else {
//            throw new GameException("injectDependencies should be called only once");
//        }
    }

    private void injectDependencies(Object host, Object component) {
        Log.verbose("Inject dependency to: " + host.getClass().getName());
        injectComponents(host, component);
        injectConfig(host);
        injectModules(host);
        injectShortcut(host);

        Application.notify(observer -> observer.onInjectDependency(object));
    }

    private void injectConfig(Object host) {
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

    private void injectComponents(Object host, Object component) {
        for (Field field: host.getClass().getDeclaredFields()) {
            if (component == null || component.getClass() == field.getType()) {
                try {
                    field.setAccessible(true);
                    BindComponent bindComponent = field.getAnnotation(BindComponent.class);
                    if (bindComponent != null) {
                        Log.verbose(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));
                        boolean hasBeenFound = false;
                        for (Object object: _objectPool) {
                            if (field.getType() == object.getClass()) {
                                field.set(host, object);
                                hasBeenFound = true;
                            }
                        }
                        if (!hasBeenFound) {
                            throw new GameException(DependencyInjector.class, "DependencyInjector: cannot find module: " + field.getType());
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new GameException(DependencyInjector.class, e);
                }
            }
        }
    }

    // TODO: methode appelée plusieurs fois (2)
    private void injectShortcut(Object host) {
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

    private void injectModules(Object host) {
        for (Field field: host.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                BindModule bindModule = field.getAnnotation(BindModule.class);
                if (bindModule != null) {
                    Log.verbose(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));

                    ModuleBase gameModule = null;
                    if (Application.gameManager != null && Application.gameManager.getGame() != null) {
                        gameModule = getModuleDependency(Application.gameManager.getGame().getModules(), field.getType());
                    }

                    ModuleBase applicationModule = getModuleDependency(Application.moduleManager.getApplicationModules(), field.getType());
                    if (gameModule != null) {
                        field.set(host, gameModule);
                    } else if (applicationModule != null) {
                        field.set(host, applicationModule);
                    } else {
//                        throw new GameException(DependencyInjector.class, "DependencyInjector: cannot find module", field.getType(), host.getClass().getSimpleName());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }
    }

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
            _models.put(cls, model);
            return model;
        } catch (IOException e) {
            throw new GameException(DependencyInjector.class, e);
        }
    }
}

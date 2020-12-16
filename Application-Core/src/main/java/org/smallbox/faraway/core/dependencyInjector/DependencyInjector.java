package org.smallbox.faraway.core.dependencyInjector;

import org.apache.commons.lang3.ObjectUtils;
import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfigService;
import org.smallbox.faraway.util.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Alex on 24/07/2016.
 */
// TODO: injection des field sur les superclass
@SuppressWarnings("Duplicates")
public class DependencyInjector {
    private final Collection<Object> _gameShortcut = new LinkedBlockingQueue<>();
    private Map<Class, DependencyInfo> _objectPoolByClass = new ConcurrentHashMap<>();
    private Map<Class, DependencyInfo> _gameObjectPoolByClass = new ConcurrentHashMap<>();
    private boolean _init = false;
    private boolean _initGame = false;
    private static final DependencyInjector _self = new DependencyInjector();
    private HashMap<Class<?>, Object> _models = new HashMap<>();
    private ApplicationClientInterface _clientInterface;

    public <T> T getObject(Class<T> cls) {

        // Get application object
        DependencyInfo<T> applicationObject = (DependencyInfo<T>) _objectPoolByClass.get(cls);
        if (applicationObject != null) {
            return applicationObject.dependency;
        }

        // Get game object
        DependencyInfo<T> gameObject = (DependencyInfo<T>) _gameObjectPoolByClass.get(cls);
        if (gameObject != null) {
            return gameObject.dependency;
        }

        return null;
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
     * Some special object like ApplicationConfigService need to be created and initialized manually
     */
    public <T> T createAndInit(Class<T> cls) {
        T object = create(cls);
        callInitMethod(object, false);
        return object;
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

//            if (_initGame) {
//                throw new RuntimeException("Cannot call register after DI init except for game scope objects: " + component.getClass());
//            }

            _gameObjectPoolByClass.put(component.getClass(), new DependencyInfo(component));

            return;
        }

        // Register application object
        if (component.getClass().isAnnotationPresent(ApplicationObject.class)) {
            assert !_objectPoolByClass.containsKey(component.getClass()) : component.getClass() + " already register in DI";

//            if (_init) {
//                throw new RuntimeException("Cannot call register after DI init except for game scope objects: " + component.getClass());
//            }

            _objectPoolByClass.put(component.getClass(), new DependencyInfo(component));

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

        _objectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(host -> {
            Log.verbose("Inject dependency to: " + host.getClass().getName());
//            doInjectComponents(host);
            doInjectConfig(host);
            doInjectShortcut(host);
            doInjectInject(host, false);
            callInitMethod(host, false);
//            Application.notify(observer -> observer.onInjectDependency(object));
        });
    }

    /**
     * Inject GameObjects in ApplicationObjects and other GameObjects
     */
    public void injectGameDependencies() {

        if (_initGame) {
            throw new RuntimeException("Cannot call injectGameDependencies after DI init game");
        }

        _initGame = true;

        List<DependencyInfo> objects = new ArrayList<>();
        objects.addAll(_objectPoolByClass.values());
        objects.addAll(_gameObjectPoolByClass.values());
        objects.stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(host -> {
            Log.verbose("Inject dependency to: " + host.getClass().getName());
            doInjectConfig(host);
            doInjectShortcut(host);
            doInjectInject(host, true);
            callInitMethod(host, true);
//            Application.notify(observer -> observer.onInjectDependency(object));
        });
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
    private void doInjectInject(Object host, boolean gameExists) {
        for (Field field: host.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Inject.class)) {
                    Log.verbose(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));

                    //Objects.requireNonNull(_objectPoolByClass.get(field.getType()), "Unable to find field to inject: " + field.getType().getName());
                    DependencyInfo toInject = ObjectUtils.firstNonNull(_objectPoolByClass.get(field.getType()), _gameObjectPoolByClass.get(field.getType()));

                    if (field.getType().isAnnotationPresent(ApplicationObject.class) || gameExists) {
                        Objects.requireNonNull(toInject, "Try to inject null value for " + field.getType().getName() + " in " + host.getClass().getTypeName());
                    }

                    if (toInject != null) {
                        field.set(host, toInject.dependency);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new GameException(DependencyInjector.class, e);
            }
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

    public void createGameObjects() {
        new Reflections("org.smallbox").getTypesAnnotatedWith(GameObject.class).stream()
                .filter(cls -> !_gameObjectPoolByClass.containsKey(cls))
                .forEach(this::create);
    }

    public void destroyGameObjects() {
        _initGame = false;
        _gameObjectPoolByClass.clear();
    }

    public <T> Collection<T> getSubTypesOf(Class<T> baseClass) {
        List<T> objects = new ArrayList<>();
        _gameObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).filter(baseClass::isInstance).map(baseClass::cast).forEach(objects::add);
        _objectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).filter(baseClass::isInstance).map(baseClass::cast).forEach(objects::add);
        return objects;
    }

    public List<AbsGameModule> getGameModules() {
        return _gameObjectPoolByClass.values().stream()
                .map(dependencyInfo -> dependencyInfo.dependency)
                .filter(o -> o instanceof AbsGameModule)
                .map(o -> (AbsGameModule)o)
                .collect(Collectors.toList());
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

    private <T> void callInitMethod(T model, boolean gameExists) {
        callMethodAnnotatedBy(model, OnInit.class);
    }

    public void callMethodAnnotatedBy(Class<? extends Annotation> annotationClass) {
        _objectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass));
        _gameObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass));
    }

    public <T> void callMethodAnnotatedBy(T model, Class<? extends Annotation> annotationClass) {
        for (Method method: model.getClass().getDeclaredMethods()) {
            try {
                method.setAccessible(true);
                if (method.isAnnotationPresent(annotationClass)) {
                    method.invoke(model);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }
    }

}

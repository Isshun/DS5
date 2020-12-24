package org.smallbox.faraway.core.dependencyInjector;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class DependencyInjector {
    private static final DependencyInjector _self = new DependencyInjector();
    private final Collection<Object> _gameShortcut = new LinkedBlockingQueue<>();
    private final Map<Class<?>, DependencyInfo<?>> _objectPoolByClass = new ConcurrentHashMap<>();
    private final Map<Class<?>, DependencyInfo<?>> _gameObjectPoolByClass = new ConcurrentHashMap<>();
    private boolean _init = false;
    private boolean _initGame = false;
    private ApplicationClientInterface _clientInterface;

    public static DependencyInjector getInstance() { return _self; }

    /**
     * Automatically create object annotated with @ApplicationObject
     */
    public void findAndCreateApplicationObjects() {
        new Reflections("org.smallbox").getTypesAnnotatedWith(ApplicationObject.class).stream()
                .filter(cls -> getDependency(cls) == null)
                .forEach(cls -> create(cls));
    }

    /**
     * Return ApplicationObject or GameObject stored in DI for asked class, return null if none of them exists
     */
    public <T> T getDependency(Class<T> cls) {
        DependencyInfo<?> dependencyInfo = getDependencyInfo(cls);
        return Objects.nonNull(dependencyInfo) ? cls.cast(dependencyInfo.dependency) : null;
    }

    /**
     * Return DependencyInfo for ApplicationObject or GameObject stored in DI for asked class, return null if none of them exists
     */
    public <T> DependencyInfo<?> getDependencyInfo(Class<T> cls) {
        DependencyInfo<?> applicationObject = _objectPoolByClass.get(cls);
        DependencyInfo<?> gameObject = _gameObjectPoolByClass.get(cls);
        return Objects.nonNull(applicationObject) ? applicationObject : gameObject;
    }

    public <T> T create(Class<T> cls) {
        Log.info("Create application object: " + cls.getSimpleName());
        try {
            T object = getDependency(cls);
            if (Objects.isNull(object)) {
                object = cls.getConstructor().newInstance();
            }

            if (object instanceof GameObserver) {
                Application.addObserver((GameObserver)object);
            }

            register(object);
            return object;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new GameException(DependencyInjector.class, "Cannot create dependency: " + cls.getSimpleName() + "\n" + e.getMessage());
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

            _gameObjectPoolByClass.put(component.getClass(), new DependencyInfo<>(component));

            return;
        }

        // Register application object
        if (component.getClass().isAnnotationPresent(ApplicationObject.class)) {
            assert !_objectPoolByClass.containsKey(component.getClass()) : component.getClass() + " already register in DI";

//            if (_init) {
//                throw new RuntimeException("Cannot call register after DI init except for game scope objects: " + component.getClass());
//            }

            _objectPoolByClass.put(component.getClass(), new DependencyInfo<>(component));

            return;
        }

//        throw new RuntimeException("Cannot call register for class: " + component.getClass());
        Log.warning("Cannot call register for class: " + component.getClass());
    }

    /**
     * Inject dependencies on objects annotated with @ApplicationObject
     */
    public void injectApplicationDependencies() {

        if (_init) {
            throw new RuntimeException("Cannot call injectDependencies after DI init");
        }

        _init = true;

        _objectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(host -> {
            Log.verbose("Inject dependency to: " + host.getClass().getName());
            doInjectShortcut(host);
            doInjectDependency(host, false);
            callInitMethod(host, false);
        });
    }

    /**
     * Inject dependencies on objects annotated with @GameObjects
     */
    public void injectGameDependencies() {

        if (_initGame) {
            throw new RuntimeException("Cannot call injectGameDependencies after DI init game");
        }

        _initGame = true;

        List<DependencyInfo<?>> objects = new ArrayList<>();
        objects.addAll(_objectPoolByClass.values());
        objects.addAll(_gameObjectPoolByClass.values());
        objects.stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(host -> {
            Log.verbose("Inject dependency to: " + host.getClass().getName());
            doInjectShortcut(host);
            doInjectDependency(host, true);
            callInitMethod(host, true);
        });
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

    private void doInjectDependency(Object host, boolean gameExists) {
        for (Field field: host.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Inject.class)) {
                    Log.verbose(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));

                    // TODO: authorize injection of null objects ?
                    //Objects.requireNonNull(_objectPoolByClass.get(field.getType()), "Unable to find field to inject: " + field.getType().getName());
                    DependencyInfo<?> toInject = getDependencyInfo(field.getType());

                    if (field.getType().isAnnotationPresent(ApplicationObject.class) || gameExists) {
                        Objects.requireNonNull(toInject, "Try to inject null value for " + field.getType().getName() + " in " + host.getClass().getTypeName());
                    }

                    if (toInject != null) {
                        field.set(host, toInject.dependency);
                    } else {
                        Log.warning(field.getType().getSimpleName() + " is null");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new GameException(DependencyInjector.class, e);
            }
        }
    }

    /**
     * Search in classpath all objects annotated with @GameObject and create them
     * This method is aimed to be called during the game initialization, see GameManager
     */
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

    @Deprecated
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

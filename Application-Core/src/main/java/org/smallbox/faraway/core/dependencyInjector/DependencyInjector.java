package org.smallbox.faraway.core.dependencyInjector;

import org.reflections.Reflections;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.log.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DependencyInjector {
    private static final DependencyInjector _self = new DependencyInjector();
    private final Map<Class<?>, DependencyInfo<?>> _applicationObjectPoolByClass = new ConcurrentHashMap<>();
    private final Map<Class<?>, DependencyInfo<?>> _gameObjectPoolByClass = new ConcurrentHashMap<>();
    private boolean _init = false;
    private boolean _initGame = false;

    public static DependencyInjector getInstance() { return _self; }

    /**
     * Automatically create object annotated with @ApplicationObject
     */
    public void findAndCreateApplicationObjects() {
        new Reflections("org.smallbox").getTypesAnnotatedWith(ApplicationObject.class).stream()
                .filter(cls -> getDependency(cls) == null)
                .forEach(this::create);
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
        DependencyInfo<?> applicationObject = _applicationObjectPoolByClass.get(cls);
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
            assert !_applicationObjectPoolByClass.containsKey(component.getClass()) : component.getClass() + " already register in DI";

//            if (_init) {
//                throw new RuntimeException("Cannot call register after DI init except for game scope objects: " + component.getClass());
//            }

            _applicationObjectPoolByClass.put(component.getClass(), new DependencyInfo<>(component));

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

        _applicationObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(host -> {
            Log.debug("Inject dependency to: " + host.getClass().getSimpleName());
            doInjectShortcut(host);
            doInjectDependency(host, host.getClass(), false);
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
        objects.addAll(_applicationObjectPoolByClass.values());
        objects.addAll(_gameObjectPoolByClass.values());
        objects.stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(host -> {
            Log.info("Inject dependency to game object: " + host.getClass().getSimpleName());
            doInjectShortcut(host);
            doInjectDependency(host, host.getClass(), true);
            callInitMethod(host, true);
        });
    }

    private void doInjectShortcut(Object host) {
        for (Method method : host.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            GameShortcut gameShortcut = method.getAnnotation(GameShortcut.class);
            if (gameShortcut != null) {
                Log.debug(String.format("Try to inject %s to %s", method.getName(), host.getClass().getSimpleName()));
                getDependency(ShortcutManager.class).addBinding(host.getClass().getName() + "." + method.getName(), gameShortcut.key(), () -> {
                    try {
                        method.invoke(host);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    private void doInjectDependency(Object host, Class<?> cls, boolean gameExists) {
        for (Field field: cls.getDeclaredFields()) {

            if (cls.getSuperclass() != null) {
                doInjectDependency(host, cls.getSuperclass(), gameExists);
            }

            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Inject.class)) {
                    Log.debug(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));

                    // TODO: authorize injection of null objects ?
                    //Objects.requireNonNull(_objectPoolByClass.get(field.getType()), "Unable to find field to inject: " + field.getType().getName());
                    DependencyInfo<?> toInject = getDependencyInfo(field.getType());

                    if (field.getType().isAnnotationPresent(ApplicationObject.class) || gameExists) {
                        Objects.requireNonNull(toInject, "Try to inject null value for " + field.getType().getSimpleName() + " in " + host.getClass().getSimpleName());
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

        // For each ApplicationObject, set to null every field annotated with @Inject representing a GameObject
        _applicationObjectPoolByClass.values().forEach(dependencyInfo -> {
            for (Field field: dependencyInfo.dependency.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Inject.class) && field.getType().isAnnotationPresent(GameObject.class)) {
                        field.set(dependencyInfo.dependency, null);
                    }
                } catch (IllegalAccessException e) {
                    throw new GameException(DependencyInjector.class, e);
                }
            }
        });
    }

    public <T> Collection<T> getSubTypesOf(Class<T> baseClass) {
        List<T> objects = new ArrayList<>();
        _gameObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).filter(baseClass::isInstance).map(baseClass::cast).forEach(objects::add);
        _applicationObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).filter(baseClass::isInstance).map(baseClass::cast).forEach(objects::add);
        return objects;
    }

    public <T extends Annotation> Map<T, Consumer<ObjectModel>> getMethodsAnnotatedBy(Class<T> baseClass) {
        Map<T, Consumer<ObjectModel>> results = new ConcurrentHashMap<>();
        Stream.concat(
                _gameObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency),
                _applicationObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency)
        ).forEach(dependency ->
                Stream.of(dependency.getClass().getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(baseClass))
                        .forEach(method -> results.put(method.getAnnotation(baseClass), objectModel -> {
                            try {
                                method.setAccessible(true);
                                method.invoke(dependency, objectModel);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                Log.error(e);
                                e.printStackTrace();
                            }
                        }))
        );
        return results;
    }

    public Collection<DependencyInfo<?>> getGameDependencies() {
        return _gameObjectPoolByClass.values();
    }

    private <T> void callInitMethod(T model, boolean gameExists) {
        callMethodAnnotatedBy(model, OnInit.class);
    }

    public void callMethodAnnotatedBy(Class<? extends Annotation> annotationClass) {
        _applicationObjectPoolByClass.values().forEach(dependencyInfo -> callMethodAnnotatedBy(dependencyInfo.dependency, annotationClass));
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

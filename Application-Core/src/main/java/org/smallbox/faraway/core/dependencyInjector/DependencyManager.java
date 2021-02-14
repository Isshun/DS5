package org.smallbox.faraway.core.dependencyInjector;

import org.reflections.Reflections;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.shortcut.ShortcutManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnSettingsUpdate;
import org.smallbox.faraway.game.world.ObjectModel;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationObject
public class DependencyManager {
    private static final DependencyManager _self = new DependencyManager();
    final Map<Class<?>, DependencyInfo<?>> _applicationObjectPoolByClass = new ConcurrentHashMap<>();
    final Map<Class<?>, DependencyInfo<?>> _gameObjectPoolByClass = new ConcurrentHashMap<>();
    private boolean _init = false;
    private boolean _initGame = false;

    public static DependencyManager getInstance() {
        return _self;
    }

    private DependencyManager() {
        register(this);
        register(new DependencyNotifier());
    }

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

            register(object);
            return object;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new GameException(DependencyManager.class, "Cannot create dependency: " + cls.getSimpleName() + "\n" + e.getMessage());
        }
    }

    /**
     * Some special object like ApplicationConfigService need to be created and initialized manually
     */
    public <T> T createAndInit(Class<T> cls) {
        T object = create(cls);
        doInjectShortcut(object);
        doInjectDependency(object, cls, false);
        getDependency(DependencyNotifier.class).callInitMethod(object);
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
            doInjectDependency(host, host.getClass(), false);
            getDependency(DependencyNotifier.class).callInitMethod(host);
        });

        _applicationObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(this::doInjectShortcut);
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
            doInjectDependency(host, host.getClass(), true);
        });
        _gameObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(this::doInjectShortcut);
        _gameObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(host -> getDependency(DependencyNotifier.class).callInitMethod(host));
    }

    @OnSettingsUpdate
    private void injectShortcuts() {
        _applicationObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(this::doInjectShortcut);
        _gameObjectPoolByClass.values().stream().map(dependencyInfo -> dependencyInfo.dependency).forEach(this::doInjectShortcut);
    }

    private void doInjectShortcut(Object host) {
        for (Method method : host.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            GameShortcut gameShortcut = method.getAnnotation(GameShortcut.class);
            if (gameShortcut != null) {
                Log.debug(String.format("Try to inject %s to %s", method.getName(), host.getClass().getSimpleName()));
                getDependency(ShortcutManager.class).addBinding(host.getClass().getSimpleName() + "." + method.getName(), gameShortcut, () -> {
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
        for (Class<?> superClass = cls.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            doInjectDependency(host, superClass, gameExists);
        }

        for (Field field : cls.getDeclaredFields()) {

            try {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Inject.class)) {
                    Log.debug(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));

                    // TODO: authorize injection of null objects ?
                    //Objects.requireNonNull(_objectPoolByClass.get(field.getType()), "Unable to find field to inject: " + field.getType().getName());
                    DependencyInfo<?> toInject = getDependencyInfo(field.getType());

//                    if (field.getType().isAnnotationPresent(ApplicationObject.class) || gameExists) {
//                        Objects.requireNonNull(toInject, "Try to inject null value for " + field.getType().getSimpleName() + " in " + host.getClass().getSimpleName());
//                    }

                    if (toInject != null) {
                        field.set(host, toInject.dependency);
                    } else {
                        Log.warning(field.getType().getSimpleName() + " is null");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new GameException(DependencyManager.class, e);
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
            for (Field field : dependencyInfo.dependency.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Inject.class) && field.getType().isAnnotationPresent(GameObject.class)) {
                        field.set(dependencyInfo.dependency, null);
                    }
                } catch (IllegalAccessException e) {
                    throw new GameException(DependencyManager.class, e);
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

    public void destroyNonBindControllers() {
        destroyNonBindControllers(_applicationObjectPoolByClass);
        destroyNonBindControllers(_gameObjectPoolByClass);
    }

    private void destroyNonBindControllers(Map<Class<?>, DependencyInfo<?>> collection) {
        LuaControllerManager luaControllerManager = getDependency(LuaControllerManager.class);
        List<Class<?>> toRemove = collection.values().stream()
                .map(dependencyInfo -> dependencyInfo.dependency)
                .filter(o -> o instanceof LuaController)
                .map(o -> (LuaController) o)
                .filter(controller -> luaControllerManager.getFileName(controller.getClass().getCanonicalName()) == null)
                .map(LuaController::getClass)
                .collect(Collectors.toList());
        toRemove.forEach(collection::remove);
    }
}

package org.smallbox.faraway.core.dependencyInjector;

import org.reflections.Reflections;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.shortcut.ShortcutManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnDestroy;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnInit;
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

import static org.smallbox.faraway.util.Errors.CREATE_AND_INIT_ON_WRONG_OBJECT;

@ApplicationObject
public class DependencyManager {
    private static final DependencyManager _self = new DependencyManager();
    final Map<Class<?>, DependencyInfo<?>> _applicationObjectPoolByClass = new ConcurrentHashMap<>();
    final Map<Class<?>, DependencyInfo<?>> _gameObjectPoolByClass = new ConcurrentHashMap<>();
    private final DependencyNotifier dependencyNotifier = new DependencyNotifier();
    private boolean _init = false;
    private boolean _initGame = false;

    public static DependencyManager getInstance() {
        return _self;
    }

    private DependencyManager() {
        register(this);
        register(dependencyNotifier);
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
        Log.debug("Create application object: " + cls.getSimpleName());
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
        if (!cls.isAnnotationPresent(ApplicationObject.class)) {
            throw new GameException(DependencyManager.class, CREATE_AND_INIT_ON_WRONG_OBJECT);
        }

        if (!_applicationObjectPoolByClass.containsKey(cls)) {
            T object = create(cls);
            doInjectShortcut(object);
            doInjectDependency(object, false);
            dependencyNotifier.callMethodAnnotatedBy(object, OnInit.class, null);
        }

        return getDependency(cls);
    }

    /**
     * Register object to dependency injector
     *
     * @param component to register
     */
    public void register(Object component) {

        // Register game object
        if (component.getClass().isAnnotationPresent(GameObject.class)) {
            if (_gameObjectPoolByClass.put(component.getClass(), new DependencyInfo<>(component)) != null) {
                throw new GameException(DependencyManager.class, component.getClass() + " already register in DI");
            }
            return;
        }

        // Register application object
        if (component.getClass().isAnnotationPresent(ApplicationObject.class)) {
            if (_applicationObjectPoolByClass.put(component.getClass(), new DependencyInfo<>(component)) != null) {
                throw new GameException(DependencyManager.class, component.getClass() + " already register in DI");
            }
            return;
        }

        throw new GameException(DependencyManager.class, component.getClass() + " isn't a managed object");
    }

    /**
     * Create and initialize application objects
     */
    public void createApplicationDependencies() {

        if (_init) {
            throw new GameException(DependencyManager.class, "Cannot call injectDependencies after DI init");
        }

        _init = true;

        // Inject dependencies, shortcuts and call OnInit on application objects
        _applicationObjectPoolByClass.values().forEach(dependencyInfo -> doInjectShortcut(dependencyInfo.dependency));
        _applicationObjectPoolByClass.values().forEach(dependencyInfo -> doInjectDependency(dependencyInfo.dependency, false));
        _applicationObjectPoolByClass.values().forEach(dependencyInfo -> dependencyNotifier.callMethodAnnotatedBy(dependencyInfo.dependency, OnInit.class, null));
    }

    /**
     * Create and initialize game objects
     */
    public void injectGameDependencies() {

        if (_initGame) {
            throw new GameException(DependencyManager.class, "Cannot call injectGameDependencies after DI init game");
        }

        _initGame = true;

        // Re-inject dependencies to application objects
        _applicationObjectPoolByClass.values().forEach(dependencyInfo -> doInjectDependency(dependencyInfo.dependency, true));

        // Inject dependencies, shortcuts and call OnInit on game objects
        _gameObjectPoolByClass.values().forEach(dependencyInfo -> doInjectDependency(dependencyInfo.dependency, true));
        _gameObjectPoolByClass.values().forEach(dependencyInfo -> doInjectShortcut(dependencyInfo.dependency));
        _gameObjectPoolByClass.values().forEach(dependencyInfo -> dependencyNotifier.callMethodAnnotatedBy(dependencyInfo.dependency, OnInit.class, null));
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

    private void doInjectDependency(Object host, boolean gameExists) {
        doInjectDependency(host, host.getClass(), gameExists);
    }

    private void doInjectDependency(Object host, Class<?> cls, boolean gameExists) {
        Log.debug("Inject dependency to game object: " + cls.getSimpleName());

        for (Class<?> superClass = cls.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            doInjectDependency(host, superClass, gameExists);
        }

        for (Field field : cls.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(Inject.class)) {
                    Log.debug(String.format("Try to inject %s to %s", field.getType().getSimpleName(), host.getClass().getSimpleName()));
                    DependencyInfo<?> dependencyInfo = getDependencyInfo(field.getType());

                    if (dependencyInfo != null) {
                        field.setAccessible(true);
                        field.set(host, dependencyInfo.dependency);
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

        _gameObjectPoolByClass.values().forEach(dependencyInfo -> dependencyNotifier.callMethodAnnotatedBy(dependencyInfo.dependency, OnDestroy.class, null));
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

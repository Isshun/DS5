package org.smallbox.faraway.core.engine.module.java;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.engine.module.ApplicationModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.ModuleInfo;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Alex on 31/08/2015.
 */
public class ModuleManager {
    private final Executor              _executor = Executors.newFixedThreadPool(5);
    private List<ModuleBase>            _modulesThird = new ArrayList<>();
    private List<ApplicationModule>     _applicationModules = new ArrayList<>();
    private List<GameModule>            _gameModules = new ArrayList<>();
    private List<ModuleBase>            _modules = new ArrayList<>();
    private List<String>                _allowedModulesNames = Arrays.asList("WorldModule", "CharacterModule", "JobModule", "PathManager");

    public void loadModules(GDXApplication.OnLoad onLoad) {
        assert _modules.isEmpty();

        loadApplicationModules(onLoad);
        loadGameModules(onLoad);
        loadThirdPartyModules(onLoad);
    }

    private void loadThirdPartyModules(GDXApplication.OnLoad onLoad) {
        //        // List thirds party modules
//        List<ThirdPartyModule> thirdPartyModules = new ArrayList<>();
//        FileUtils.list("data/modules/").forEach(file -> {
//            try {
//                ModuleInfo info = ModuleInfo.fromJSON(new JSONObject(new String(Files.readAllBytes(new File(file, "module.json").toPath()), StandardCharsets.UTF_8)));
//                if ("java".equals(info.type)) {
//                    thirdPartyModules.addSubJob(new ThirdPartyModule(info, file));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        // Load thirds party modules
//        _modulesThird.clear();
//        thirdPartyModules.forEach(thirdPartyModule -> {
//            FileUtils.list(thirdPartyModule.getDirectory()).stream().filter(file -> file.getName().endsWith(".jar")).forEach(file -> {
//                Log.info("Load jar module: " + file.getAbsolutePath());
//
//                try {
//                    JarFile jarFile = new JarFile(file);
//                    Enumeration entries = jarFile.entries();
//
//                    URL[] urls = { file.toURI().toURL() };
//                    URLClassLoader cl = URLClassLoader.newInstance(urls);
//
//                    while (entries.hasMoreElements()) {
//                        JarEntry je = (JarEntry) entries.nextElement();
//                        if (je.isDirectory() || !je.getName().endsWith(".class")) {
//                            continue;
//                        }
//                        // -6 because of .class
//                        String className = je.getName().substring(0, je.getName().length()-6);
//                        className = className.replace('/', '.');
//                        Class<?> cls = cl.loadClass(className);
//                        if (GameModule.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
//                            Log.info("Discover third party module: " + cls.getSimpleName());
//                            try {
//                                GameModule module = cls.asSubclass(GameModule.class).getConstructor().newInstance();
//                                module.setInfo(thirdPartyModule.getInfo());
//                                _modulesThird.addSubJob(module);
//                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (BaseRenderer.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
//                            Log.info("Discover third party render: " + cls.getSimpleName());
//                            try {
//                                _rendersThird.addSubJob(cls.asSubclass(BaseRenderer.class).getConstructor().newInstance());
//                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//            });
//        });

//        _modules.addAll(_modulesThird);

//        Log.info("Load third party modules");
//        _modulesThird.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(ModuleBase::createGame);
//
    }

    // Load application modules
    private void loadApplicationModules(GDXApplication.OnLoad onLoad) {
        assert _applicationModules.isEmpty();
        Log.info("Load application modules");

        // Find application modules
        new Reflections("org.smallbox.faraway").getSubTypesOf(ApplicationModule.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .forEach(cls -> {
                    try {
                        Log.info("Find application module: " + cls.getSimpleName());
                        ApplicationModule module = cls.getConstructor().newInstance();
                        module.setInfo(ModuleInfo.fromName(module.getClass().getSimpleName()));
                        _applicationModules.add(module);
                        _modules.add(module);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        // Load application modules
        boolean moduleHasBeenLoaded;
        do {
            // Try to onLoad first module with required dependencies
            moduleHasBeenLoaded = false;
            for (ApplicationModule module: _applicationModules) {
                if (module.isActivate() && !module.isLoaded() && module.hasRequiredDependencies(_applicationModules)) {
                    module.load();
                    moduleHasBeenLoaded = true;
                    break;
                }
            }
        } while (moduleHasBeenLoaded);

        // Check all game modules has been loaded
        if (checkAllModulesHasBeenLoaded(_applicationModules)) {
            Log.info("All application modules has been loaded");
        } else {
            throw new RuntimeException("Some application modules could not be loaded");
        }

        _applicationModules.forEach(module -> Application.addObserver(module));
        _applicationModules.forEach(Application.dependencyInjector::register);
        _applicationModules.forEach(ModuleBase::create);
    }

    private void loadGameModules(GDXApplication.OnLoad onLoad) {
        assert _gameModules.isEmpty();
        Log.info("Load game modules");

        // Find game modules
        new Reflections("org.smallbox.faraway").getSubTypesOf(GameModule.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
//                .filter(cls -> _allowedModulesNames.contains(cls.getSimpleName()))
                .forEach(cls -> {
                    try {
                        Log.info("Find game module: " + cls.getSimpleName());
                        GameModule module = cls.getConstructor().newInstance();
                        module.setInfo(ModuleInfo.fromName(module.getClass().getSimpleName()));
                        _gameModules.add(module);
                        _modules.add(module);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });

        // Load game modules
        boolean moduleHasBeenLoaded;
        do {
            // Try to onLoad first module with required dependencies
            moduleHasBeenLoaded = false;
            for (GameModule module: _gameModules) {
                if (module.isActivate() && !module.isLoaded() && module.hasRequiredDependencies(_gameModules)) {
                    module.load();
                    moduleHasBeenLoaded = true;
                    break;
                }
            }
        } while (moduleHasBeenLoaded);

        // Check all game modules has been loaded
        if (checkAllModulesHasBeenLoaded(_gameModules)) {
            System.out.println("All game modules has been loaded");
        } else {
            throw new RuntimeException("Some game modules could not be loaded");
        }

        _gameModules.forEach(module -> Application.addObserver(module));
        _gameModules.forEach(Application.dependencyInjector::register);
        _gameModules.forEach(ModuleBase::create);
    }

    // Check if all modules have been loaded
    private boolean checkAllModulesHasBeenLoaded(List<? extends ModuleBase> modules) {
        boolean allModulesHasBeenLoaded = true;
        for (ModuleBase module: modules) {
            if (module.isActivate() && !module.isLoaded()) {
                allModulesHasBeenLoaded = false;
                Log.warning("[" + module.getName() + "]" + " could not be loaded");
            }
        }
        return allModulesHasBeenLoaded;
    }

    public ModuleBase                       getModule(Class<? extends ModuleBase> cls) { return _modules.stream().filter(cls::isInstance).findFirst().get(); }
    public ModuleBase                       getModule(String className) { return _modules.stream().filter(module -> module.getClass().getSimpleName().equals(className)).findFirst().get(); }
    public Collection<ModuleBase>           getModules() { return _modules; }
    public Collection<ModuleBase>           getModulesThird() { return _modulesThird; }
    public List<GameModule>                 getGameModules() { return _gameModules; }
    public List<ApplicationModule>          getApplicationModules() { return _applicationModules; }

    public void unloadModule(Class<? extends ModuleBase> cls) { unloadModule(getModule(cls)); }
    public void loadModule(Class<? extends ModuleBase> cls) { loadModule(getModule(cls)); }
    public void toggleModule(Class<? extends ModuleBase> cls) { toggleModule(getModule(cls)); }

    public void unloadModule(ModuleBase module) {
        if (!module.isModuleMandatory()) {
            module.unload();
            Application.removeObserver(module);
        }
    }

    public void loadModule(ModuleBase module) {
        module.load();
        Application.addObserver(module);
    }

    public void toggleModule(ModuleBase module) {
        if (module.isLoaded()) { unloadModule(module); }
        else { loadModule(module); }
    }

    public Executor getExecutor() {
        return _executor;
    }

    public void gameStart(Game game, List<GameModule> modules) {
        modules.stream().filter(ModuleBase::isLoaded).forEach(module -> module.startGame(game));
    }
}
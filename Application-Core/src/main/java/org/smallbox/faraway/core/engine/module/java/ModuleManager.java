package org.smallbox.faraway.core.engine.module.java;

import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.engine.module.ApplicationModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.ModuleInfo;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alex on 31/08/2015.
 */
@ApplicationObject
public class ModuleManager implements GameObserver {

    @Inject
    private GameManager gameManager;

    public interface OnLoadModuleListener {
        void onLoadModule(String message);
    }

    private List<ModuleBase>            _modulesThird = new ArrayList<>();
    private List<ApplicationModule>     _applicationModules = new ArrayList<>();
    private List<ModuleBase>            _modules;

    public void loadModules(OnLoadModuleListener onLoad) {
        _modules = new ArrayList<>();
        loadApplicationModules(onLoad);
        loadThirdPartyModules(onLoad);
    }

    private void loadThirdPartyModules(OnLoadModuleListener onLoad) {
        //        // List thirds party modules
//        List<ThirdPartyModule> thirdPartyModules = new ArrayList<>();
//        FileUtils.list("data/modules/").forEach(file -> {
//            try {
//                ModuleInfoAnnotation info = ModuleInfoAnnotation.fromJSON(new JSONObject(new String(Files.readAllBytes(new File(file, "module.json").toPath()), StandardCharsets.UTF_8)));
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
//                        if (BaseLayer.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
//                            Log.info("Discover third party render: " + cls.getSimpleName());
//                            try {
//                                _rendersThird.addSubJob(cls.asSubclass(BaseLayer.class).getConstructor().newInstance());
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
    private void loadApplicationModules(OnLoadModuleListener onLoad) {
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
                        throw new GameException(ModuleManager.class, "Cannot create application module: " + cls.getSimpleName());
                    }
                });

        // Load application modules
        boolean moduleHasBeenLoaded;
        do {
            // Try to onLoadModule first module with required dependencies
            moduleHasBeenLoaded = false;
            for (ApplicationModule module: _applicationModules) {
                if (module.isActivate() && !module.isLoaded()) {
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

        _applicationModules.forEach(Application::addObserver);
        _applicationModules.forEach(DependencyInjector.getInstance()::register);
        _applicationModules.forEach(ModuleBase::create);
    }

    // Check if all modules have been loaded
    private boolean checkAllModulesHasBeenLoaded(List<? extends ModuleBase> modules) {
        boolean allModulesHasBeenLoaded = true;
        for (ModuleBase module: modules) {
            if (module.isActivate() && !module.isLoaded()) {
                allModulesHasBeenLoaded = false;
                System.out.println("[" + module.getName() + "]" + " could not be loaded");
                Log.warning("[" + module.getName() + "]" + " could not be loaded");
            }
        }
        return allModulesHasBeenLoaded;
    }

    public <T> T getModule(Class<T> cls) {
        Optional<T> optionalApplicationModule = (Optional<T>) _modules.stream().filter(cls::isInstance).findFirst();
        if (optionalApplicationModule.isPresent()) {
            return optionalApplicationModule.get();
        }

        if (gameManager != null && gameManager.getGame() != null) {
            Optional<T> optionalGameModule = (Optional<T>) gameManager.getGame().getModules().stream().filter(cls::isInstance).findFirst();
            if (optionalGameModule.isPresent()) {
                return optionalGameModule.get();
            }
        }

        return null;
    }
    public ModuleBase                       getModule(String className) { return _modules.stream().filter(module -> module.getClass().getSimpleName().equals(className)).findFirst().get(); }
    public Collection<ModuleBase>           getModules() { return _modules; }
    public Collection<ModuleBase>           getModulesThird() { return _modulesThird; }
    public List<ApplicationModule>          getApplicationModules() { return _applicationModules; }
}
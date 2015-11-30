package org.smallbox.faraway.core.engine.module.java;

import com.sun.pisces.RendererBase;
import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GDXApplication;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.engine.module.ApplicationModule;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.ModuleInfo;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.MinimapRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Alex on 31/08/2015.
 */
public class ModuleManager {
    private static ModuleManager        _self;
    private List<ModuleBase>            _modulesThird = new ArrayList<>();
    private List<BaseRenderer>          _gameRenders = new ArrayList<>();
    private BaseRenderer                _minimapRenderer;
    private List<SerializerInterface>   _serializers = new ArrayList<>();
    private List<ApplicationModule>     _applicationModules = new ArrayList<>();
    private List<GameModule>            _gameModules = new ArrayList<>();
    private List<ModuleBase>            _modules = new ArrayList<>();

    public static ModuleManager getInstance() {
        if (_self == null) {
            _self = new ModuleManager();
        }
        return _self;
    }

    public void startGame(Game game) {
        _gameModules.stream().filter(ModuleBase::isLoaded).forEach(module -> module.startGame(game));
        _gameRenders.stream().filter(BaseRenderer::isLoaded).forEach(renderer -> renderer.startGame(game));
        _minimapRenderer.startGame(game);
    }

    public void initGame(Game game) {
        // Load modules
        _gameModules.forEach(ModuleBase::destroy);
        _gameModules.clear();
        new Reflections().getSubTypesOf(GameModule.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .forEach(cls -> {
                    try {
                        Log.info("Find module: " + cls.getSimpleName());
                        GameModule module = cls.getConstructor().newInstance();
                        ModuleInfo info = new ModuleInfo();
                        info.name = module.getClass().getSimpleName();
                        module.setInfo(info);
                        _gameModules.add(module);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        Collections.sort(_gameModules, (o1, o2) -> o2.getModulePriority() - o1.getModulePriority());
        game.setModules(_gameModules);
        _gameModules.stream().filter(ModuleBase::isLoaded).forEach(GameModule::create);
        _gameModules.forEach(module -> Application.getInstance().addObserver(module));

        _modules.clear();
        _modules.addAll(_applicationModules);
        _modules.addAll(_gameModules);
        _modules.sort((o1, o2) -> o2.getModulePriority() - o1.getModulePriority());

        // Load renders
        _gameRenders.forEach(BaseRenderer::destroy);
        _gameRenders.clear();
        new Reflections().getSubTypesOf(BaseRenderer.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Load render: " + cls.getSimpleName());
                BaseRenderer render = cls.getConstructor().newInstance();
                if (render instanceof  MinimapRenderer) {
                    _minimapRenderer = render;
                } else {
                    _gameRenders.add(render);
                }
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        _gameRenders.sort((r1, r2) -> r1.getLevel() - r2.getLevel());
        _gameRenders.forEach(renderer -> Application.getInstance().addObserver(renderer));
        Application.getInstance().addObserver(_minimapRenderer);
    }

    public void load(GDXApplication.OnLoad onLoad) {
        // Load modules
        assert _applicationModules.isEmpty();
        new Reflections().getSubTypesOf(ApplicationModule.class).stream()
                .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                .forEach(cls -> {
                    try {
                        Log.info("Find module: " + cls.getSimpleName());
                        ApplicationModule module = cls.getConstructor().newInstance();
                        ModuleInfo info = new ModuleInfo();
                        info.name = module.getClass().getSimpleName();
                        module.setInfo(info);
                        _applicationModules.add(module);
                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        _applicationModules.sort((m1, m2) -> m2.getModulePriority() - m1.getModulePriority());
        _applicationModules.forEach(module -> {
            onLoad.onLoad("Load: " + module.getClass().getSimpleName());
            module.create();
        });
        _applicationModules.forEach(module -> Application.getInstance().addObserver(module));

        // Load game serializers
        _serializers.clear();
        new Reflections().getSubTypesOf(SerializerInterface.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Load serializer: " + cls.getSimpleName());
                SerializerInterface serializer = cls.getConstructor().newInstance();
                _serializers.add(serializer);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        _serializers.sort((r1, r2) -> r2.getModulePriority() - r1.getModulePriority());

//        // List thirds party modules
//        List<ThirdPartyModule> thirdPartyModules = new ArrayList<>();
//        FileUtils.list("data/modules/").forEach(file -> {
//            try {
//                ModuleInfo info = ModuleInfo.fromJSON(new JSONObject(new String(Files.readAllBytes(new File(file, "module.json").toPath()), StandardCharsets.UTF_8)));
//                if ("java".equals(info.type)) {
//                    thirdPartyModules.add(new ThirdPartyModule(info, file));
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
//                                _modulesThird.add(module);
//                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        if (BaseRenderer.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
//                            Log.info("Discover third party render: " + cls.getSimpleName());
//                            try {
//                                _rendersThird.add(cls.asSubclass(BaseRenderer.class).getConstructor().newInstance());
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
//        _modulesThird.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(ModuleBase::create);
//
        Application.getInstance().notify(GameObserver::onReloadUI);
    }

    public BaseRenderer                     getRender(Class<? extends BaseRenderer> cls) { return _gameRenders.stream().filter(cls::isInstance).findFirst().get(); }
    public ModuleBase                       getModule(Class<? extends ModuleBase> cls) { return _modules.stream().filter(cls::isInstance).findFirst().get(); }
    public ModuleBase                       getModule(String className) { return _modules.stream().filter(module -> module.getClass().getSimpleName().equals(className)).findFirst().get(); }
    public Collection<BaseRenderer>         getRenders() { return _gameRenders; }
    public Collection<ModuleBase>           getModules() { return _modules; }
    public Collection<ModuleBase>           getModulesThird() { return _modulesThird; }
    public Collection<SerializerInterface>  getSerializers() { return _serializers; }

    public void unloadModule(Class<? extends ModuleBase> cls) { unloadModule(getModule(cls)); }
    public void loadModule(Class<? extends ModuleBase> cls) { loadModule(getModule(cls)); }
    public void toggleModule(Class<? extends ModuleBase> cls) { toggleModule(getModule(cls)); }

    public void unloadModule(ModuleBase module) {
        if (!module.isModuleMandatory()) {
            module.destroy();
            Application.getInstance().removeObserver(module);
        }
    }

    public void loadModule(ModuleBase module) {
        module.create();
        Application.getInstance().addObserver(module);
    }

    public void toggleModule(ModuleBase module) {
        if (module.isLoaded()) {
            if (!module.isModuleMandatory()) {
                module.destroy();
                Application.getInstance().removeObserver(module);
            }
        } else {
            module.create();
            Application.getInstance().addObserver(module);
        }
    }

    public BaseRenderer getMinimapRender() {
        return _minimapRenderer;
    }
}
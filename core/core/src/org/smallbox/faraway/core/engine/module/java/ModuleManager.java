package org.smallbox.faraway.core.engine.module.java;

import org.json.JSONObject;
import org.reflections.Reflections;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.data.serializer.SerializerInterface;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.ModuleInfo;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.MinimapRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Alex on 31/08/2015.
 */
public class ModuleManager {
    private static ModuleManager        _self;
    private List<ModuleBase>            _modules = new ArrayList<>();
    private List<ModuleBase>            _modulesBase = new ArrayList<>();
    private List<ModuleBase>            _modulesThird = new ArrayList<>();
    private List<BaseRenderer>          _renders = new ArrayList<>();
    private List<BaseRenderer>          _rendersBase = new ArrayList<>();
    private List<BaseRenderer>          _rendersThird = new ArrayList<>();
    private BaseRenderer                _minimapRenderer;
    private List<SerializerInterface>   _serializers = new ArrayList<>();

    public static ModuleManager getInstance() {
        if (_self == null) {
            _self = new ModuleManager();
        }
        return _self;
    }

    public void load() {
        // Load game modules
        _modulesBase.clear();
        new Reflections("org.smallbox.faraway").getSubTypesOf(ModuleBase.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Find module: " + cls.getSimpleName());
                ModuleBase module = cls.getConstructor().newInstance();
                ModuleInfo info = new ModuleInfo();
                info.name = module.getClass().getSimpleName();
                module.setInfo(info);
                _modulesBase.add(module);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        _modules.addAll(_modulesBase);

        // Load game renders
        _rendersBase.clear();
        new Reflections("org.smallbox.faraway").getSubTypesOf(BaseRenderer.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Load render: " + cls.getSimpleName());
                BaseRenderer render = cls.getConstructor().newInstance();
                if (render instanceof  MinimapRenderer) {
                    _minimapRenderer = render;
                } else {
                    _rendersBase.add(render);
                }
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        // Load game serializers
        _serializers.clear();
        new Reflections("org.smallbox.faraway").getSubTypesOf(SerializerInterface.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Load serializer: " + cls.getSimpleName());
                SerializerInterface serializer = cls.getConstructor().newInstance();
                _serializers.add(serializer);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        _serializers.sort((r1, r2) -> r2.getModulePriority() - r1.getModulePriority());

        // List thirds party modules
        List<ThirdPartyModule> thirdPartyModules = new ArrayList<>();
        FileUtils.list("data/modules/").forEach(file -> {
            try {
                ModuleInfo info = ModuleInfo.fromJSON(new JSONObject(new String(Files.readAllBytes(new File(file, "module.json").toPath()), StandardCharsets.UTF_8)));
                if ("java".equals(info.type)) {
                    thirdPartyModules.add(new ThirdPartyModule(info, file));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Load thirds party modules
        _modulesThird.clear();
        thirdPartyModules.forEach(thirdPartyModule -> {
            FileUtils.list(thirdPartyModule.getDirectory()).stream().filter(file -> file.getName().endsWith(".jar")).forEach(file -> {
                Log.info("Load jar module: " + file.getAbsolutePath());

                try {
                    JarFile jarFile = new JarFile(file);
                    Enumeration entries = jarFile.entries();

                    URL[] urls = { file.toURI().toURL() };
                    URLClassLoader cl = URLClassLoader.newInstance(urls);

                    while (entries.hasMoreElements()) {
                        JarEntry je = (JarEntry) entries.nextElement();
                        if (je.isDirectory() || !je.getName().endsWith(".class")) {
                            continue;
                        }
                        // -6 because of .class
                        String className = je.getName().substring(0, je.getName().length()-6);
                        className = className.replace('/', '.');
                        Class<?> cls = cl.loadClass(className);
                        if (ModuleBase.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                            Log.info("Discover third party module: " + cls.getSimpleName());
                            try {
                                ModuleBase module = cls.asSubclass(ModuleBase.class).getConstructor().newInstance();
                                module.setInfo(thirdPartyModule.getInfo());
                                _modulesThird.add(module);
                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        if (BaseRenderer.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                            Log.info("Discover third party render: " + cls.getSimpleName());
                            try {
                                _rendersThird.add(cls.asSubclass(BaseRenderer.class).getConstructor().newInstance());
                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        });

        _modulesBase.sort((m1, m2) -> m2.getModulePriority() - m1.getModulePriority());

//        _modules.addAll(_modulesThird);
        _modules.sort((m1, m2) -> m2.getModulePriority() - m1.getModulePriority());

        _renders.addAll(_rendersBase);
        _renders.addAll(_rendersThird);
        _renders.sort((r1, r2) -> r1.getLevel() - r2.getLevel());

        _modules.forEach(module -> Application.getInstance().addObserver(module));
        _renders.forEach(renderer -> Application.getInstance().addObserver(renderer));
        Application.getInstance().addObserver(_minimapRenderer);

        Log.info("Load base modules");
        _modulesBase.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() > 0).forEach(ModuleBase::create);
        _modulesBase.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(ModuleBase::create);

        Log.info("Load third party modules");
        _modulesThird.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(ModuleBase::create);

        Application.getInstance().notify(GameObserver::onReloadUI);

//        _renders.sort((m1, m2) -> m2.getModulePriority() - m1.getModulePriority());
    }

    public BaseRenderer                     getRender(Class<? extends BaseRenderer> cls) { return _renders.stream().filter(cls::isInstance).findFirst().get(); }
    public ModuleBase getModule(Class<? extends ModuleBase> cls) { return _modules.stream().filter(cls::isInstance).findFirst().get(); }
    public ModuleBase getModule(String className) { return _modules.stream().filter(module -> module.getClass().getSimpleName().equals(className)).findFirst().get(); }
    public Collection<BaseRenderer>         getRenders() { return _renders; }
    public Collection<ModuleBase>           getModules() { return _modules; }
    public Collection<ModuleBase>           getModulesBase() { return _modulesBase; }
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

    public void startGame(Game game) {
        Log.info("Load base modules");
        _modulesBase.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() > 0).forEach(module -> module.load(game));
        _modulesBase.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(module -> module.load(game));

        Log.info("Load third party modules");
        _modulesThird.stream().filter(ModuleBase::isLoaded).filter(module -> module.getModulePriority() == 0).forEach(module -> module.load(game));
    }
}
package org.smallbox.faraway.core.game.module;

import org.json.JSONObject;
import org.reflections.Reflections;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.ModuleInfo;
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
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Alex on 31/08/2015.
 */
public class ModuleManager {
    private static ModuleManager    _self;
    private List<GameModule>        _modules = new ArrayList<>();
    private List<GameModule>        _modulesBase = new ArrayList<>();
    private List<GameModule>        _modulesThird = new ArrayList<>();
    private List<BaseRenderer>      _renders = new ArrayList<>();
    private List<BaseRenderer>      _rendersBase = new ArrayList<>();
    private List<BaseRenderer>      _rendersThird = new ArrayList<>();

    public static ModuleManager getInstance() {
        if (_self == null) {
            _self = new ModuleManager();
        }
        return _self;
    }

    public void load() {
        // Load game modules
        _modulesBase.clear();
        new Reflections("org.smallbox.faraway").getSubTypesOf(GameModule.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                GameModule module = cls.getConstructor().newInstance();
                _modulesBase.add(module);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        // Load game renders
        _rendersBase.clear();
        new Reflections("org.smallbox.faraway").getSubTypesOf(BaseRenderer.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Load render: " + cls.getSimpleName());
                BaseRenderer render = cls.getConstructor().newInstance();
                _rendersBase.add(render);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

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
                System.out.println("Load jar module: " + file.getAbsolutePath());

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
                        if (GameModule.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                            System.out.println("Discover third party module: " + cls.getSimpleName());
                            try {
                                GameModule module = cls.asSubclass(GameModule.class).getConstructor().newInstance();
                                module.setInfo(thirdPartyModule.getInfo());
                                _modulesThird.add(module);
                            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        if (BaseRenderer.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                            System.out.println("Discover third party render: " + cls.getSimpleName());
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

        _modulesBase.sort((m1, m2) -> m2.getPriority() - m1.getPriority());

        _modules.addAll(_modulesBase);
//        _modules.addAll(_modulesThird);
        _modules.sort((m1, m2) -> m2.getPriority() - m1.getPriority());

        _renders.addAll(_rendersBase);
        _renders.addAll(_rendersThird);
//        _renders.sort((m1, m2) -> m2.getPriority() - m1.getPriority());
    }

    public GameModule           getModule(Class<? extends GameModule> cls) { return _modules.stream().filter(cls::isInstance).findFirst().get(); }
    public GameModule           getModule(String className) { return _modules.stream().filter(module -> module.getClass().getSimpleName().equals(className)).findFirst().get(); }
    public List<BaseRenderer>   getRenders() { return _renders; }
    public List<GameModule>     getModules() { return _modules; }
    public List<GameModule>     getModulesBase() { return _modulesBase; }
    public List<GameModule>     getModulesThird() { return _modulesThird; }

    public void unloadModule(Class<? extends GameModule> cls) { unloadModule(getModule(cls)); }
    public void loadModule(Class<? extends GameModule> cls) { loadModule(getModule(cls)); }
    public void toggleModule(Class<? extends GameModule> cls) { toggleModule(getModule(cls)); }

    public void unloadModule(GameModule module) {
        if (!module.isMandatory()) {
            module.destroy();
            Game.getInstance().removeObserver(module);
        }
    }

    public void loadModule(GameModule module) {
        module.create();
        Game.getInstance().addObserver(module);
    }

    public void toggleModule(GameModule module) {
        if (module.isLoaded()) {
            if (!module.isMandatory()) {
                module.destroy();
                Game.getInstance().removeObserver(module);
            }
        } else {
            module.create();
            Game.getInstance().addObserver(module);
        }
    }

}

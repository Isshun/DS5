package org.smallbox.faraway.game.module;

import org.reflections.Reflections;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
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
        new Reflections("org.smallbox.faraway").getSubTypesOf(GameModule.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                _modulesBase.add(cls.getConstructor().newInstance());
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        // Load game renders
        new Reflections("org.smallbox.faraway").getSubTypesOf(BaseRenderer.class).stream().filter(cls -> !Modifier.isAbstract(cls.getModifiers())).forEach(cls -> {
            try {
                Log.info("Load render: " + cls.getSimpleName());
                BaseRenderer render = cls.getConstructor().newInstance();
                _rendersBase.add(render);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        // Load thirds party modules
        FileUtils.listRecursively("mods/").stream().filter(file -> file.getName().endsWith(".jar")).forEach(file -> {
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
                        System.out.println("Load third party module: " + cls.getSimpleName());
                        try {
                            _modulesThird.add(cls.asSubclass(GameModule.class).getConstructor().newInstance());
                        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    if (BaseRenderer.class.isAssignableFrom(cls) && !Modifier.isAbstract(cls.getModifiers())) {
                        System.out.println("Load third party render: " + cls.getSimpleName());
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

        _modules.addAll(_modulesBase);
//        _modules.addAll(_modulesThird);
        _modules.sort((m1, m2) -> m2.getPriority() - m1.getPriority());

        _renders.addAll(_rendersBase);
        _renders.addAll(_rendersThird);
//        _renders.sort((m1, m2) -> m2.getPriority() - m1.getPriority());
    }

    public GameModule           getModule(Class<? extends GameModule> cls) { return _modules.stream().filter(cls::isInstance).findFirst().get(); }
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

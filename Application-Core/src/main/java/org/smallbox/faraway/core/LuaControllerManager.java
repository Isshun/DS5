package org.smallbox.faraway.core;

import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.game.BindController;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class LuaControllerManager {
    private static LuaControllerManager     _self;

    private List<LuaController>             _controllers = new ArrayList<>();

    public static LuaControllerManager getInstance() {
        if (_self == null) {
            _self = new LuaControllerManager();
        }
        return _self;
    }

    public void register(LuaController controller) {
        _controllers.add(controller);
    }

    // Inject controllers to modules
    public void injectControllersToModules() {
        ModuleManager.getInstance().getModules().forEach(module -> {
            for (Field field: module.getClass().getDeclaredFields()) {
                BindController annotation = field.getAnnotation(BindController.class);
                if (annotation != null) {
                    try {
                        field.setAccessible(true);
                        field.set(module, invokeController(field.getType()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

//    // Inject sub-controllers to controllers
//    public void injectSubControllersToControllers() {
//        _controllers.forEach(module -> {
//            for (Field field: module.getClass().getDeclaredFields()) {
//                LegacyBindController bindLuaView = field.getAnnotation(LegacyBindController.class);
//                if (bindLuaView != null) {
//                    try {
//                        field.setAccessible(true);
//                        field.set(module, invokeController(field.getType(), bindLuaView.value()));
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    /**
     * Invoke LuaController
     * @param cls LuaController type
     * @return Invoked LuaController
     */
    private LuaController invokeController(Class<?> cls) {
        try {
            Constructor constructor = cls.getConstructor();
            constructor.setAccessible(true);
            LuaController controller = (LuaController) constructor.newInstance();
            controller.setView(ModuleManager.getInstance().getViewByController(cls.getCanonicalName()));
            return controller;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public void injectModules() {
//        _controllers.forEach(controller -> {
//            for (Field field: controller.getClass().getDeclaredFields()) {
//                try {
//                    BindModule bindModule = field.getAnnotation(BindModule.class);
//                    if (bindModule != null) {
//                        Log.debug(String.format("Try to inject %s (%s) to %s", field.getType().getSimpleName(), bindModule.value(), this.getClass().getSimpleName()));
//                        field.setAccessible(true);
//                        field.set(controller, getModuleDependency(ModuleManager.getInstance().getGameModules(), field.getType()));
//                    }
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private ModuleBase getModuleDependency(List<? extends ModuleBase> loadedModules, Class cls) {
//        for (ModuleBase module: loadedModules) {
//            if (cls.isInstance(module)) {
//                return module;
//            }
//        }
//        return null;
//    }
}

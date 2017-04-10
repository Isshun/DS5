package org.smallbox.faraway.client;

import com.google.common.base.CaseFormat;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Alex on 04/04/2017.
 */
public class MenuManager {

    public MenuManager() {
        ApplicationClient.uiManager.getMenuViews().forEach((name, rootView) -> {
            if (rootView.getView().getController() != null) {
                bindControllerFields(rootView.getView().getController(), rootView.getView());
                bindControllerMethods(rootView.getView().getController(), rootView.getView());
                bindControllerSubControllers(rootView.getView().getController());
            }
        });
    }

    public void display(String viewName) {
        ApplicationClient.uiManager.getMenuViews().get(viewName).getView().setVisible(true);
    }

    /**
     * Bind controller fields
     *
     * @param controller Controller receiving binding
     * @param rootView Root view
     */
    private void bindControllerFields(LuaController controller, View rootView) {
        for (Field field : controller.getClass().getDeclaredFields()) {
            if (field.getAnnotation(BindLua.class) != null) {
                field.setAccessible(true);
                try {
                    // Bind by field name
                    {
                        View view = rootView.findById(field.getName());
                        if (view != null) {
                            Log.debug(LuaControllerManager.class, "LuaController: Bind view %s", view.getName());
                            field.set(controller, view);
                        }
                    }
                    // Bind by lower underscore converted field name
                    {
                        View view = rootView.findById(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName()));
                        if (view != null) {
                            Log.debug(LuaControllerManager.class, "LuaController: Bind view %s", view.getName());
                            field.set(controller, view);
                        }
                    }
                    if (field.get(controller) == null) {
                        throw new GameException(LuaControllerManager.class, "Unable to bind field: " + field.getName() + " in controller: " + controller.getClass().getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new GameException(LuaControllerManager.class, e);
                }
            }
        }
    }

    /**
     * Bind sub controllers
     *
     * @param controller Controller receiving binding
     */
    private void bindControllerSubControllers(LuaController controller) {
        for (Class cls = controller.getClass(); cls != null; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getAnnotation(BindLuaController.class) != null) {
                    field.setAccessible(true);
                    ApplicationClient.uiManager.getMenuViews().values().stream().map(rootView -> rootView.getView().getController())
                            .filter(subController -> subController != null && subController.getClass() == field.getType())
                            .findAny()
                            .ifPresent(subController -> {
                                try {
                                    Log.debug(LuaControllerManager.class, "LuaController: Bind sub controller %s", subController.getClass().getSimpleName());
                                    field.set(controller, subController);
                                } catch (IllegalAccessException e) {
                                    throw new GameException(LuaControllerManager.class, e);
                                }
                            });
                }
            }
        }
    }

    /**
     * Bind controller methods
     *
     * @param controller Controller receiving binding
     * @param rootView Root view
     */
    private void bindControllerMethods(LuaController controller, View rootView) {
        for (Method method: controller.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(BindLuaAction.class) != null) {
                method.setAccessible(true);

                View view = rootView.findByAction(method.getName());
                if (view != null) {
                    Log.debug(LuaControllerManager.class, "LuaController: Bind method %s", method.getName());
                    view.setOnClickListener2((GameEvent event) -> {
                        try {
                            Log.debug(LuaControllerManager.class, "Method: %s", method.getName());
                            Log.debug(LuaControllerManager.class, "View: %s", view.getName());
                            method.invoke(controller, view);
                        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                            throw new GameException(LuaControllerManager.class, e);
                        }
                    });
                }
            }
        }
    }
}

package org.smallbox.faraway.core.game.module.character.controller;

import com.google.common.base.CaseFormat;
import org.smallbox.faraway.core.LuaControllerManager;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController {
    private View _view;

    public LuaController() {
        ModuleManager.getInstance().addController(getClass().getCanonicalName(), this);
    }

    public void setView(View view) {
        _view = view;

        if (view != null) {
            Log.info("Set view to controller (%s -> %s)", view.getName(), getClass().getName());

            for (Field field : getClass().getDeclaredFields()) {
                if (field.getAnnotation(BindLua.class) != null) {
                    field.setAccessible(true);
                    try {
                        // Bind by field name
                        if (view.findById(field.getName()) != null) {
                            field.set(this, view.findById(field.getName()));
                        }
                        // Bind by lower underscore converted field name
                        if (view.findById(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName())) != null) {
                            field.set(this, view.findById(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName())));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            onCreate();
        }
    }

    protected View getView() {
        return _view;
    }

    protected abstract void onCreate();
}

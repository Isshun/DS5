package org.smallbox.faraway.core.game.module.character.controller;

import com.google.common.base.CaseFormat;
import org.smallbox.faraway.core.LuaControllerManager;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.ui.engine.views.widgets.View;

import java.lang.reflect.Field;

/**
 * Created by Alex on 25/04/2016.
 */
public abstract class LuaController {
    private View _view;

    public void setView(View view) {
        _view = view;

        for (Field field: getClass().getDeclaredFields()) {
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

    protected View getView() {
        return _view;
    }

    protected abstract void onCreate();
}

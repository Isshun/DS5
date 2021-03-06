package org.smallbox.faraway.client.ui.extra;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.client.ui.event.OnClickListener;
import org.smallbox.faraway.client.ui.event.OnFocusListener;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.UIDropDown;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;

public class ViewEvents {
    private final UIEventManager uiEventManager = DependencyManager.getInstance().getDependency(UIEventManager.class);
    private final View view;

    public OnClickListener _onClickListener;
    private UIEventManager.OnDragListener _onDragListener;
    private OnClickListener _onMouseWheelUpListener;
    private OnClickListener _onMouseWheelDownListener;
    private OnFocusListener _onFocusListener;

    public ViewEvents(View view) {
        this.view = view;
    }

    public boolean hasClickListener() {
        return _onClickListener != null;
    }

    public void setOnDragListener(UIEventManager.OnDragListener onDragListener) {
        _onDragListener = onDragListener;
        uiEventManager.setOnDragListener(view, _onDragListener);
    }

    public View setOnClickListener(OnClickListener onClickListener) {
        _onClickListener = onClickListener;
        uiEventManager.setOnClickListener(view, onClickListener);
        return view;
    }

    // TODO: crash in lua throw on main thread
    public void setOnClickListener(LuaValue value) {
        _onClickListener = () -> value.call(CoerceJavaToLua.coerce(view));
        uiEventManager.setOnClickListener(view, _onClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelUpListener(LuaValue value) {
        _onMouseWheelUpListener = () -> value.call(CoerceJavaToLua.coerce(view));
        uiEventManager.setOnMouseWheelUpListener(view, _onMouseWheelUpListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelDownListener(LuaValue value) {
        _onMouseWheelDownListener = () -> value.call(CoerceJavaToLua.coerce(view));
        uiEventManager.setOnMouseWheelDownListener(view, _onMouseWheelDownListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnFocusListener(LuaValue value) {
        _onFocusListener = new OnFocusListener() {
            @Override
            public void onEnter(View view) {
                value.call(CoerceJavaToLua.coerce(view), LuaValue.valueOf(true));
            }

            @Override
            public void onExit(View view) {
                value.call(CoerceJavaToLua.coerce(view), LuaValue.valueOf(false));
            }
        };
        uiEventManager.setOnFocusListener(view, _onFocusListener);
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        assert onFocusListener != null;
        _onFocusListener = onFocusListener;
        uiEventManager.setOnFocusListener(view, onFocusListener);
    }

    public void onEnter() {
        view.setFocus(true);
        if (_onFocusListener != null) {
            _onFocusListener.onEnter(view);
        }
    }

    public void onExit() {
        view.setFocus(false);
        if (_onFocusListener != null) {
            _onFocusListener.onExit(view);
        }
    }

    public void click(int x, int y) {
        if (_onClickListener != null) {
            _onClickListener.onClick();

            if (view.getParent() != null && view.getParent() instanceof UIDropDown) {
                ((UIDropDown)view.getParent()).setCurrent(view);
            }
        }
    }

    public void remove() {
        if (_onClickListener != null) {
            uiEventManager.removeOnClickListener(view);
        }
    }
}

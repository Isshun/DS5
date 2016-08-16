package org.smallbox.faraway.ui.engine.views.widgets;

import com.sun.istack.internal.NotNull;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.extend.FadeEffect;
import org.smallbox.faraway.core.engine.module.lua.data.extend.RotateAnimation;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.OnFocusListener;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.views.UIAdapter;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class View implements Comparable<View> {
    protected int _originWidth;
    protected int _originHeight;
    private String _group;

    public void setAlign(VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
        _verticalAlign = verticalAlign;
        _horizontalAlign = horizontalAlign;
    }

    public void clear() {
        UIEventManager.getInstance().removeListeners(_views);

        _views.forEach(View::clear);
        _views.clear();
    }

    public View getRootView() {
        for (View view = this; true; view = view.getParent()) {
            if (view.getParent() == null) {
                return view;
            }
        }
    }

    public String getGroup() {
        return _group;
    }

    public enum HorizontalAlign {LEFT, RIGHT, CENTER}
    public enum VerticalAlign {TOP, BOTTOM, CENTER}

    protected RotateAnimation _animation;
    protected boolean _focusable;
    private int _regularBackground;
    private int _focusBackground;

    public void setAnimation(RotateAnimation animation) {
        _animation = animation;
    }

    public void setFocusable(boolean focusable) {
        _focusable = focusable;
    }

    public void click(GameEvent event) {
        assert _onClickListener != null;
        _onClickListener.onClick(event);

        if (_parent != null && _parent instanceof UIDropDown) {
            ((UIDropDown)_parent).setCurrent(this);
        }
    }

    public enum Align { CENTER, LEFT, CENTER_VERTICAL, RIGHT };

    protected final ModuleBase  _module;
    protected List<View>        _views = new ArrayList<>();
    protected boolean           _isAlignLeft = true;
    protected boolean           _isAlignTop = true;
    protected int               _finalX;
    protected int               _finalY;
    protected int               _marginTop;
    protected int               _marginRight;
    protected int               _marginBottom;
    protected int               _marginLeft;
    protected UIAdapter         _adapter;
    protected int               _objectId;
    protected int               _hash;
    protected int               _fixedWidth = -1;
    protected int               _fixedHeight = -1;
    protected String            _name;
    protected boolean           _inGame;
    protected int               _deep;
    protected int               _level;
    protected Color             _backgroundFocusColor;
    protected int               _width = -1;
    protected int               _height = -1;
    protected int               _x;
    protected int               _y;
    protected boolean           _isVisible;
    protected int               _paddingLeft;
    protected int               _paddingBottom;
    protected int               _paddingRight;
    protected int               _paddingTop;
    protected View              _parent;
    protected OnClickListener   _onClickListener;
    protected OnClickListener   _onRightClickListener;
    protected OnClickListener   _onMouseWheelUpListener;
    protected OnClickListener   _onMouseWheelDownListener;
    protected OnFocusListener   _onFocusListener;
    protected boolean           _isFocus;
    protected boolean           _isActive = true;
    protected int               _id;
    protected String            _actionName;
    protected int               _borderSize;
    protected Object            _data;
    protected Align             _align = Align.LEFT;
    protected int               _offsetX;
    protected int               _offsetY;
    protected int               _layer;
    protected Color             _backgroundColor;
    protected FadeEffect        _effect;
    protected HorizontalAlign   _horizontalAlign;
    protected VerticalAlign     _verticalAlign;

    public View(ModuleBase module) {
        _module = module;
        _isVisible = true;
        _borderSize = 2;
        _x = 0;
        _y = 0;
    }

    public boolean      isFocus() { return _isFocus; }
    public boolean      isVisible() { return _isVisible; }
    public boolean      isActive() { return _isActive; }
    public boolean      inGame() { return _inGame; }

    public void         setId(int id) { _id = id; }
    public void         setId(String id) { _id = id.hashCode(); }
    public void         setTextAlign(Align align) { _align = align; }
    public void         setFocus(boolean focus) { _isFocus = focus; }
    public void         setActive(boolean active) { _isActive = active; }
    public void         setParent(View parent) {
        _parent = parent;
    }
    public void         setAdapter(UIAdapter adapter) {
        _adapter = adapter;
    }
    public void         setName(String name) { _name = name; }
    public void         setInGame(boolean inGame) { _inGame = inGame; }
    public void         setDeep(int deep) { _deep = deep; if (_views != null) _views.forEach(view -> view.setDeep(deep + 1));}
    public void         setLevel(int level) { _level = level; }
    public View         setBackgroundColor(long color) { _backgroundColor = new Color(color); return this; }
    public View         setBackgroundColor(Color color) { _backgroundColor = color; return this; }

    public void         setVisible(boolean visible) {
        if (visible && _group != null) {
            // Set visible false for other views sharing current view's group
            UserInterface.getInstance().getViews().stream()
                    .filter(view -> _group.equals(view.getGroup()))
                    .forEach(view -> view.setVisible(false));
        }

        // Set current view visible
        _isVisible = visible;
    }

    public void         setGroup(String group) { _group = group; }
    public void         setEffect(FadeEffect effect) { _effect = effect; }
    public void         setRegularBackgroundColor(int regularBackground) { _regularBackground = regularBackground; }
    public View         setFocusBackgroundColor(int focusBackground) { _focusBackground = focusBackground; return this; }
    public void         setActionName(String actionName) { _actionName = actionName; }
    public void         setLayer(int layer) { _layer = layer; }

    public Color        getBackgroundColor() { return _backgroundColor; }
    public int          getLayer() { return _layer; }
    public View         getParent() { return _parent; }
    public int          getId() { return _id; }
    public int          getPosX() { return _x; }
    public int          getPosY() { return _y; }
    public int          getFinalX() { return _finalX; }
    public int          getFinalY() { return _finalY; }
    public int          getDeep() { return _deep; }
    public int          getLevel() { return _level; }
    public int          getRegularBackground() { return _regularBackground; }
    public int          getFocusBackground() { return _focusBackground; }
    public String       getName() { return _name; }
    public List<View>   getViews() { return _views; }
    public ModuleBase   getModule() { return _module; }
    protected String    getString() { return null; }
    public int          getHeight() { return _height; }
    public int          getWidth() { return _width; }
    public int          getMarginTop() { return _marginTop; }
    public int          getMarginRight() { return _marginRight; }
    public int          getMarginBottom() { return _marginBottom; }
    public int          getMarginLeft() { return _marginLeft; }
    public FadeEffect   getEffect() { return _effect; }
    public String       getActionName() { return _actionName; }

    public int          compareLevel(View view) { return _deep != view.getDeep() ? _deep - view.getDeep() : hashCode() - view.hashCode(); }

    @Override
    public int compareTo(@NotNull View view) {
        return view.hashCode() - hashCode();
    }

    public void draw(GDXRenderer renderer, int x, int y) {
        if (_isVisible) {
            _finalX = getAlignedX() + _marginLeft + x;
            _finalY = getAlignedY() + _marginTop + y;

            if (_backgroundFocusColor != null && _isFocus) {
                renderer.draw(_backgroundFocusColor, _finalX, _finalY, _width, _height);
            }
            else if (_backgroundColor != null) {
                renderer.draw(_backgroundColor, _finalX, _finalY, _width, _height);
            }

            if (_adapter != null && _adapter.getData() != null && needRefresh(_adapter)) {
                removeAllViews();
                _adapter.setRefresh();
                Iterator<ObjectModel> iterator = _adapter.getData().iterator();
                try {
                    while (iterator.hasNext()) {
                        ObjectModel data = iterator.next();
                        View subview = _adapter.getCallback().onCreateView();
                        subview.setObjectId(data.id);
                        _adapter.getCallback().onBindView(subview, data);
                        addView(subview);
                    }
                } catch (ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTextAlign(boolean isAlignLeft, boolean isAlignTop) {
        _isAlignLeft = isAlignLeft;
        _isAlignTop = isAlignTop;
    }

    public final void addView(View view) {
        UserInterface.getInstance().addView(view);

        onAddView(view);
    }

    protected abstract void onAddView(View view);

    public final void removeAllViews() {
        _views.forEach(view -> {
            UserInterface.getInstance().removeView(view);
            UIEventManager.getInstance().removeListeners(view);
            view.removeAllViews();
            onRemoveView(view);
        });
        _views.clear();
    }

    protected abstract void onRemoveView(View view);

    public boolean contains(int x, int y) {
        return (_finalX <= x && _finalX + _width >= x && _finalY <= y && _finalY + _height >= y);
    }

    public void setMargin(int top, int right, int bottom, int left) {
        _marginTop = (int) (top * Application.getInstance().getConfig().uiScale);
        _marginRight = (int) (right * Application.getInstance().getConfig().uiScale);
        _marginBottom = (int) (bottom * Application.getInstance().getConfig().uiScale);
        _marginLeft = (int) (left * Application.getInstance().getConfig().uiScale);
    }

    public UIAdapter getAdapter() {
        return _adapter;
    }

    private void setObjectId(int objectId) {
        _objectId = objectId;
    }

    private boolean needRefresh(UIAdapter adapter) {
        return true;
    }

    private int getObjectId() {
        return _objectId;
    }

    public View setBackgroundFocusColor(long color) {
        _backgroundFocusColor = new Color(color);
        return this;
    }

    public View setOnClickListener(OnClickListener onClickListener) {
        assert onClickListener != null;
        _onClickListener = onClickListener;
        UIEventManager.getInstance().setOnClickListener(this, onClickListener);
        return this;
    }

    // TODO: crash in lua throw on main thread
    public void setOnClickListener(LuaValue value) {
        _onClickListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        UIEventManager.getInstance().setOnClickListener(this, _onClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnRightClickListener(LuaValue value) {
        _onRightClickListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        UIEventManager.getInstance().setOnRightClickListener(this, _onRightClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelUpListener(LuaValue value) {
        _onMouseWheelUpListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        UIEventManager.getInstance().setOnMouseWheelUpListener(this, _onMouseWheelUpListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelDownListener(LuaValue value) {
        _onMouseWheelDownListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        UIEventManager.getInstance().setOnMouseWheelDownListener(this, _onMouseWheelDownListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnFocusListener(LuaValue value) {
        _onFocusListener = new OnFocusListener() {
            @Override
            public void onEnter(View view) {
                value.call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(true));
            }

            @Override
            public void onExit(View view) {
                value.call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(false));
            }
        };
        UIEventManager.getInstance().setOnFocusListener(this, _onFocusListener);
    }

    public void setOnRightClickListener(OnClickListener onClickListener) {
        assert onClickListener != null;
        _onRightClickListener = onClickListener;
        UIEventManager.getInstance().setOnRightClickListener(this, onClickListener);
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        assert onFocusListener != null;
        _onFocusListener = onFocusListener;
        UIEventManager.getInstance().setOnFocusListener(this, onFocusListener);
    }

    public void onClick(GameEvent event) {
        if (_onClickListener != null) {
            _onClickListener.onClick(event);
        }
    }

    public void setPadding(int t, int r, int b, int l) {
        _paddingTop = (int) (t * Application.getInstance().getConfig().uiScale);
        _paddingRight = (int) (r * Application.getInstance().getConfig().uiScale);
        _paddingBottom = (int) (b * Application.getInstance().getConfig().uiScale);
        _paddingLeft = (int) (l * Application.getInstance().getConfig().uiScale);
    }

    public void setPadding(int t, int r) {
        _paddingTop = _paddingBottom = (int) (t * Application.getInstance().getConfig().uiScale);
        _paddingRight = _paddingLeft = (int) (r * Application.getInstance().getConfig().uiScale);
    }

    public View setPadding(int padding) {
        _paddingTop = _paddingBottom = _paddingRight = _paddingLeft = (int) (padding * Application.getInstance().getConfig().uiScale);
        return this;
    }

    public View setFixedSize(int width, int height) {
        _fixedWidth = (int) (width * Application.getInstance().getConfig().uiScale);
        _fixedHeight = (int) (height * Application.getInstance().getConfig().uiScale);
        return this;
    }

    public View setSize(int width, int height) {
        _width = (int) (width * Application.getInstance().getConfig().uiScale);
        _height = (int) (height * Application.getInstance().getConfig().uiScale);
        _originWidth = width;
        _originHeight = height;
        return this;
    }

    public View setPosition(int x, int y) {
//        x = (int) (x * Application.getInstance().getConfig().uiScale);
//        y = (int) (y * Application.getInstance().getConfig().uiScale);
//        _x = _horizontalAlign == HorizontalAlign.LEFT ? x : Application.getInstance().getConfig().screen.resolution[0] - x;
//        _y = _verticalAlign == VerticalAlign.TOP ? y : Application.getInstance().getConfig().screen.resolution[1] - y;
        _x = (int) (x * Application.getInstance().getConfig().uiScale);
        _y = (int) (y * Application.getInstance().getConfig().uiScale);

        return this;
    }

    public void onEnter() {
        _isFocus = true;
        if (_onFocusListener != null) {
            _onFocusListener.onEnter(this);
        }
    }

    public void onExit() {
        _isFocus = false;
        if (_onFocusListener != null) {
            _onFocusListener.onExit(this);
        }
    }

    public Object getData() {
        return _data;
    }

    public void setData(Object data) {
        _data = data;
    }

    protected void remove() {
        _parent = null;
        if (_onClickListener != null) {
            UIEventManager.getInstance().removeOnClickListener(this);
        }
    }

    public abstract int getContentWidth();
    public abstract int getContentHeight();
    public void init(){}

    public View findById(String id) {
        return findById(id.hashCode());
    }

    public View findByAction(String actionName) {
        for (View view: _views) {
            if (view._actionName != null && view._actionName.equals(actionName)) {
                return view;
            }
            View ret = view.findByAction(actionName);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    public View findById(int resId) {
        for (View view: _views) {
            if (view._id == resId) {
                return view;
            }
            View ret = view.findById(resId);
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }

    protected int getAlignedX() {
        if (_horizontalAlign == HorizontalAlign.CENTER) {
            return (_parent.getWidth() / 2) - (_width / 2) + _x;
        }
        if (_horizontalAlign == HorizontalAlign.RIGHT) {
            return _parent.getWidth() - _x;
        }
        return _x;
    }

    protected int getAlignedY() {
        if (_verticalAlign == VerticalAlign.CENTER) {
            return (_parent.getHeight() / 2) - (_height / 2) + _y;
        }
        if (_verticalAlign == VerticalAlign.BOTTOM) {
            return _parent.getHeight() - _y;
        }
        return _y;
    }
}
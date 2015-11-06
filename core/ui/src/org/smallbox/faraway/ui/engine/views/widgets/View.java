package org.smallbox.faraway.ui.engine.views.widgets;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.OnFocusListener;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.views.UIAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class View {
    public List<View> _views = new ArrayList<>();

    protected boolean   _isAlignLeft = true;
    protected boolean   _isAlignTop = true;
    protected int       _finalX;
    protected int       _finalY;
    protected int         _marginTop;
    protected int         _marginRight;
    protected int         _marginBottom;
    protected int         _marginLeft;
    private UIAdapter   _adapter;
    private int         _objectId;
    private LuaModule   _module;
    protected int       _hash;
    protected int       _fixedWidth = -1;
    protected int       _fixedHeight = -1;
    private String      _name;
    private boolean     _inGame;
    private Color       _backgroundFocusColor;

    public void setTextAlign(boolean isAlignLeft, boolean isAlignTop) {
        _isAlignLeft = isAlignLeft;
        _isAlignTop = isAlignTop;
    }

    public abstract void addView(View view);

    public void removeAllViews() {
        _views.forEach(view -> {
            UIEventManager.getInstance().removeListeners(view);
            view.removeAllViews();
        });
        _views.clear();
    }

    public int getHeight() {
        return _height;
    }

    public boolean contains(int x, int y) {
        return (_finalX <= x && _finalX + _width >= x && _finalY <= y && _finalY + _height >= y);
    }

    public void setModule(LuaModule module) {
        _module = module;
    }

    public LuaModule getModule() {
        return _module;
    }

    protected String getString() {
        return null;
    }

    public void setFixedSize(int width, int height) {
        _fixedWidth = width;
        _fixedHeight = height;
    }

    public void setMargin(int top, int right, int bottom, int left) {
        _marginTop = top;
        _marginRight = right;
        _marginBottom = bottom;
        _marginLeft = left;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setInGame(boolean inGame) {
        _inGame = inGame;
    }

    public boolean inGame() {
        return _inGame;
    }

    public enum Align { CENTER, LEFT, CENTER_VERTICAL, RIGHT };

    protected int               _width = -1;
    protected int               _height = -1;
    public int                  _x;
    public int                  _y;
    protected boolean            _isVisible;
    protected Rectangle         _rect;
    protected int                 _paddingLeft;
    protected int                _paddingBottom;
    protected int                 _paddingRight;
    protected int                 _paddingTop;
    public View _parent;
    protected OnClickListener   _onClickListener;
    private OnClickListener     _onRightClickListener;
    protected OnFocusListener   _onFocusListener;
    protected boolean             _isFocus;
    protected int                 _id;
    protected int                 _borderSize;
    protected boolean             _invalid;
    protected Object             _data;
    protected Align             _align = Align.LEFT;
    protected int               _offsetX;
    protected int               _offsetY;
    protected Color             _backgroundColor;

    public View(int width, int height) {
        _width = width;
        _height = height;
        _isVisible = true;
        _borderSize = 2;
        _x = 0;
        _y = 0;
    }

    public boolean         isFocus() { return _isFocus; }
    public boolean         isVisible() { return _isVisible; }

    public void         setId(int id) { _id = id; }
    public void         setId(String id) { _id = id.hashCode(); }
    public void         setTextAlign(Align align) { _align = align; }
    public void         setFocus(boolean focus) { _isFocus = focus; }
    public void         setParent(View parent) {
        _parent = parent;
    }
    public void         setAdapter(UIAdapter adapter) {
        _adapter = adapter;
    }

    public View         getParent() { return _parent; }
    public int             getId() { return _id; }
    public int             getPosX() { return _x; }
    public int             getPosY() { return _y; }
    public List<View>   getViews() { return _views; }

    public void draw(GDXRenderer renderer, int x, int y) {
        if (_isVisible) {
            _finalX = _x + _marginTop + x;
            _finalY = _y + _marginTop + y;

            if (_backgroundColor != null) {
                renderer.draw(_backgroundColor, _x + _marginLeft + x, _y + _marginTop + y, _width, _height);
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

    public UIAdapter getAdapter() {
        return _adapter;
    }

    private void setObjectId(int objectId) {
        _objectId = objectId;
    }

    private boolean needRefresh(UIAdapter adapter) {
        return true;
//        if (adapter.getData().size() != _views.size()) {
//            return true;
//        }
//        Iterator<ObjectModel> objIterator = adapter.getData().iterator();
//        Iterator<View> subviewIterator = _views.iterator();
//        while (objIterator.hasNext() && subviewIterator.hasNext()) {
//            if (objIterator.next().id != subviewIterator.next().getObjectId()) {
//                return true;
//            }
//        }
//        return objIterator.hasNext() || subviewIterator.hasNext();
    }

    private int getObjectId() {
        return _objectId;
    }

    public void setBackgroundFocusColor(long color) {
        if (_backgroundFocusColor == null) {
            UIEventManager.getInstance().setOnFocusListener(this, new OnFocusListener() {
                Color _oldColor;
                @Override
                public void onEnter(View view) {
                    _oldColor = view.getBackgroundColor();
                    view.setBackgroundColor(_backgroundFocusColor);
                }

                @Override
                public void onExit(View view) {
                    view.setBackgroundColor(_oldColor);
                }
            });
        }
        _backgroundFocusColor = new Color(color);
    }

    private Color getBackgroundColor() {
        return _backgroundColor;
    }

    public void setBackgroundColor(long color) {
        _backgroundColor = new Color(color);
    }

    public void setBackgroundColor(Color color) {
        _backgroundColor = color;
    }

    public void setBorderColor(Color color) {
    }

    public void setVisible(boolean visible) {
        _isVisible = visible;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        _onClickListener = onClickListener;
        UIEventManager.getInstance().setOnClickListener(this, onClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnClickListener(LuaValue value) {
        _onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                value.call(CoerceJavaToLua.coerce(this));
            }
        };
        UIEventManager.getInstance().setOnClickListener(this, _onClickListener);
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
        _onRightClickListener = onClickListener;
        UIEventManager.getInstance().setOnRightClickListener(this, onClickListener);
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        _onFocusListener = onFocusListener;
        UIEventManager.getInstance().setOnFocusListener(this, onFocusListener);
    }

    public void onClick() {
        if (_onClickListener != null) {
            _onClickListener.onClick(this);
        }
    }

    public void setPadding(int t, int r, int b, int l) {
        _paddingTop = t;
        _paddingRight = r;
        _paddingBottom = b;
        _paddingLeft = l;
        _invalid = true;
    }

    public void setPadding(int t, int r) {
        _paddingTop = t;
        _paddingRight = r;
        _paddingBottom = t;
        _paddingLeft = r;
        _invalid = true;
    }

    public void setPadding(int padding) {
        _paddingTop = padding;
        _paddingRight = padding;
        _paddingBottom = padding;
        _paddingLeft = padding;
        _invalid = true;
    }

    public void setSize(int width, int height) {
        _width = (int) (width * Data.config.uiScale);
        _height = (int) (height * Data.config.uiScale);
        _invalid = true;
    }

    public void setPosition(int x, int y) {
        _x = (int) (x * Data.config.uiScale) + (_isAlignLeft ? 0 : Data.config.screen.resolution[0]);
        _y = (int) (y * Data.config.uiScale) + (_isAlignTop ? 0 : Data.config.screen.resolution[1]);

        _invalid = true;
    }

    public int getMarginTop() { return _marginTop; }
    public int getMarginRight() { return _marginRight; }
    public int getMarginBottom() { return _marginBottom; }
    public int getMarginLeft() { return _marginLeft; }

    public int getPaddingTop() { return _paddingTop; }
    public int getPaddingRight() { return _paddingRight; }
    public int getPaddingBottom() { return _paddingBottom; }
    public int getPaddingLeft() { return _paddingLeft; }

    public void setMarginLeft(int marginLeft) {
        _marginLeft = marginLeft;
    }

    public void setMarginTop(int marginTop) {
        _marginTop = marginTop;
    }

    private int getOffsetX() {
        return _offsetX;
    }

    private int getOffsetY() {
        return _offsetY;
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

    public void setBorderSize(int borderSize) {
        _borderSize = borderSize;
        _invalid = true;
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
}

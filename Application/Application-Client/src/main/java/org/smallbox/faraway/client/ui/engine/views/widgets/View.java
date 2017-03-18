package org.smallbox.faraway.client.ui.engine.views.widgets;

import org.eclipse.jetty.util.ConcurrentArrayQueue;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.FadeEffect;
import org.smallbox.faraway.client.RotateAnimation;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.OnFocusListener;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.UIAdapter;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.config.Config;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class View implements Comparable<View> {
    protected int _originWidth;
    protected int _originHeight;
    private String _group;
    private String _path;
    private int _index;
    private boolean _sorted;
    private Color _borderColor;

    public void setAlign(VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
        _verticalAlign = verticalAlign;
        _horizontalAlign = horizontalAlign;
    }

    public final void removeAllViews() {
        _views.forEach(view -> {
            ApplicationClient.uiManager.removeView(view);
            ApplicationClient.uiEventManager.removeListeners(view);
            view.removeAllViews();
            onRemoveView(view);
        });
        _views.clear();
    }

    public String getGroup() {
        return _group;
    }

    public boolean isLeaf() {
        return _views.isEmpty();
    }

    public void setSpecial(boolean special) {
        _special = special;
    }

    public void actionSpecial() {
        _parent.setSpecialTop(this);
    }

    public boolean hasSpecial() {
        return _special;
    }

    public void setSpecialTop(View specialTop) {
        _specialTop = specialTop;
    }

    public void setPath(String path) {
        _path = path;
    }

    public String getPath() {
        return _path;
    }

    public void setIndex(int index) {
        _index = index;
    }

    public int getIndex() {
        return _index;
    }

    public View setBorderColor(long color) {
        _borderColor = new Color(color);
        return this;
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

    public void setFocusable(boolean focusable) { _focusable = focusable; }
    public void setSorted(boolean sorted) {
        _sorted = sorted;

        if (sorted) {
            Collection<View> views = _views;
             _views = new PriorityBlockingQueue<>(10, View::compareTo);
             _views.addAll(views);
        }
    }

    public void click(GameEvent event) {
        assert _onClickListener != null;
        event.view = this;
        _onClickListener.onClick(event);

        if (_parent != null && _parent instanceof UIDropDown) {
            ((UIDropDown)_parent).setCurrent(this);
        }
    }

    public enum Align { CENTER, LEFT, CENTER_VERTICAL, RIGHT }

    protected final ModuleBase  _module;

//    protected Set<View>         _views = new ConcurrentSkipListSet<>((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
    protected Collection<View>  _views = new ConcurrentArrayQueue<>();
    protected Collection<View>  _nextViews = new ConcurrentArrayQueue<>();
    protected boolean           _isAlignLeft = true;
    protected boolean           _isAlignTop = true;
    protected boolean           _special = false;
    private View _specialTop;
    protected int               _finalX;
    protected int               _finalY;
    protected int               _marginTop;
    protected int               _marginRight;
    protected int               _marginBottom;
    protected int               _marginLeft;
    protected UIAdapter _adapter;
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
    protected UIEventManager.OnDragListener _onDragListener;
    protected OnClickListener   _onRightClickListener;
    protected OnClickListener   _onMouseWheelUpListener;
    protected OnClickListener   _onMouseWheelDownListener;
    protected OnFocusListener _onFocusListener;
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
    protected HorizontalAlign   _horizontalAlign = HorizontalAlign.LEFT;
    protected VerticalAlign     _verticalAlign = VerticalAlign.TOP;

    public View(ModuleBase module) {
        _module = module;
        _isVisible = true;
        _borderSize = 2;
        _x = 0;
        _y = 0;
    }

    public boolean      isFocus() { return _isFocus; }
    public boolean      isVisible() { return _isVisible && (_parent == null || _parent.isVisible()); }
    //    public boolean      isVisible() { return _isVisible; }
    public boolean      isActive() { return _isActive; }
    public boolean      inGame() { return _inGame; }

    public View         setId(int id) { _id = id; return this; }
    public View         setId(String id) { _id = id.hashCode(); return this; }
    public void         setTextAlign(Align align) { _align = align; }
    public void         setFocus(boolean focus) { _isFocus = focus; }
    public void         setActive(boolean active) { _isActive = active; }
    public void         setParent(View parent) {
        _parent = parent;
    }
    public void         setAdapter(UIAdapter adapter) {
        _adapter = adapter;
    }
    public View         setName(String name) { _name = name; return this; }
    public void         setInGame(boolean inGame) { _inGame = inGame; }
    public void         setDeep(int deep) { _deep = deep; if (_views != null) _views.forEach(view -> view.setDeep(deep + 1));}
    public void         setLevel(int level) { _level = level; }
    public View         setBackgroundColor(long color) { _backgroundColor = new Color(color); return this; }
    public View         setBackgroundColor(Color color) { _backgroundColor = color; return this; }

    public void         toggleVisible() { setVisible(!isVisible()); }
    public void         setVisible(boolean visible) {

//        // Masque les vues appartenemt au même groupe
//        if (visible && _group != null) {
//            ApplicationClient.uiManager.getViews().stream()
//                    .filter(view -> view != this && _group.equals(view.getGroup()))
//                    .forEach(view -> view.setVisible(false));
//        }

        if (_parent != null && _parent._special) {
            _parent.getViews().forEach(view -> view._isVisible = false);
//            _parent.setVisible(false);
        }

        // Set current view visible
        _isVisible = visible;
    }

    public void         setGroup(String group) { _group = group; }
    public void         setEffect(FadeEffect effect) { _effect = effect; }
    public View         setRegularBackgroundColor(int regularBackground) { _regularBackground = regularBackground; return this; }
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

    public Collection<View> getViews() { return _views; }
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
    public int compareTo(View view) {
        return view.hashCode() - hashCode();
    }

    public void draw(GDXRenderer renderer, int x, int y) {
        if (_isVisible) {
            _finalX = getAlignedX() + _marginLeft + x;
            _finalY = getAlignedY() + _marginTop + y;

            if (_backgroundFocusColor != null && _isFocus) {
                renderer.drawPixel(_backgroundFocusColor, _finalX, _finalY, _width, _height);
            }

            else if (_backgroundColor != null) {
                renderer.drawPixel(_backgroundColor, _finalX, _finalY, _width, _height);
            }

            if (_borderColor != null) {
                renderer.drawRectangle(_finalX, _finalY, _width, _height, _borderColor, false);
            }

//            if (_adapter != null && _adapter.getData() != null && needRefresh(_adapter)) {
//                removeAllViews();
//                _adapter.setRefresh();
//                Iterator<ObjectModel> iterator = _adapter.getData().iterator();
//                try {
//                    while (iterator.hasNext()) {
//                        ObjectModel data = iterator.next();
//                        View subview = _adapter.getCallback().onCreateView();
//                        subview.setObjectId(data.id);
//                        _adapter.getCallback().onBindView(subview, data);
//                        addView(subview);
//                    }
//                } catch (ConcurrentModificationException e) {
//                    e.printStackTrace();
//                }
//            }

            if (Config.onDebugView) {
                renderer.drawText(getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft, getAlignedY() + y + _offsetY + _paddingTop + _marginTop, 12, com.badlogic.gdx.graphics.Color.CYAN, _name);
            }
        }
    }

    public void setTextAlign(boolean isAlignLeft, boolean isAlignTop) {
        _isAlignLeft = isAlignLeft;
        _isAlignTop = isAlignTop;
    }

    public final void addNextView(View view) {
        _nextViews.add(view);
    }

    public final void switchViews() {
        removeAllViews();
        _views.addAll(_nextViews);
        _nextViews.clear();
    }

    public final View addView(View view) {
        view.setParent(this);

        if (CollectionUtils.notContains(_views, view)) {
            _views.add(view);
        }

        ApplicationClient.uiManager.addView(view);

        onAddView(view);

        return this;
    }

    protected abstract void onAddView(View view);

    protected abstract void onRemoveView(View view);

    public boolean contains(int x, int y) {
        return (_finalX <= x && _finalX + _width >= x && _finalY <= y && _finalY + _height >= y);
    }

    public View setMargin(int top, int right, int bottom, int left) {
        _marginTop = (int) (top * Application.config.uiScale);
        _marginRight = (int) (right * Application.config.uiScale);
        _marginBottom = (int) (bottom * Application.config.uiScale);
        _marginLeft = (int) (left * Application.config.uiScale);
        return this;
    }

    public View setMargin(int top, int right) {
        return setMargin(top, right, top, right);
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

    public View setOnDragListener(UIEventManager.OnDragListener onDragListener) {
        _onDragListener = onDragListener;
        ApplicationClient.uiEventManager.setOnDragListener(this, _onDragListener);
        return this;
    }

    public View setOnClickListener(OnClickListener onClickListener) {
        assert onClickListener != null;
        _onClickListener = onClickListener;
        ApplicationClient.uiEventManager.setOnClickListener(this, onClickListener);
        return this;
    }

    // TODO: crash in lua throw on main thread
    public void setOnClickListener(LuaValue value) {
        _onClickListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        ApplicationClient.uiEventManager.setOnClickListener(this, _onClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnRightClickListener(LuaValue value) {
        _onRightClickListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        ApplicationClient.uiEventManager.setOnRightClickListener(this, _onRightClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelUpListener(LuaValue value) {
        _onMouseWheelUpListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        ApplicationClient.uiEventManager.setOnMouseWheelUpListener(this, _onMouseWheelUpListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelDownListener(LuaValue value) {
        _onMouseWheelDownListener = (GameEvent event) -> value.call(CoerceJavaToLua.coerce(this));
        ApplicationClient.uiEventManager.setOnMouseWheelDownListener(this, _onMouseWheelDownListener);
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
        ApplicationClient.uiEventManager.setOnFocusListener(this, _onFocusListener);
    }

    public void setOnRightClickListener(OnClickListener onClickListener) {
        assert onClickListener != null;
        _onRightClickListener = onClickListener;
        ApplicationClient.uiEventManager.setOnRightClickListener(this, onClickListener);
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        assert onFocusListener != null;
        _onFocusListener = onFocusListener;
        ApplicationClient.uiEventManager.setOnFocusListener(this, onFocusListener);
    }

    public void onClick(GameEvent event) {
        if (_onClickListener != null) {
            _onClickListener.onClick(event);
        }
    }

    public void setPadding(int t, int r, int b, int l) {
        _paddingTop = (int) (t * Application.config.uiScale);
        _paddingRight = (int) (r * Application.config.uiScale);
        _paddingBottom = (int) (b * Application.config.uiScale);
        _paddingLeft = (int) (l * Application.config.uiScale);
    }

    public View setPadding(int t, int r) {
        _paddingTop = _paddingBottom = (int) (t * Application.config.uiScale);
        _paddingRight = _paddingLeft = (int) (r * Application.config.uiScale);
        return this;
    }

    public View setPadding(int padding) {
        _paddingTop = _paddingBottom = _paddingRight = _paddingLeft = (int) (padding * Application.config.uiScale);
        return this;
    }

    public View setFixedSize(int width, int height) {
        _fixedWidth = (int) (width * Application.config.uiScale);
        _fixedHeight = (int) (height * Application.config.uiScale);
        return this;
    }

    public View setSize(int width, int height) {
        _width = (int) (width * Application.config.uiScale);
        _height = (int) (height * Application.config.uiScale);
        _originWidth = width;
        _originHeight = height;
        return this;
    }

    public View setWidth(int width) {
        _width = (int) (width * Application.config.uiScale);
        _originWidth = width;
        return this;
    }

    public View setHeight(int height) {
        _height = (int) (height * Application.config.uiScale);
        _originHeight = height;
        return this;
    }

    public View setPositionX(int x) {
        _x = (int) (x * Application.config.uiScale);
        return this;
    }

    public View setPositionY(int y) {
        _y = (int) (y * Application.config.uiScale);
        return this;
    }

    public View setPosition(int x, int y) {
        _x = (int) (x * Application.config.uiScale);
        _y = (int) (y * Application.config.uiScale);
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

    public View setData(Object data) {
        _data = data;
        return this;
    }

    protected void remove() {
        _parent = null;
        if (_onClickListener != null) {
            ApplicationClient.uiEventManager.removeOnClickListener(this);
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

        // Alignement par rapport au parent
        if (_parent != null) {
            if (_horizontalAlign == HorizontalAlign.CENTER) {
                return (_parent.getWidth() / 2) - (_width / 2) + _x;
            }
            if (_horizontalAlign == HorizontalAlign.RIGHT) {
                return _parent.getWidth() - _width - _x;
            }
        }

        // Alignement par rapport à l'écran
        else {
            if (_horizontalAlign == HorizontalAlign.CENTER) {
                return (ApplicationClient.gdxRenderer.getWidth() / 2) - (_width / 2) + _x;
            }
            if (_horizontalAlign == HorizontalAlign.RIGHT) {
                return ApplicationClient.gdxRenderer.getWidth() - _width - _x;
            }
        }

        return _x;
    }

    protected int getAlignedY() {

        // Alignement par rapport au parent
        if (_parent != null) {
            if (_verticalAlign == VerticalAlign.CENTER) {
                return (_parent.getHeight() / 2) - (_height / 2) + _y;
            }
            if (_verticalAlign == VerticalAlign.BOTTOM) {
                return _parent.getHeight() - _y;
            }
        }

        // Alignement par rapport à l'écran
        else {
            if (_verticalAlign == VerticalAlign.CENTER) {
                return (ApplicationClient.gdxRenderer.getHeight() / 2) - (_width / 2) + _y;
            }
            if (_verticalAlign == VerticalAlign.BOTTOM) {
                return ApplicationClient.gdxRenderer.getHeight() - _y;
            }
        }

        return _y;
    }

    @Override
    public String toString() { return _path; }
}
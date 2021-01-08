package org.smallbox.faraway.client.ui.engine.views.widgets;

import com.badlogic.gdx.graphics.Color;
import org.apache.commons.lang3.StringUtils;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.smallbox.faraway.client.FontManager;
import org.smallbox.faraway.client.RotateAnimation;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.OnFocusListener;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.UIAdapter;
import org.smallbox.faraway.core.config.Config;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.util.CollectionUtils;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class View implements Comparable<View> {
    protected final UIEventManager uiEventManager;
    protected final UIManager uiManager;
    protected final SpriteManager spriteManager;
    protected final GDXRenderer gdxRenderer;
    protected final FontManager fontManager;
    protected final Data applicationData;

    protected int _originWidth;
    protected int _originHeight;
    private String _group;
    private String _path;
    private int _index;
    private boolean _sorted;
    private Color _borderColor;
    private LuaController _controller;
    private boolean _isGameView;

    public View(ModuleBase module) {
        // Inject all dependency to view once for all, waiting for a clever solution
        uiEventManager = DependencyInjector.getInstance().getDependency(UIEventManager.class);
        uiManager = DependencyInjector.getInstance().getDependency(UIManager.class);
        spriteManager = DependencyInjector.getInstance().getDependency(SpriteManager.class);
        gdxRenderer = DependencyInjector.getInstance().getDependency(GDXRenderer.class);
        applicationData = DependencyInjector.getInstance().getDependency(Data.class);
        fontManager = DependencyInjector.getInstance().getDependency(FontManager.class);

        _module = module;
        _isVisible = true;
        _borderSize = 2;
        _x = 0;
        _y = 0;
    }

    // TODO: use value from application config
    protected float uiScale = 1;

    public void setAlign(VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
        _verticalAlign = verticalAlign;
        _horizontalAlign = horizontalAlign;
    }

    public final void removeAllViews() {
        _views.forEach(view -> {
            uiManager.removeView(view);
            uiEventManager.removeListeners(view);
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
        _borderColor = color == 0 ? null : ColorUtils.fromHex(color);
        return this;
    }

    public View setBorderColor(Color color) {
        _borderColor = color;
        return this;
    }

    public void setController(LuaController controller) {
        _controller = controller;
    }

    public LuaController getController() {
        return _controller;
    }

    public boolean hasClickListener() {
        return _onClickListener != null;
    }

    public boolean isGameView() {
        return _isGameView;
    }

    public void setGameView(boolean isGameView) {
        _isGameView = isGameView;
    }

    public View createFromTemplate() {
        return _template != null ? _template.createFromTemplate() : null;
    }

    public enum HorizontalAlign {LEFT, RIGHT, CENTER}
    public enum VerticalAlign {TOP, BOTTOM, CENTER}

    protected RotateAnimation _animation;
    protected boolean _focusable;
    private long _regularBackground;
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

    public void click(int x, int y) {
        assert _onClickListener != null;
        _onClickListener.onClick(x, y);

        if (_parent != null && _parent instanceof UIDropDown) {
            ((UIDropDown)_parent).setCurrent(this);
        }
    }

    public enum Align { CENTER, LEFT, CENTER_VERTICAL, RIGHT }

    protected final ModuleBase  _module;

    public interface TemplateCallback {
        View createFromTemplate();
    }

//    protected Set<View>         _views = new ConcurrentSkipListSet<>((o1, o2) -> Integer.compare(o1.getIndex(), o2.getIndex()));
    protected Collection<View>  _views = new LinkedBlockingQueue<>();
    protected TemplateCallback  _template;
    protected Collection<View>  _nextViews = new ConcurrentLinkedQueue<>();
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
    protected String            _id;
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

    public boolean      isFocus() { return _isFocus; }
    public boolean      isVisible() { return _isVisible && (_parent == null || _parent.isVisible()); }
    //    public boolean      isVisible() { return _isVisible; }
    public boolean      isActive() { return _isActive; }
    public boolean      inGame() { return _inGame; }

    public View         setId(String id) { _id = id; return this; }
    public View         setTextAlign(Align align) { _align = align; return this; }
    public View         setTextAlign(String align) { _align = View.Align.valueOf(StringUtils.upperCase(align)); return this; }
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
    public View         setBackgroundColor(long color) { _backgroundColor = ColorUtils.fromHex(color); return this; }
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
    public View         setRegularBackgroundColor(long regularBackground) { _regularBackground = regularBackground; return this; }
    public View         setFocusBackgroundColor(int focusBackground) { _focusBackground = focusBackground; return this; }
    public void         setActionName(String actionName) { _actionName = actionName; }
    public void         setLayer(int layer) { _layer = layer; }
    public Color        getBackgroundColor() { return _backgroundColor; }
    public int          getLayer() { return _layer; }
    public View         getParent() { return _parent; }
    public String       getId() { return _id; }
    public int          getPosX() { return _x; }
    public int          getPosY() { return _y; }
    public int          getFinalX() { return _finalX; }
    public int          getFinalY() { return _finalY; }
    public int          getDeep() { return _deep; }
    public int          getLevel() { return _level; }
    public long         getRegularBackground() { return _regularBackground; }
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
                renderer.drawPixelUI(_finalX, _finalY, _width, _height, _backgroundFocusColor);
            }

            else if (_backgroundColor != null) {
                renderer.drawPixelUI(_finalX, _finalY, _width, _height, _backgroundColor);
            }

            if (_borderColor != null) {
                renderer.drawRectangleUI(_finalX, _finalY, _width, _height, _borderColor, false);
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
                renderer.drawTextUI(getAlignedX() + x + _offsetX + _paddingLeft + _marginLeft, getAlignedY() + y + _offsetY + _paddingTop + _marginTop, 12, com.badlogic.gdx.graphics.Color.CYAN, _name);
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
        _nextViews.forEach(this::addView);
        _nextViews.clear();

        if (_parent != null) {
            _parent.updateSize();
        }
    }

    protected void updateSize() {
    }

    public final View addView(View view) {
        view.setParent(this);

        if (CollectionUtils.notContains(_views, view)) {
            _views.add(view);
        }

        uiManager.addView(view);

        onAddView(view);

        return this;
    }

    public final View setTemplate(TemplateCallback templateCallback) {
        _template = templateCallback;
        return this;
    }

    protected abstract void onAddView(View view);

    protected abstract void onRemoveView(View view);

    public boolean contains(int x, int y) {
        return (_finalX <= x && _finalX + _width >= x && _finalY <= y && _finalY + _height >= y);
    }

    public View setMargin(int top, int right, int bottom, int left) {
        _marginTop = (int) (top * uiScale);
        _marginRight = (int) (right * uiScale);
        _marginBottom = (int) (bottom * uiScale);
        _marginLeft = (int) (left * uiScale);
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
        _backgroundFocusColor = ColorUtils.fromHex(color);
        return this;
    }

    public View setBackgroundFocusColor(Color color) {
        _backgroundFocusColor = color;
        return this;
    }

    public View setOnDragListener(UIEventManager.OnDragListener onDragListener) {
        _onDragListener = onDragListener;
        uiEventManager.setOnDragListener(this, _onDragListener);
        return this;
    }

    public View setOnClickListener(OnClickListener onClickListener) {
//        if (_path != null && _path.contains("game_menu.pause")) {
            _onClickListener = onClickListener;
            uiEventManager.setOnClickListener(this, onClickListener);
//        }
        return this;
    }

    public View setOnClickListener2(OnClickListener onClickListener) {
        _onClickListener = onClickListener;
        return this;
    }

    // TODO: crash in lua throw on main thread
    public void setOnClickListener(LuaValue value) {
        _onClickListener = (int x, int y) -> value.call(CoerceJavaToLua.coerce(this));
        uiEventManager.setOnClickListener(this, _onClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnRightClickListener(LuaValue value) {
        _onRightClickListener = (int x, int y) -> value.call(CoerceJavaToLua.coerce(this));
        uiEventManager.setOnRightClickListener(this, _onRightClickListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelUpListener(LuaValue value) {
        _onMouseWheelUpListener = (int x, int y) -> value.call(CoerceJavaToLua.coerce(this));
        uiEventManager.setOnMouseWheelUpListener(this, _onMouseWheelUpListener);
    }

    // TODO: crash in lua throw on main thread
    public void setOnMouseWheelDownListener(LuaValue value) {
        _onMouseWheelDownListener = (int x, int y) -> value.call(CoerceJavaToLua.coerce(this));
        uiEventManager.setOnMouseWheelDownListener(this, _onMouseWheelDownListener);
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
        uiEventManager.setOnFocusListener(this, _onFocusListener);
    }

    public void setOnRightClickListener(OnClickListener onClickListener) {
        assert onClickListener != null;
        _onRightClickListener = onClickListener;
        uiEventManager.setOnRightClickListener(this, onClickListener);
    }

    public void setOnFocusListener(OnFocusListener onFocusListener) {
        assert onFocusListener != null;
        _onFocusListener = onFocusListener;
        uiEventManager.setOnFocusListener(this, onFocusListener);
    }

    public void setPadding(int t, int r, int b, int l) {
        _paddingTop = (int) (t * uiScale);
        _paddingRight = (int) (r * uiScale);
        _paddingBottom = (int) (b * uiScale);
        _paddingLeft = (int) (l * uiScale);
    }

    public View setPadding(int t, int r) {
        _paddingTop = _paddingBottom = (int) (t * uiScale);
        _paddingRight = _paddingLeft = (int) (r * uiScale);
        return this;
    }

    public View setPadding(int padding) {
        _paddingTop = _paddingBottom = _paddingRight = _paddingLeft = (int) (padding * uiScale);
        return this;
    }

    public View setFixedSize(int width, int height) {
        _fixedWidth = (int) (width * uiScale);
        _fixedHeight = (int) (height * uiScale);
        return this;
    }

    public View setSize(int width, int height) {
        _width = (int) (width * uiScale);
        _height = (int) (height * uiScale);
        _originWidth = width;
        _originHeight = height;
        return this;
    }

    public View setWidth(int width) {
        _width = (int) (width * uiScale);
        _originWidth = width;
        return this;
    }

    public View setHeight(int height) {
        _height = (int) (height * uiScale);
        _originHeight = height;
        return this;
    }

    public View setPositionX(int x) {
        _x = (int) (x * uiScale);
        return this;
    }

    public View setPositionY(int y) {
        _y = (int) (y * uiScale);
        return this;
    }

    public View setPosition(int x, int y) {
        _x = (int) (x * uiScale);
        _y = (int) (y * uiScale);
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
            uiEventManager.removeOnClickListener(this);
        }
    }

    public abstract int getContentWidth();
    public abstract int getContentHeight();
    public void init(){}

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

    public View findById(String resId) {
        for (View view: _views) {
            if (StringUtils.equals(view._id, resId)) {
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
                return (gdxRenderer.getWidth() / 2) - (_width / 2) + _x;
            }
            if (_horizontalAlign == HorizontalAlign.RIGHT) {
                return gdxRenderer.getWidth() - _width - _x;
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
                return (gdxRenderer.getHeight() / 2) - (_width / 2) + _y;
            }
            if (_verticalAlign == VerticalAlign.BOTTOM) {
                return gdxRenderer.getHeight() - _y;
            }
        }

        return _y;
    }

    @Override
    public String toString() { return _path; }
}
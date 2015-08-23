package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.renderer.GDXRenderer;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.OnFocusListener;
import org.smallbox.faraway.ui.engine.UIEventManager;

import java.awt.*;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class View {
    private String      _name;
    protected boolean   _isAlignLeft = true;
    protected boolean   _isAlignTop = true;
    protected int       _finalX;
    protected int       _finalY;

    public void setName(String name) {
        _name = name;
    }

    public void setAlign(boolean isAlignLeft, boolean isAlignTop) {
        _isAlignLeft = isAlignLeft;
        _isAlignTop = isAlignTop;
    }

    public enum Align { CENTER, LEFT, CENTER_VERTICAL, RIGHT };

    protected int               _width;
    protected int               _height;
    public int                  _x;
    public int                  _y;
    protected boolean			_isVisible;
    protected Rectangle 		_rect;
    protected int 				_paddingLeft;
    protected int				_paddingBottom;
    protected int 				_paddingRight;
    protected int 				_paddingTop;
    public FrameLayout          _parent;
    protected OnClickListener _onClickListener;
    private OnClickListener     _onRightClickListener;
    protected OnFocusListener _onFocusListener;
    protected boolean 			_isFocus;
    protected int 				_id;
    protected int 				_borderSize;
    protected boolean 			_invalid;
    protected Object 			_data;
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

    public boolean 		isFocus() { return _isFocus; }
    public boolean 		isVisible() { return _isVisible; }

    public void 		setId(int id) { _id = id; }
    public void         setAlign(Align align) { _align = align; }
    public void 		setFocus(boolean focus) { _isFocus = focus; }
    public void 		setParent(FrameLayout parent) {
        _parent = parent;
    }

    public FrameLayout 	getParent() { return _parent; }
    public int 			getId() { return _id; }
    public int 			getPosX() { return _x; }
    public int 			getPosY() { return _y; }

    protected abstract void onDraw(GDXRenderer renderer, Viewport viewport);
    public abstract void draw(GDXRenderer renderer, Viewport viewport);
    public abstract void draw(GDXRenderer renderer, int x, int y);
    public abstract void refresh();

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

    public Rectangle getRect() {
        if (_rect == null) {
            _rect = computeRect();
        }
        return _rect;
    }

    public void resetPos() {
        _rect = computeRect();
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

    public void setSize(int width, int height) {
        _width = (int) (width * GameData.config.uiScale);
        _height = (int) (height * GameData.config.uiScale);
        _invalid = true;
    }

    public void setPosition(int x, int y) {
        _x = (int) (x * GameData.config.uiScale) + (_isAlignLeft ? 0 : GameData.config.screen.resolution[0]);
        _y = (int) (y * GameData.config.uiScale) + (_isAlignTop ? 0 : GameData.config.screen.resolution[1]);

        _invalid = true;
    }

    protected Rectangle computeRect() {
        _finalX = 0;
        _finalY = 0;
        View view = this;
        while (view != null) {
            _finalX += view.getPosX() + view.getOffsetX();
            _finalY += view.getPosY() + view.getOffsetY();
            view = view.getParent();
        }
        return new Rectangle(_finalX, _finalY, _width == 0 ? getContentWidth() : _width, _height == 0 ? getContentHeight() : _height);
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
    public View findById(String id){return null;}
    public void resetAllPos() {
        resetPos();
    }
}

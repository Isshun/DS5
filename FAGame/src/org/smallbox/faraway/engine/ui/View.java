package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.*;

import java.awt.*;

/**
 * Created by Alex on 27/05/2015.
 */
public abstract class View {
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
    protected OnClickListener   _onClickListener;
    protected OnFocusListener   _onFocusListener;
    protected boolean 			_isFocus;
    protected int 				_id;
    protected int 				_borderSize;
    protected boolean 			_invalid;
    protected Object 			_data;
    private ColorView           _background;

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
    public void 		setFocus(boolean focus) { _isFocus = focus; }
    public void 		setParent(FrameLayout parent) { _parent = parent; }

    public FrameLayout 	getParent() { return _parent; }
    public int 			getId() { return _id; }
    public int 			getPosX() { return _x; }
    public int 			getPosY() { return _y; }

    protected abstract void onDraw(GFXRenderer renderer, RenderEffect effect);
    public abstract void draw(GFXRenderer renderer, RenderEffect effect);
    public abstract void refresh();

    public void setBackgroundColor(org.smallbox.faraway.Color color) {
        _background = ViewFactory.getInstance().createColorView(_width, _height);
        _background.setBackgroundColor(color);
    }

    public void setBorderColor(org.smallbox.faraway.Color color) {
    }

    public void setSize(int width, int height) {
        _width = width;
        _height = height;
        _invalid = true;
    }

    public void setVisible(boolean visible) {
        _isVisible = visible;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        _onClickListener = onClickListener;
        UIEventManager.getInstance().setOnClickListener(this, onClickListener);
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

    public void setPosition(int x, int y) {
        _x = x;
        _y = y;
        _invalid = true;
    }

    protected Rectangle computeRect() {
        int x = 0;
        int y = 0;
        View view = this;
        while (view != null) {
            x += view.getPosX();
            y += view.getPosY();
            view = view.getParent();
        }
        return new Rectangle(x, y, _width, _height);
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

}

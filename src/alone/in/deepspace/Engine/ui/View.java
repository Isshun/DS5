package alone.in.deepspace.engine.ui;

import java.awt.Rectangle;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.renderer.MainRenderer;
import alone.in.deepspace.manager.UIEventManager;

public abstract class View {
	protected Vector2f 		_pos;
	protected Vector2f 		_size;
	protected boolean		_isVisible;
	protected Rectangle 	_rect;
	protected int 			_posX;
	protected int 			_posY;
	private RectangleShape	_background;
	private RectangleShape	_borders[];
	protected int 			_paddingLeft;
	protected int			_paddingBottom;
	protected int 			_paddingRight;
	protected int 			_paddingTop;
	protected FrameLayout 	_parent;
	private OnClickListener _onClickListener;
	private OnFocusListener _onFocusListener;
	private boolean 		_isFocus;
	private int 			_id;
	private int 			_borderSize;
	private boolean 		_invalid;
	private Color 			_borderColor;
	private Color _backgroundColor;

	public View(Vector2f size) {
		_size = size;
		_isVisible = true;
		_borderSize = 2;
		_pos = new Vector2f(0, 0);

		onCreate();
	}
	
	protected abstract void onCreate();
	protected abstract void onDraw(RenderWindow app, RenderStates render);

	public boolean 		isFocus() { return _isFocus; }
	public boolean 		isVisible() { return _isVisible; }

	public void 		setId(int id) { _id = id; }
	public void 		setFocus(boolean focus) { _isFocus = focus; }
	public void 		setParent(FrameLayout parent) { _parent = parent; }

	public FrameLayout 	getParent() { return _parent; }
	public int 			getId() { return _id; }
	public int 			getPosX() { return _posX; }
	public int 			getPosY() { return _posY; }

	public void draw(RenderWindow app, RenderStates render) {
		if (_isVisible == false) {
			return;
		}

		if (_invalid) {
			refresh();
		}
		
		if (_background != null) {
			app.draw(_background, render);
		}
		
		onDraw(app, render);

		
		// Borders
		if (_borders != null) {
			app.draw(_borders[0], render);
			app.draw(_borders[2], render);
			app.draw(_borders[1], render);
			app.draw(_borders[3], render);
		}
	}

	private void refresh() {
		// Background
		if (_backgroundColor != null && _size != null && _pos != null) {
			_background = new RectangleShape();
			_background.setSize(_size);
			_background.setPosition(_pos);
			_background.setFillColor(_backgroundColor);
		} else {
			_background = null;
		}
	
		// Border
		if (_borderColor != null && _size != null && _pos != null) {
			_borders = new RectangleShape[4];
			_borders[0] = new RectangleShape();
			_borders[0].setPosition(_pos);
			_borders[0].setSize(new Vector2f(_size.x, _borderSize));
			_borders[0].setFillColor(_borderColor);

			_borders[1] = new RectangleShape();
			_borders[1].setPosition(_pos.x, _pos.y + _size.y);
			_borders[1].setSize(new Vector2f(_size.x, _borderSize));
			_borders[1].setFillColor(_borderColor);

			_borders[2] = new RectangleShape();
			_borders[2].setPosition(_pos);
			_borders[2].setSize(new Vector2f(_borderSize, _size.y));
			_borders[2].setFillColor(_borderColor);

			_borders[3] = new RectangleShape();
			_borders[3].setPosition(_pos.x + _size.x - _borderSize, _pos.y);
			_borders[3].setSize(new Vector2f(_borderSize, _size.y));
			_borders[3].setFillColor(_borderColor);
		} else {
			_borders = null;
		}

		_invalid = false;
	}

	public void setSize(Vector2f size) {
		_size = size;
		_invalid = true;
	}
	
	public void setVisible(boolean visible) {
		_isVisible = visible;
	}
	
	public void setOnClickListener(OnClickListener onClickListener) {
		if (_onClickListener != null) {
			UIEventManager.getInstance().removeOnClickListener(_onClickListener);
		}
		_onClickListener = onClickListener;
		UIEventManager.getInstance().setOnClickListener(this, onClickListener);
	}

	public void setOnFocusListener(OnFocusListener onFocusListener) {
		if (_onFocusListener != null) {
			UIEventManager.getInstance().removeOnFocusListener(_onFocusListener );
		}
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

	// TODO
	public void resetPos() {
		_rect = null;
	}

	public void setBackgroundColor(Color color) {
		_backgroundColor = color;
		_invalid = true;
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

	public void setPosition(Vector2f pos) {
		if (pos != null) {
			_pos = pos;
			_posX = (int) pos.x;
			_posY = (int) pos.y;
		}
		_invalid = true;
	}

	private Rectangle computeRect() {
		int x = 0;
		int y = 0;
		View view = this;
		while (view != null) {
			x += view.getPosX();
			y += view.getPosY();
			view = view.getParent();
		}
		return new Rectangle(x, y, (int)(_size != null ? _size.x : 0), (int)(_size != null ? _size.y : 0));
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
	
	public void setBorderColor(Color color) {
		_borderColor = color;
		_invalid = true;
	}

	public void setBorderSize(int borderSize) {
		_borderSize = borderSize;
		_invalid = true;
	}

}

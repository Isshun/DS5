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
//	protected int 			_parentPosX;
//	protected int 			_parentPosY;
	protected int 			_posX;
	protected int 			_posY;
	private RectangleShape	_background;
	protected int 			_paddingLeft;
	protected int			_paddingBottom;
	protected int 			_paddingRight;
	protected int 			_paddingTop;
	protected FrameLayout 	_parent;
	private OnClickListener _onClickListener;
	private OnFocusListener _onFocusListener;
	private boolean 		_isFocus;
	private int _id;

	public View(Vector2f size) {
		_size = size;
		_isVisible = true;
		_pos = new Vector2f(0, 0);

		onCreate();
	}
	
	protected abstract void onCreate();
	protected abstract void onDraw(RenderWindow app, RenderStates render);

	public void draw(RenderWindow app, RenderStates render) {
		if (_isVisible == false) {
			return;
		}
		
		if (_background != null) {
			MainRenderer.getInstance().draw(_background, render);
		}
		
		onDraw(app, render);
	}

	public void setSize(Vector2f size) {
		_size = size;
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
		if (color == null) {
			_background = null;
			return;
		}
		
		if (_background == null) {
			_background = new RectangleShape();
		}
		if (_size != null) {
			_background.setSize(_size);
		}
		if (_pos != null) {
			_background.setPosition(_pos);
		}
		_background.setFillColor(color);
	}

//	public void setParentPosition(int x, int y) {
//		if (x != _parentPosX || y != _parentPosY) {
//			_parentPosX = x;
//			_parentPosY = y;
//			_rect = new Rectangle(_parentPosX + _posX, _parentPosY + _posY, (int)(_size != null ? _size.x : 0), (int)(_size != null ? _size.y : 0));
//		}
//	}
	
	public void setPadding(int t, int r, int b, int l) {
		_paddingTop = t;
		_paddingRight = r;
		_paddingBottom = b;
		_paddingLeft = l;
	}

	public void setPadding(int t, int r) {
		_paddingTop = t;
		_paddingRight = r;
		_paddingBottom = t;
		_paddingLeft = r;
	}

	public void setPosition(Vector2f pos) {
		if (pos != null && (pos.x != _posX || pos.y != _posY)) {
			_pos = pos;
			_posX = (int) pos.x;
			_posY = (int) pos.y;
		}
		if (_background != null) {
			_background.setPosition(pos);
		}
	}

	public void setParent(FrameLayout parent) {
		_parent = parent;
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

	public FrameLayout getParent() {
		return _parent;
	}

	public boolean isFocus() {
		return _isFocus;
	}

	public void setFocus(boolean focus) {
		_isFocus = focus;
	}

	public boolean isVisible() {
		return _isVisible;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public int getPosX() {
		return _posX;
	}

	public int getPosY() {
		return _posY;
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
}

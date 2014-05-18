package alone.in.deepspace.Engine.ui;

import java.awt.Rectangle;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Engine.renderer.MainRenderer;
import alone.in.deepspace.UserInterface.EventManager;
import alone.in.deepspace.UserInterface.OnFocusListener;

public abstract class View {
	protected Vector2f 		_pos;
	private Vector2f 		_size;
	protected boolean		_isVisible;
	private Rectangle 		_rect;
	private int 			_parentPosX;
	private int 			_parentPosY;
	protected int 			_posX;
	protected int 			_posY;
	private RectangleShape	_background;
	protected int 			_paddingLeft;
	protected int			_paddingBottom;
	protected int 			_paddingRight;
	protected int 			_paddingTop;
	private RectangleView _parent;
	private OnClickListener _onClickListener;
	private OnFocusListener _onFocusListener;
	private boolean mIsActive;

	public View(Vector2f size) {
		_size = size;
		_isVisible = true;
	}
	
	public void refresh(RenderWindow app, RenderStates render) {
		if (_isVisible == false) {
			return;
		}
		
		if (_background != null) {
			MainRenderer.getInstance().draw(_background, render);
		}
		
		onRefresh(app, render);
	}

	protected void setSize(Vector2f size) {
		_size = size;		
	}
	
	public void setVisible(boolean visible) {
		_isVisible = visible;
	}
	
	public void setOnClickListener(OnClickListener onClickListener) {
		_onClickListener = onClickListener;
		EventManager.getInstance().setOnClickListener(this, onClickListener);
	}

	public void setOnFocusListener(OnFocusListener onFocusListener) {
		_onFocusListener = onFocusListener;
		EventManager.getInstance().setOnFocusListener(this, onFocusListener);
	}

	public void click() {
		if (_onClickListener != null) {
			_onClickListener.onClick(this);
		}
	}

	public Rectangle getRect() {
		return _rect;
	}

	public void setBackgroundColor(Color color) {
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

	public void setParentPosition(int x, int y) {
		if (x != _parentPosX || y != _parentPosY) {
			_parentPosX = x;
			_parentPosY = y;
			_rect = new Rectangle(_parentPosX + _posX, _parentPosY + _posY, (int)(_size != null ? _size.x : 0), (int)(_size != null ? _size.y : 0));
		}
	}
	
	public void setPadding(int t, int r, int b, int l) {
		_paddingTop = t;
		_paddingRight = r;
		_paddingBottom = b;
		_paddingLeft = l;
//		if (_background != null && _pos != null && _size != null) {
//			_background.setSize(new Vector2f(_size.x + _paddingLeft + _paddingRight, _size.y + _paddingTop + _paddingBottom));
//		}
	}

	public void setPosition(Vector2f pos) {
		if (pos != null && (pos.x != _posX || pos.y != _posY)) {
			_pos = pos;
			_posX = (int) pos.x;
			_posY = (int) pos.y;
			_rect = new Rectangle(_parentPosX + _posX, _parentPosY + _posY, (int)(_size != null ? _size.x : 0), (int)(_size != null ? _size.y : 0));
		}
		if (_background != null) {
			_background.setPosition(_pos);
		}
	}

	public void onRefresh(RenderWindow app, RenderStates states) {
	}

	public void setParent(RectangleView parent) {
		_parent = parent;
	}

	public RectangleView getParent() {
		return _parent;
	}

	public boolean isActive() {
		return mIsActive;
	}

	public void setActive(boolean active) {
		mIsActive = active;
	}

}

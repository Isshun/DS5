package alone.in.DeepSpace.UserInterface.Utils;

import java.awt.Rectangle;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.system.Vector2f;

import alone.in.DeepSpace.UserInterface.EventManager;

public abstract class UIView {
	private Vector2f 		_pos;
	private Vector2f 		_size;
	protected boolean		_isVisible;
	private Rectangle 		_rect;
	private int _parentPosX;
	private int _parentPosY;
	private int _posX;
	private int _posY;

	public UIView(Vector2f size) {
		_size = size;
		_isVisible = true;
	}
	
	public abstract void refresh(RenderStates _render);
	
	public void setVisible(boolean visible) {
		_isVisible = visible;
	}
	
	public void setOnClickListener(OnClickListener onClickListener) {
		EventManager.getInstance().setOnClickListener(this, onClickListener);
	}

	public Rectangle getRect() {
		return _rect;
	}

	public void setParentPosition(int x, int y) {
		if (x != _parentPosX || y != _parentPosY) {
			_parentPosX = x;
			_parentPosY = y;
			_rect = new Rectangle(_parentPosX + _posX, _parentPosY + _posY, (int)_size.x, (int)_size.y);
		}
	}

	public void setPosition(Vector2f pos) {
		if (pos != null && (pos.x != _posX || pos.y != _posY)) {
			_pos = pos;
			_posX = (int) pos.x;
			_posY = (int) pos.y;
			_rect = new Rectangle(_parentPosX + _posX, _parentPosY + _posY, (int)_size.x, (int)_size.y);
		}
	}

}

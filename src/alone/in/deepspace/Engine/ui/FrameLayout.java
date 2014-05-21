package alone.in.deepspace.engine.ui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Utils.Log;

public class FrameLayout extends View {
	private List<View> 		_views;
	protected RenderStates 	_render;

	public FrameLayout(Vector2f size) {
		super(size);
	}

	@Override
	protected void onCreate() {
		_views = new ArrayList<View>();
		
		setPosition(0, 0);
	}

	private void createRender() {
		int posX = _posX;
		int posY = _posY;
		
		View parent = _parent;
		while (parent != null) {
			posX += parent._posX;
			posY += parent._posY;
			parent = parent._parent;
		}
		
		Transform transform = new Transform();
	    transform = Transform.translate(transform, posX, posY);
	    _render = new RenderStates(transform);
	}

	public void addView(View view) {
		if (this.equals(view)) {
			Log.error("FrameLayout: try to add itself to childrens");
			return;
		}
		
		view.setParentPosition(_posX, _posY);
		view.setParent(this);
		_views.add(view);
	}

	public void clearAllViews() {
		_views.clear();
	}

	public void removeView(View view) {
		view.setParentPosition(_posX, _posY);
		_views.remove(view);
	}

	public void setPosition(int x, int y) {
		_posX = x;
		_posY = y;
		_rect = new Rectangle(_parentPosX + _posX, _parentPosY + _posY, (int)(_size != null ? _size.x : 0), (int)(_size != null ? _size.y : 0));
		for (View view: _views) {
			view.setParentPosition(x, y);
		}
	}

	// TODO
	public void setPosition(Vector2f pos) {
		setPosition((int)pos.x, (int)pos.y);
	}

	@Override
	public void refresh(RenderWindow app, RenderStates render) {
		if (_isVisible == false) {
			return;
		}
		
		if (_render == null) {
			createRender();
		}

		super.refresh(app, _render);

		for (View view: _views) {
			view.refresh(app, _render);
		}
		
		onRefresh(app);
	}
	
	public void setVisible(boolean visible) {
		_isVisible = visible;
	}

	public void onRefresh(RenderWindow app) {
	}

	public boolean getVisible() {
		return _isVisible;
	}

}

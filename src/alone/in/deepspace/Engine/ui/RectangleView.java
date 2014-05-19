package alone.in.deepspace.engine.ui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.renderer.MainRenderer;

public abstract class RectangleView extends View {
	protected RenderStates 	_render;
	private RectangleShape 	_background;
	private List<View> 		_views;

	public RectangleView(Vector2f size) {
		super(size);
	
		_views = new ArrayList<View>();
		
		setPosition(0, 0);
	}

	private void createRender() {
		Transform transform = new Transform();
	    transform = Transform.translate(transform, _posX + _parentPosX, _posY + _parentPosY);
	    _render = new RenderStates(transform);
	}
	
	public void setBackgroundColor(Color color) {
		if (_background == null) {
			_background = new RectangleShape();
			_background.setSize(_size);
			_background.setFillColor(color);
		}
	}

	public void addView(View view) {
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
		for (View view: _views) {
			view.setParentPosition(x, y);
		}
	}
	
	public void refresh(RenderWindow app) {
		if (_isVisible == false) {
			return;
		}
		
		if (_render == null) {
			createRender();
		}
		
		if (_background != null) {
			MainRenderer.getInstance().draw(_background, _render);
		}

		for (View view: _views) {
			view.refresh(app, _render);
		}
		
		onRefresh(app);
	}
	
	public void setVisible(boolean visible) {
		_isVisible = visible;
	}

	public abstract void onRefresh(RenderWindow app);

	public boolean getVisible() {
		return _isVisible;
	}

}

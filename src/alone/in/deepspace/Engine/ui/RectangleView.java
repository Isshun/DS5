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

public abstract class RectangleView {
	protected RenderStates 	_render;
	private RectangleShape 	_background;
	private int _posX;
	private int _posY;

	private List<View> _views;

	protected boolean _isVisible;

	public RectangleView(Vector2f size) {
		_views = new ArrayList<View>();
		_background = new RectangleShape();
		_background.setSize(size);
	}
	
	public void setPosition(Vector2f pos) {
		_posX = (int) pos.x;
		_posY = (int) pos.y;
		Transform transform = new Transform();
	    transform = Transform.translate(transform, pos);
	    _render = new RenderStates(transform);
	}
	
	public void setBackgroundColor(Color color) {
		_background.setFillColor(color);
	}

	public void addView(View view) {
		view.setParentPosition(_posX, _posY);
		view.setParent(this);
		_views.add(view);
	}

	public void removeView(View view) {
		view.setParentPosition(_posX, _posY);
		_views.remove(view);
	}

	public void setPosition(int x, int y) {
		for (View view: _views) {
			view.setParentPosition(x, y);
		}
	}
	
	public void refresh(RenderWindow app) {
		if (_isVisible == false) {
			return;
		}
		
		MainRenderer.getInstance().draw(_background, _render);

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

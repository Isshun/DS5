package alone.in.deepspace.engine.ui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.util.Log;

public class FrameLayout extends View {
	private List<View> 		_views;
	protected RenderStates 	_render;

	public FrameLayout(Vector2f size) {
		super(size);
		init();
	}

	public FrameLayout() {
		super(new Vector2f(0, 0));
		init();
	}

	private void init() {
		_views = new ArrayList<View>();
		setPosition(0, 0);
	}

	@Override
	protected void onDraw(RenderWindow app, RenderStates render) {
	}

	/**
	 * FrameLayout have there own draw() method because render parameter
	 * is RenderStates for root element View and don't contains the right
	 * transformation for sub elements.
	 */
	@Override
	public void draw(RenderWindow app, RenderStates render) {
		if (_isVisible == false) {
			return;
		}
		
		if (_render == null) {
			createRender();
		}

		super.draw(app, _render);

		for (View view: _views) {
			view.draw(app, _render);
		}
		
		onDraw(app, _render);
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
		
//		view.setParentPosition(_posX, _posY);
		view.setParent(this);
		_views.add(view);
	}

	public void clearAllViews() {
		for (View view: _views) {
			view.remove();
		}
		_views.clear();
	}

	public void removeView(View view) {
		view.remove();
		_views.remove(view);
	}

	public void setPosition(int x, int y) {
		_posX = x;
		_posY = y;
		
		_render = null;
//		for (View view: _views) {
//			view.setPosition(view.getPosX() + x, y);
//		}
	}

	// TODO
	public void setPosition(Vector2f pos) {
		setPosition((int)pos.x, (int)pos.y);
	}
	
	public void setVisible(boolean visible) {
		_isVisible = visible;
	}

	public boolean getVisible() {
		return _isVisible;
	}

}

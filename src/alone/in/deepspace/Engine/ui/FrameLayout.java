package alone.in.deepspace.engine.ui;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Transform;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.renderer.MainRenderer;

public class FrameLayout extends View {
	private List<View> 		_views;

	public FrameLayout(Vector2f size) {
		super(size);
	}

	@Override
	protected void onCreate() {
		_views = new ArrayList<View>();
		
		setPosition(0, 0);
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
	
	@Override
	public void refresh(RenderWindow app, RenderStates render) {
		if (_isVisible == false) {
			return;
		}
		
		if (_render == null) {
			createRender();
		}

		super.refresh(app, _render);
		
//		if (_background != null) {
//			MainRenderer.getInstance().draw(_background, _render);
//		}
//
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

package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.renderer.GDXRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class FrameLayout extends View {
	public FrameLayout(int width, int height) {
		super(width, height);
	}

	public FrameLayout() {
		super(-1, -1);
	}

    @Override
	public void init() {
		if (_align == Align.CENTER && _parent != null) {
			_offsetX = (_parent.getContentWidth() - _width) / 2;
			_offsetY = (_parent.getContentHeight() - _height) / 2;
		}

        _views.forEach(View::init);
	}

	public void addView(View view) {
		if (this.equals(view)) {
//			Log.error("FrameLayout: try to add itself to childrens");
			return;
		}
		
		view.setParent(this);
		_views.add(view);
		view.resetAllPos();
	}

	@Override
	protected void remove() {
		super.remove();
		for (View view: _views) {
			view.remove();
		}
		_views.clear();
	}

    public void removeAllViews() {
        _views.forEach(View::remove);
		_views.clear();
	}

	public void removeView(View view) {
		view.remove();
		_views.remove(view);
	}

	public void resetAllPos() {
		for (View view: _views) {
			if (view instanceof FrameLayout) {
				((FrameLayout) view).resetAllPos();
			} else {
				view.resetPos();
			}
		}
	}

    public List<View> getViews() {
        return _views;
    }


}

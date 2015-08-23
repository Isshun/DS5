package org.smallbox.faraway.ui.engine.view;

import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.renderer.GDXRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class FrameLayout extends View {
    protected List<View> 		_views = new ArrayList<>();;

	public FrameLayout(int width, int height) {
		super(width, height);
	}

	public FrameLayout() {
		super(0, 0);
	}

    @Override
	public void init() {
		if (_align == Align.CENTER && _parent != null) {
			_offsetX = (_parent.getContentWidth() - _width) / 2;
			_offsetY = (_parent.getContentHeight() - _height) / 2;
		}

        _views.forEach(View::init);
	}

	@Override
	protected void onDraw(GDXRenderer renderer, Viewport viewport) {
	}

	@Override
	public void draw(GDXRenderer renderer, int x, int y) {
		if (!_isVisible) {
			return;
		}

		if (_backgroundColor != null) {
			renderer.draw(_backgroundColor, _x, _y, _width, _height);
		}

		for (View view: _views) {
			view.draw(renderer, null);
		}

		onDraw(renderer, null);
	}

	/**
	 * FrameLayout have there own draw() method because _renderEffect parameter
	 * is RenderStates for root element View and don't contains the right
	 * transformation for sub elements.
	 */
	@Override
	public void draw(GDXRenderer renderer, Viewport viewport) {
		draw(renderer, 0, 0);
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

    @Override
    public View findById(String id) {
        return findById(id.hashCode());
    }

    private View findById(int resId) {
        for (View view: _views) {
            if (view._id == resId) {
                return view;
            }
            if (view instanceof FrameLayout) {
                View ret = ((FrameLayout)view).findById(resId);
                if (ret != null) {
                    return ret;
                }
            }
        }
        return null;
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

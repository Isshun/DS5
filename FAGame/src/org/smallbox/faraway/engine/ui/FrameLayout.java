package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class FrameLayout extends View {
    protected List<View> 		_views = new ArrayList<>();;
	protected RenderEffect _renderEffect;

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
	protected void onDraw(GFXRenderer renderer, RenderEffect effect) {
	}

	@Override
	public void draw(GFXRenderer renderer, int x, int y) {
		if (_isVisible == false) {
			return;
		}

		if (_renderEffect == null) {
			createRender();
		}

		if (_background != null) {
			renderer.draw(_background, _renderEffect);
		}

		for (View view: _views) {
			view.draw(renderer, _renderEffect);
		}

		onDraw(renderer, _renderEffect);
	}

	/**
	 * FrameLayout have there own draw() method because _renderEffect parameter
	 * is RenderStates for root element View and don't contains the right
	 * transformation for sub elements.
	 */
	@Override
	public void draw(GFXRenderer renderer, RenderEffect effect) {
		draw(renderer, 0, 0);
	}

	protected abstract void createRender();

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
        _views.forEach(org.smallbox.faraway.engine.ui.View::remove);
		_views.clear();
	}

	public void removeView(View view) {
		view.remove();
		_views.remove(view);
	}

	public void setPosition(int x, int y) {
		_x = x;
		_y = y;
		
		_renderEffect = null;
//		for (View view: _views) {
//			view.setPosition(view.getPosX() + x, y);
//		}
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

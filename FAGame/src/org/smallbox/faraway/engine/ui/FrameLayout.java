package org.smallbox.faraway.engine.ui;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.RenderEffect;

import java.util.ArrayList;
import java.util.List;

public abstract class FrameLayout extends View {
    protected List<View> 		_views;
	protected RenderEffect _renderEffect;

	public FrameLayout(int width, int height) {
		super(width, height);
		init();
	}

	public FrameLayout() {
		super(0, 0);
		init();
	}

	private void init() {
		_views = new ArrayList<>();
		setPosition(0, 0);
	}

	@Override
	protected void onDraw(GFXRenderer renderer, RenderEffect effect) {
	}

	/**
	 * FrameLayout have there own draw() method because _renderEffect parameter
	 * is RenderStates for root element View and don't contains the right
	 * transformation for sub elements.
	 */
	@Override
	public void draw(GFXRenderer renderer, RenderEffect effect) {
		if (_isVisible == false) {
			return;
		}
		
		if (_renderEffect == null) {
			createRender();
		}

        if (_background != null) {
            renderer.draw(_background, effect);
        }

		for (View view: _views) {
			view.draw(renderer, _renderEffect);
		}
		
		onDraw(renderer, _renderEffect);
	}

	protected abstract void createRender();

	public void addView(View view) {
		if (this.equals(view)) {
//			Log.error("FrameLayout: try to add itself to childrens");
			return;
		}
		
		view.setParent(this);
		_views.add(view);
		view.resetPos();
	}

	@Override
	protected void remove() {
		super.remove();
		for (View view: _views) {
			view.remove();
		}
		_views.clear();
	}

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

    public void clearAllViews() {
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

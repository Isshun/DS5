package org.smallbox.faraway.engine.ui;

import org.jsfml.system.Vector2f;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.Renderer;
import org.smallbox.faraway.engine.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FrameLayout extends View {
	private List<View> 		_views;
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
		_views = new ArrayList<View>();
		setPosition(0, 0);
	}

	@Override
	protected void onDraw(Renderer renderer, RenderEffect effect) {
        for (View view: _views) {
            view.draw(renderer, effect);
        }
	}

	/**
	 * FrameLayout have there own draw() method because _renderEffect parameter
	 * is RenderStates for root element View and don't contains the right
	 * transformation for sub elements.
	 */
	@Override
	public void draw(Renderer renderer, RenderEffect effect) {
		if (_isVisible == false) {
			return;
		}
		
		if (_renderEffect == null) {
			createRender();
		}

		super.draw(renderer, _renderEffect);

		for (View view: _views) {
			view.draw(renderer, _renderEffect);
		}
		
		onDraw(renderer, _renderEffect);
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

        _renderEffect = new RenderEffect();
	    _renderEffect.setTranslate(posX, posY);
	}

	public void addView(View view) {
		if (this.equals(view)) {
			Log.error("FrameLayout: try to add itself to childrens");
			return;
		}
		
		view.setParent(this);
		_views.add(view);
	}

	@Override
	protected void remove() {
		super.remove();
		for (View view: _views) {
			view.remove();
		}
		_views.clear();
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
		
		_renderEffect = null;
//		for (View view: _views) {
//			view.setPosition(view.getPosX() + x, y);
//		}
	}

	// TODO
	public void setPosition(Vector2f pos) {
		setPosition((int)pos.x, (int)pos.y);
	}
}

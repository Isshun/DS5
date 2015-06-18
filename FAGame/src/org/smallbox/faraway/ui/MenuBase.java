package org.smallbox.faraway.ui;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.RenderEffect;
import org.smallbox.faraway.ui.engine.View;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuBase {
	private final int   _width;
	private final int   _height;
    private List<View>  _views;
    private boolean     _isVisible;
    private Color       _backgroundColor;
    private int _x;
    private int _y;

    public MenuBase(int width, int height) {
        _views = new ArrayList<>();
		_width = width;
		_height = height;
	}

	public void onKeyDown() {
	}

	public void onKeyUp() {
	}

	public void onKeyEnter() {
	}

	public boolean checkKey(GameEventListener.Key key) {
		return false;
	}

    protected void addView(View view) {
        _views.add(view);
    }

    protected void setBackgroundColor(Color color) {
        _backgroundColor = color;
    }

    public void setVisible(boolean isVisible) {
        _isVisible = isVisible;
    }

    public void draw(GFXRenderer renderer, RenderEffect effect) {
        // TODO background

        for (View view: _views) {
            view.draw(renderer, effect);
        }

        onDraw(renderer, effect);
    }

    public abstract void onDraw(GFXRenderer renderer, RenderEffect effect);

    public boolean isVisible() {
        return _isVisible;
    }

    public void setPosition(int x, int y) {
        _x = x;
        _y = y;
    }

}

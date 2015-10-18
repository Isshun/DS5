package org.smallbox.faraway.ui.engine.views;

import org.smallbox.faraway.engine.renderer.GDXRenderer;

import java.util.List;

public class UIFrame extends View {
    public UIFrame(int width, int height) {
        super(width, height);
    }

    public UIFrame() {
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

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
//        _needResetPos = true;
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (_views != null) {
                for (View view : _views) {
                    view.draw(renderer, _x + x, _y + y);
                }
            }
        }
    }

    public void addView(View view) {
        if (this.equals(view)) {
//            Log.error("UIFrame: try to add itself to childrens");
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

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    public void removeAllViews() {
        _views.forEach(View::remove);
        _views.clear();
    }

    public void removeView(View view) {
        view.remove();
        _views.remove(view);
    }
}

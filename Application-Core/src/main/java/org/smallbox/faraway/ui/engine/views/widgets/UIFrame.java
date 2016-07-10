package org.smallbox.faraway.ui.engine.views.widgets;

import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;

public class UIFrame extends View {
    public UIFrame(ModuleBase module) {
        super(module);
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
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (_views != null) {
                for (View view : _views) {
                    view.draw(renderer, getAlignedX() + x, getAlignedY() + y);
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
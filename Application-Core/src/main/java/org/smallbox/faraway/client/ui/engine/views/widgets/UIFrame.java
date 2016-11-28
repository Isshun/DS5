package org.smallbox.faraway.client.ui.engine.views.widgets;

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

    @Override
    protected void onAddView(View view) {
        if (this.equals(view)) {
//            Log.error("UIFrame: try to addSubJob itself to childrens");
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

    @Override
    protected void onRemoveView(View view) {
        view.remove();
    }
}

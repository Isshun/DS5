package org.smallbox.faraway.ui.engine.views.widgets;

import org.smallbox.faraway.core.engine.renderer.GDXRenderer;

/**
 * Created by Alex on 26/09/2015.
 */
public class UIList extends View {
    public UIList(int width, int height) {
        super(width, height);
    }

    @Override
    public void addView(View view) {
        _views.add(view);

        view.setParent(this);

        int offset = 0;
        for (View subView : _views) {
            subView.setMarginTop(offset);
            offset += subView.getHeight();
        }

        if (_fixedWidth == -1 || _fixedHeight == -1) {
            setSize(_width, offset);
        }
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
//            if (_needResetPos) {
//                _finalX = x;
//                _finalY = y;
//                View view = this;
//                while (view != null) {
//                    _finalX += view.getPosX();
//                    _finalY += view.getPosY();
//                    view = view.getParent();
//                }
//
//                int offset = 0;
//                for (View subView : _views) {
//                    subView.setMarginTop(offset);
//                    offset += subView.getHeight();
//                }
//            }

            int offset = 0;
            for (View view : _views) {
                view.draw(renderer, _x + x, _y + y + offset);
                offset += view.getHeight();
            }
        }
    }

    @Override
    public int getContentWidth() {
        return _width;
    }

    @Override
    public int getContentHeight() {
        return _height;
    }
}

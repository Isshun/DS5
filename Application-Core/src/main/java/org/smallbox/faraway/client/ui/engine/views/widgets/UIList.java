package org.smallbox.faraway.client.ui.engine.views.widgets;

import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.engine.module.ModuleBase;

import java.util.ConcurrentModificationException;
import java.util.stream.Collectors;

public class UIList extends View implements AutoCloseable {

    public UIList(ModuleBase module) {
        super(module);
    }

    @Override
    protected void onAddView(View view) {
        view.setDeep(_deep + 1);

        int offset = 0;
        for (View subView : _views) {
            offset += subView.getHeight() + subView.getMarginTop() + subView.getMarginBottom();
        }

        if (_fixedWidth == -1 || _fixedHeight == -1) {
            setSize(_width, offset);
        }
    }

    @Override
    protected void onRemoveView(View view) {
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            try {
                int offset = 0;
                for (View view : _views) {
                    if (view.isVisible()) {
                        view.draw(renderer, getAlignedX() + x + _marginLeft, offset + getAlignedY() + y + _marginTop);
                        offset += view.getHeight() + view.getMarginTop() + view.getMarginBottom();
                    }
                }
            } catch (ConcurrentModificationException e) {
                // TODO
//                throw new GameException(e);
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

    @Override
    public void close() throws Exception {
    }

    @Override
    public String toString() { return "" + _name + " [" + String.join(", ", _views.stream().map(View::toString).collect(Collectors.toList()))+ "]"; }
}

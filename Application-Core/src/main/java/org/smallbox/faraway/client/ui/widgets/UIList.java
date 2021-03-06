package org.smallbox.faraway.client.ui.widgets;

import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.module.ModuleBase;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;

public class UIList extends CompositeView implements AutoCloseable {

    private int spacing;

    public UIList(ModuleBase module) {
        super(module);
    }

    public UIList(ModuleBase module, List<View> views) {
        super(module);
        views.forEach(this::addView);
    }

    @Override
    protected void onAddView(View view) {
        view.setDeep(_deep + 1);
        updateSize();
    }

    @Override
    protected void updateSize() {
        int offset = 0;
        for (View subView : _views) {
            offset += subView.getHeight() + subView.getGeometry().getMarginTop() + subView.getGeometry().getMarginBottom();
        }

        if (geometry.getFixedWidth() == View.CONTENT) {
            geometry.setWidth(getWidth());
        }

        if (geometry.getFixedHeight() == View.CONTENT) {
            geometry.setHeight(offset);
        }
    }

    @Override
    protected void onRemoveView(View view) {
    }

    @Override
    public void draw(BaseRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            try {
                int offset = 0;
                for (View view : _views) {
                    if (view.isVisible()) {
                        view.draw(renderer, getAlignedX() + x + geometry.getMarginLeft(), offset + getAlignedY() + y + geometry.getMarginTop());
                        offset += view.getHeight() + view.getGeometry().getMarginTop() + view.getGeometry().getMarginBottom() + spacing;
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
        return getWidth();
    }

    @Override
    public int getContentHeight() {
        return getHeight();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public String toString() { return "" + getId() + " [" + _views.stream().map(View::toString).collect(Collectors.joining(", ")) + "]"; }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

}

package org.smallbox.faraway.client.ui.engine.views.widgets;

import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.views.Align;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.engine.module.ModuleBase;

import java.util.stream.Collectors;

public class UIFrame extends CompositeView {
    public UIFrame(ModuleBase module) {
        super(module);
    }

    @Override
    public void init() {
        if (_align == Align.CENTER && _parent != null) {
            geometry.setOffsetX((_parent.getContentWidth() - getWidth()) / 2);
            geometry.setOffsetY((_parent.getContentHeight() - getHeight()) / 2);
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
    }

    @Override
    public void remove() {
        super.remove();
        _views.forEach(View::remove);
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

    @Override
    public String toString() { return "" + getName() + " [" + _views.stream().map(View::toString).collect(Collectors.joining(", ")) + "]"; }
}

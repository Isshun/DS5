package org.smallbox.faraway.client.ui.engine.views.widgets;

import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.engine.module.ModuleBase;

public class UIDropDown extends CompositeView {
    private int     _count;
    private boolean _isOpen;
    private View    _current;
    private final View    _overlay;
    private int     _currentIndex = -1;

    public UIDropDown(ModuleBase module) {
        super(module);

        _overlay = new UIFrame(module);
        _overlay.setDeep(100);
        _overlay.getEvents().setOnClickListener((x, y) -> {
            setOpen(true);
            uiEventManager.setCurrentDropDown(_isOpen ? UIDropDown.this : null);
        });
    }

    public boolean  isOpen() { return _isOpen; }
    public View     getCurrent() { return _current; }
    public int      getCurrentIndex() { return _currentIndex; }
    public void     setOpen(boolean isOpen) {
        _isOpen = isOpen;
        _overlay.setVisible(!_isOpen);
    }

    public void setCurrent(View current) {
        _current = current;
//        _currentIndex = _views.indexOf(current);
    }

    public void setCurrentIndex(int index) {
        _currentIndex = index;
//        _current = _views.get(index);
    }

    @Override
    public void onAddView(View view) {
        view.setDeep(_deep + 10);
        _views.add(view);
        view.setParent(this);
        _count = _views.size();

        if (_current == null) {
            _current = view;
            _currentIndex = 0;
        }
    }

    @Override
    protected void onRemoveView(View view) {
        _count = _views.size();
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (_current != null) {
                _current.draw(renderer, geometry.getX() + x, geometry.getY() + y);
            }
        }
    }

    public void drawDropDown(GDXRenderer renderer, int x, int y) {
        if (_isVisible) {
            if (_isOpen) {
                int offsetY = getHeight();
                for (View view : _views) {
                    view.draw(renderer, geometry.getFinalX(), geometry.getFinalY() + offsetY);
                    offsetY += view.getHeight();
                }
            }
            _overlay.draw(renderer, geometry.getFinalX(), geometry.getFinalY());
        }
    }

    @Override
    public int getContentWidth() {
        return _current != null ? _current.getWidth() : 0;
    }

    @Override
    public int getWidth() {
        return _current != null ? _current.getWidth() : 0;
    }

    @Override
    public int getContentHeight() {
        return _current != null ? _current.getHeight() : 0;
    }

    @Override
    public int getHeight() {
        return _current != null ? _current.getHeight() : 0;
    }
}

package org.smallbox.faraway.ui.engine.views.widgets;

import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.UIEventManager;

/**
 * Created by Alex on 28/09/2015.
 */
public class UIDropDown extends View {
    private int     _count;
    private boolean _isOpen;
    private View    _current;
    private View    _overlay;
    private int     _currentIndex = -1;

    public UIDropDown(int width, int height) {
        super(width, height);

        _overlay = new UIFrame(width, height);
        _overlay.setDeep(100);
        _overlay.setOnClickListener(() -> {
            setOpen(true);
            UIEventManager.getInstance().setCurrentDropDown(_isOpen ? UIDropDown.this : null);
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
        _currentIndex = _views.indexOf(current);
    }

    public void setCurrentIndex(int index) {
        _currentIndex = index;
        _current = _views.get(index);
    }

    @Override
    public void addView(View view) {
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
    public void removeAllViews() {
        super.removeAllViews();
        _count = 0;
    }

    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            if (_current != null) {
                _current.draw(renderer, _x + x, _y + y);
            }
        }
    }

    public void drawDropDown(GDXRenderer renderer, int x, int y) {
        if (_isVisible) {
            if (_isOpen) {
                int offsetY = _height;
                for (View view : _views) {
                    view.draw(renderer, _finalX, _finalY + offsetY);
                    offsetY += view.getHeight();
                }
            }
            _overlay.draw(renderer, _finalX, _finalY);
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

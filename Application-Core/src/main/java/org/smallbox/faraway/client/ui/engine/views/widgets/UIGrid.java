package org.smallbox.faraway.client.ui.engine.views.widgets;

import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.OnKeyListener;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.engine.module.ModuleBase;

import java.util.stream.Collectors;

public class UIGrid extends CompositeView {
    private int         _columns;
    private int         _rowHeight;
    private int         _columnWidth;
    private int         _count;
    private int         _index;
    private boolean     _keepSorted;

    public UIGrid(ModuleBase module) {
        super(module);
    }

    @Override
    public UIGrid setSize(int width, int height) {
        if (width != -1 && height != -1) {
            super.setSize(width, height);
        } else {
            super.setSize(_columns * _columnWidth, height);
        }
        return this;
    }

    @Override
    public void setFocusable(boolean focusable) {
        _focusable = focusable;

        if (_focusable) {
            uiEventManager.setOnKeyListener(this, new OnKeyListener() {
                @Override
                public void onKeyPress(View view, int key) {
                }

                @Override
                public void onKeyRelease(View view, int key) {
//                    _views.get(_index).onExit();
//
//                    if (key == Input.Keys.DOWN) {
//                        _index = Math.min(_count - 1, _index + _columns);
//                    }
//                    if (key == Input.Keys.UP) {
//                        _index = Math.max(0, _index - _columns);
//                    }
//                    if (key == Input.Keys.RIGHT) {
//                        _index = Math.min(_count - 1, _index + 1);
//                    }
//                    if (key == Input.Keys.LEFT) {
//                        _index = Math.max(0, _index - 1);
//                    }
////                    _views.get(_index).onClick();
//                    _views.get(_index).onEnter();
                }
            });
        } else {
            uiEventManager.removeOnKeyListener(this);
        }
    }

    @Override
    protected void onAddView(View view) {
        view.setDeep(_deep + 1);

        _count = _views.size();

//        if (_keepSorted) {
//            Collections.sort(_views, (o1, o2) -> {
//                if (o1.getString() == null) return -1;
//                if (o2.getString() == null) return 1;
//                return o1.getString().compareTo(o2.getString());
//            });
//        }
    }

//    @Override
//    public void removeView(View view) {
//        _views.remove(view);
//        view.setParent(null);
//        _count = _views.size();
//    }

    @Override
    protected void onRemoveView(View view) {
        _count = _views.size();
    }


    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            int offsetX = 0, offsetY = 0;
            int index = 0;
            for (View view : _views) {
                view.draw(renderer, geometry.getX() + x + offsetX, geometry.getY() + y + offsetY);

                if (++index % _columns == 0) {
                    offsetX = 0;
                    offsetY += _rowHeight;
                } else {
                    offsetX += _columnWidth;
                }
            }
        }
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
    public int getHeight() {
        return (_count * _rowHeight) / _columns;
    }

    public void setColumns(int columns) {
        _columns = columns;
    }

    public void setRowHeight(int rowHeight) {
        _rowHeight = (int) (rowHeight * geometry.getUiScale());
    }

    public void setColumnWidth(int columnWidth) {
        _columnWidth = (int) (columnWidth * geometry.getUiScale());
    }

    public int getColumnWidth() {
        return _columnWidth;
    }

    public int getColumns() {
        return _columns;
    }

    public void keepSorted(boolean keepSorted) {
        _keepSorted = keepSorted;
    }

    @Override
    public String toString() { return "" + getName() + " [" + _views.stream().map(View::toString).collect(Collectors.joining(", ")) + "]"; }
}

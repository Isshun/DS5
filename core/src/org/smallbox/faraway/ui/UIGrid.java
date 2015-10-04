package org.smallbox.faraway.ui;

import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.ui.engine.view.View;

/**
 * Created by Alex on 28/09/2015.
 */
public class UIGrid extends View {
    private int _columns;
    private int _rowHeight;
    private int _columnWidth;
    private int _count;

    public UIGrid(int width, int height) {
        super(width, height);
    }

    @Override
    public void addView(View view) {
        _views.add(view);
        view.setParent(this);
        _count = _views.size();
    }

//    @Override
//    public void removeView(View view) {
//        _views.remove(view);
//        view.setParent(null);
//        _count = _views.size();
//    }

    public void removeAllViews() {
        _views.clear();
        _count = 0;
    }


    @Override
    public void draw(GDXRenderer renderer, int x, int y) {
        super.draw(renderer, x, y);

        if (_isVisible) {
            int offsetX = 0, offsetY = 0;
            int index = 0;
            for (View view : _views) {
                view.draw(renderer, _x + x + offsetX, _y + y + offsetY);

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
        _rowHeight = rowHeight;
    }

    public void setColumnWidth(int columnWidth) {
        _columnWidth = columnWidth;
    }

    public int getColumnWidth() {
        return _columnWidth;
    }

    public int getColumns() {
        return _columns;
    }
}

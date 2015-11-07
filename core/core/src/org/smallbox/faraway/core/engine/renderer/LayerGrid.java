package org.smallbox.faraway.core.engine.renderer;

import org.smallbox.faraway.core.RenderLayer;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;

/**
 * Created by Alex on 07/11/2015.
 */
public class LayerGrid {
    public interface OnRefreshLayer {
        void onRefreshLayer(RenderLayer layer, int fromX, int fromY, int toX, int toY);
    }

    private static final int    CACHE_SIZE = 25;
    private final int           _columns;
    private final int           _rows;
    private RenderLayer[][]     _layers;
    private OnRefreshLayer      _onRefreshLayer;
    private boolean             _needRefresh = true;

    public LayerGrid(int columns, int rows) {
        _columns = columns;
        _rows = rows;
        _layers = new RenderLayer[columns][rows];
        int index = 0;
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                _layers[column][row] = new RenderLayer(index++,
                        column * CACHE_SIZE * Constant.TILE_WIDTH,
                        row * CACHE_SIZE * Constant.TILE_HEIGHT,
                        CACHE_SIZE * Constant.TILE_WIDTH,
                        CACHE_SIZE * Constant.TILE_HEIGHT);
            }
        }
    }

    public void planRefresh(int column, int row) {
        _layers[column][row].planRefresh();
        _needRefresh = true;
    }

    public void setOnRefreshLayer(OnRefreshLayer onRefreshLayer) {
        _onRefreshLayer = onRefreshLayer;
    }

    public void refresh() {
        for (int column = 0; column < _columns; column++) {
            for (int row = 0; row < _rows; row++) {
                if (_layers[column][row].isVisible(Game.getInstance().getViewport()) && _layers[column][row].needRefresh()) {
                    _layers[column][row].refresh();
                    _onRefreshLayer.onRefreshLayer(_layers[column][row], column * CACHE_SIZE, row * CACHE_SIZE, (column + 1) * CACHE_SIZE, (row + 1) * CACHE_SIZE);
                }
            }
        }
    }

    public void refreshAll() {
        for (int column = 0; column < _columns; column++) {
            for (int row = 0; row < _rows; row++) {
                _layers[column][row].planRefresh();
            }
        }
        _needRefresh = true;
    }

    public void draw(GDXRenderer renderer) {
        Viewport viewport = Game.getInstance().getViewport();

        for (int column = _columns - 1; column >= 0; column--) {
            for (int row = _rows - 1; row >= 0; row--) {
                // Draw up to date layer
                if (_layers[column][row].isVisible(viewport) && !_layers[column][row].needRefresh() && _layers[column][row].isDrawable()) {
                    _layers[column][row].onDraw(renderer, viewport, column * CACHE_SIZE * Constant.TILE_WIDTH, row * CACHE_SIZE * Constant.TILE_HEIGHT);
                }

                // Refresh needed layer
                if (_layers[column][row].isVisible(viewport) && _layers[column][row].needRefresh()) {
                    System.out.println("refresh layer: " + _layers[column][row].getIndex());
                    _layers[column][row].refresh();
                    _onRefreshLayer.onRefreshLayer(_layers[column][row], column * CACHE_SIZE, row * CACHE_SIZE, (column + 1) * CACHE_SIZE, (row + 1) * CACHE_SIZE);
                }

                // Clear out of screen layers
                if (!_layers[column][row].isVisible(viewport) && _layers[column][row].isDrawable()) {
                    System.out.println("clear layer: " + _layers[column][row].getIndex());
                    _layers[column][row].clear();
                }
            }
        }
    }
}

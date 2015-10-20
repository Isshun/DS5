package org.smallbox.faraway.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.smallbox.faraway.core.engine.drawable.GDXDrawable;

/**
 * Created by Alex on 21/07/2015.
 */
public class UISelection extends GDXDrawable {
    private ShapeRenderer       _shapeRenderer;
    private int                 _startX;
    private int                 _startY;
    private int                 _endX;
    private int                 _endY;

    public UISelection() {
        _shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void draw(SpriteBatch batch, int x, int y) {
        if (_startX != -1) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            _shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());

            x = Math.min(_startX, _endX);
            y = Math.min(_startY, _endY);
            int width = Math.abs(_startX - _endX);
            int height = Math.abs(_startY - _endY);

            _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            _shapeRenderer.setColor(Color.BLUE);
            _shapeRenderer.rect(x, y, 1, height);
            _shapeRenderer.end();

            _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            _shapeRenderer.setColor(Color.BLUE);
            _shapeRenderer.rect(x, y, width, 1);
            _shapeRenderer.end();

            _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            _shapeRenderer.setColor(Color.BLUE);
            _shapeRenderer.rect(x + width, y, 1, height);
            _shapeRenderer.end();

            _shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            _shapeRenderer.setColor(Color.BLUE);
            _shapeRenderer.rect(x, y + height, width, 1);
            _shapeRenderer.end();
        }
    }

    public int getFromX() { return Math.min(_startX, _endX); }
    public int getFromY() { return Math.min(_startY, _endY); }
    public int getToX() { return Math.max(_startX, _endX); }
    public int getToY() { return Math.max(_startY, _endY); }

    public void setPosition(int x, int y) {
        _endX = x;
        _endY = y;
    }

    public void setStart(int x, int y) {
        _startX = _endX = x;
        _startY = _endY = y;
    }

    public void clear() {
        _startX = _endX = -1;
        _startY = _endY = -1;
    }
}

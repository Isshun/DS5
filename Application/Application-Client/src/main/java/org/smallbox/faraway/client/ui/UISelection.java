//package org.smallbox.faraway.client.ui;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.glutils.ShapeLayer;
//import org.smallbox.faraway.client.drawable.GDXDrawable;
//import org.smallbox.faraway.core.Application;
//
///**
// * Created by Alex on 21/07/2015.
// */
//public class UISelection extends GDXDrawable {
//    private ShapeLayer       _shapeLayer;
//    private int                 _startX;
//    private int                 _startY;
//    private int                 _startZ;
//    private int                 _endX;
//    private int                 _endY;
//    private int                 _endZ;
//
//    public UISelection() {
//        Application.runOnMainThread(() -> _shapeLayer = new ShapeLayer());
//    }
//
//    @Override
//    public void draw(SpriteBatch batch, int x, int y) {
//        if (_startX != -1) {
//            Gdx.gl.glEnable(GL20.GL_BLEND);
//            _shapeLayer.setProjectionMatrix(batch.getProjectionMatrix());
//
//            x = Math.min(_startX, _endX);
//            y = Math.min(_startY, _endY);
//            int width = Math.abs(_startX - _endX);
//            int height = Math.abs(_startY - _endY);
//
//            _shapeLayer.begin(ShapeLayer.ShapeType.Filled);
//            _shapeLayer.setColor(Color.BLUE);
//            _shapeLayer.rect(x, y, 1, height);
//            _shapeLayer.end();
//
//            _shapeLayer.begin(ShapeLayer.ShapeType.Filled);
//            _shapeLayer.setColor(Color.BLUE);
//            _shapeLayer.rect(x, y, width, 1);
//            _shapeLayer.end();
//
//            _shapeLayer.begin(ShapeLayer.ShapeType.Filled);
//            _shapeLayer.setColor(Color.BLUE);
//            _shapeLayer.rect(x + width, y, 1, height);
//            _shapeLayer.end();
//
//            _shapeLayer.begin(ShapeLayer.ShapeType.Filled);
//            _shapeLayer.setColor(Color.BLUE);
//            _shapeLayer.rect(x, y + height, width, 1);
//            _shapeLayer.end();
//        }
//    }
//
//    public int getFromX() { return Math.min(_startX, _endX); }
//    public int getFromY() { return Math.min(_startY, _endY); }
//    public int getFromZ() { return Math.max(_startZ, _endZ); }
//    public int getToX() { return Math.max(_startX, _endX); }
//    public int getToY() { return Math.max(_startY, _endY); }
//    public int getToZ() { return Math.max(_startZ, _endZ); }
//
//    public void setPosition(int x, int y, int z) {
//        _endX = x;
//        _endY = y;
//        _endZ = z;
//    }
//
//    public void setStart(int x, int y, int z) {
//        _startX = _endX = x;
//        _startY = _endY = y;
//        _startZ = _endZ = z;
//    }
//
//    public void clear() {
//        _startX = _endX = -1;
//        _startY = _endY = -1;
//        _startZ = _endZ = -1;
//    }
//}

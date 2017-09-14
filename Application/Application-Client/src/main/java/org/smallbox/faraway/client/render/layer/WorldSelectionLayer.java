//package org.smallbox.faraway.client.render.layer;
//
//import com.badlogic.gdx.graphics.Color;
//import org.smallbox.faraway.client.render.Viewport;
//import org.smallbox.faraway.core.GameLayer;
//import org.smallbox.faraway.common.dependencyInjector.BindModule;
//import org.smallbox.faraway.modules.world.WorldModule;
//
//@GameLayer(level = 999, visible = true)
//public class WorldSelectionLayer extends BaseLayer {
//
//    @BindComponent
//    private WorldModule worldModule;
//
//    private int _startX;
//    private int _startY;
//    private boolean _isPressed;
//
//    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
//        if (_isPressed) {
//            renderer.drawRectangle(_startX, _startY, _endX - _startX, _endY - _startY, Color.RED, false);
//        }
//    }
//
//}
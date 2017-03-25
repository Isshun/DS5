package org.smallbox.faraway.client.render.layer;

import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.modules.world.WorldModule;

@GameLayer(level = 999, visible = true)
public class WorldSelectionLayer extends BaseLayer {

    @BindModule
    private WorldModule worldModule;

    private int _startX;
    private int _startY;
    private int _endX;
    private int _endY;
    private boolean _isPressed;

    public void    onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        if (_isPressed) {
            renderer.drawRectangle(_startX, _startY, _endX - _startX, _endY - _startY, Color.RED, false);
        }
    }

    @Override
    public void onMouseMove(GameEvent event) {
        _endX = event.mouseEvent.x;
        _endY = event.mouseEvent.y;
    }

    @Override
    public void onMousePress(GameEvent event) {
        _isPressed = true;
        _startX = event.mouseEvent.x;
        _startY = event.mouseEvent.y;
    }

    @Override
    public void onMouseRelease(GameEvent event) {
        _isPressed = false;
    }
}
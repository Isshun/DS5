package org.smallbox.faraway.client.input;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class GameEventManager implements EventManager {

    @Inject private GameActionManager gameActionManager;

    private boolean _mousePressed;

    // Current mouse position
    protected int _mouseX;
    protected int _mouseY;

    // Mouse press position
    protected int _mouseDownX;
    protected int _mouseDownY;

    public boolean isMousePressed() {
        return _mousePressed;
    }

    public int getMouseX() {
        return _mouseX;
    }

    public int getMouseY() {
        return _mouseY;
    }

    public int getMouseOffsetX() {
        return _mouseX - _mouseDownX;
    }

    public int getMouseOffsetY() {
        return _mouseY - _mouseDownY;
    }

    public int getMouseDownX() {
        return _mouseDownX;
    }

    public int getMouseDownY() {
        return _mouseDownY;
    }

    @Override
    public boolean onDrag(int x, int y, boolean pressed) {
        return false;
    }

    @Override
    public boolean onMousePress(int x, int y, int button) {
        _mouseDownX = x;
        _mouseDownY = y;
        _mouseX = x;
        _mouseY = y;
        if (button == Input.Buttons.LEFT) {
            _mousePressed = true;
        }
        return false;
    }

    @Override
    public boolean onMouseRelease(int x, int y, int button) {
        _mouseX = x;
        _mouseY = y;
        _mousePressed = false;

        if (button == Input.Buttons.RIGHT) {
            gameActionManager.clearAction();
        }

        Application.notifyClient(obs -> obs.onMouseRelease(x, y, button));

        return false;
    }

    @Override
    public boolean onMouseMove(int x, int y, boolean pressed) {
        _mouseX = x;
        _mouseY = y;
        return false;
    }

}

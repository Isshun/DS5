package org.smallbox.faraway.client.manager;

import com.badlogic.gdx.InputProcessor;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.util.Constant;

import static com.badlogic.gdx.Input.Buttons;
import static com.badlogic.gdx.Input.Keys;

/**
 * Created by Alex on 04/06/2015.
 */
public class InputManager implements InputProcessor {
    private GameEventListener.Modifier _modifier;
    private int                 _lastMouseButton;
    private boolean[]           _keyDirection;
    private int                 _lastPosX;
    private int                 _lastPosY;
    private int _touchDownX;
    private int _touchDownY;
    private int _touchDragX;
    private int _touchDragY;
    private boolean _touchDrag;

    public InputManager() {
        _modifier = GameEventListener.Modifier.NONE;
        _keyDirection = new boolean[4];
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.CONTROL_LEFT) {
            _modifier = GameEventListener.Modifier.CONTROL;
        }

        if (keycode == Keys.ALT_LEFT) {
            _modifier = GameEventListener.Modifier.ALT;
        }

        if (keycode == Keys.SHIFT_LEFT) {
            _modifier = GameEventListener.Modifier.SHIFT;
        }

        if (keycode == Keys.A || keycode == Keys.LEFT) {
            _keyDirection[0] = true;
        }

        if (keycode == Keys.W || keycode == Keys.UP) {
            _keyDirection[1] = true;
        }

        if (keycode == Keys.D || keycode == Keys.RIGHT) {
            _keyDirection[2] = true;
        }

        if (keycode == Keys.S || keycode == Keys.DOWN) {
            _keyDirection[3] = true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.A || keycode == Keys.LEFT) {
            _keyDirection[0] = false;
//            return true;
        }

        if (keycode == Keys.W || keycode == Keys.UP) {
            _keyDirection[1] = false;
//            return true;
        }

        if (keycode == Keys.D || keycode == Keys.RIGHT) {
            _keyDirection[2] = false;
//            return true;
        }

        if (keycode == Keys.S || keycode == Keys.DOWN) {
            _keyDirection[3] = false;
//            return true;
        }

        if (keycode == Keys.CONTROL_LEFT) {
            _modifier = GameEventListener.Modifier.NONE;
        }

        if (keycode == Keys.ALT_LEFT) {
            _modifier = GameEventListener.Modifier.NONE;
        }

        if (keycode == Keys.SHIFT_LEFT) {
            _modifier = GameEventListener.Modifier.NONE;
        }

        // Cleat UiEventManager selection listener when escape key is pushed
        if (keycode == Keys.ESCAPE && ApplicationClient.selectionManager.getSelectionListener() != null) {
            ApplicationClient.selectionManager.setSelectionListener(null);
            return false;
        }

        ApplicationClient.onKeyEvent(GameEventListener.Action.RELEASED, keycode, _modifier);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        _lastMouseButton = button;
        _touchDownX = _touchDragX = x;
        _touchDownY = _touchDragY = y;

        if (x > 0 && x < Constant.WINDOW_WIDTH && y > 0 && y < Constant.WINDOW_HEIGHT) {

            // Passe l'evenement à l'ui event manager
            if (ApplicationClient.uiEventManager.onMousePress(x, y, button)) {
                return false;
            }

            // Passe l'evenement au game event manager
            if (ApplicationClient.gameEventManager.onMousePress(x, y, button)) {
                return false;
            }

        }

        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        _touchDrag = false;

        if (x > 0 && x < Constant.WINDOW_WIDTH && y > 0 && y < Constant.WINDOW_HEIGHT) {

            // Passe l'evenement à l'ui event manager
            if (ApplicationClient.uiEventManager.onMouseRelease(x, y, button)) {
                return false;
            }

            // Passe l'evenement au game event manager
            if (ApplicationClient.gameEventManager.onMouseRelease(x, y, button)) {
                return false;
            }

        }

        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        _touchDragX = x;
        _touchDragY = y;
        _touchDrag = true;

        if (_lastMouseButton == Buttons.RIGHT) {
//            if (Application.gameManager.isLoaded()) {
//                ApplicationClient.layerManager.getViewport().update(x, y);
//                return true;
//            }
//        } else if (_lastMouseButton == Buttons.LEFT) {
//            Log.debug("select: " + _touchDownX + "x" + _touchDownY);
//            Log.debug("to: " + x + "x" + y );
//
////            Application.notify(observer -> observer.onSelectParcel(parcels));
//
//            return false;
//        } else {
        }

        ApplicationClient.onMouseEvent(GameEventListener.Action.MOVE, -1, x, y, _lastMouseButton == Buttons.RIGHT);

        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        _lastPosX = x;
        _lastPosY = y;

        if (x > 0 && x < Constant.WINDOW_WIDTH && y > 0 && y < Constant.WINDOW_HEIGHT) {

            // Passe l'evenement à l'ui event manager
            if (ApplicationClient.uiEventManager.onMouseMove(x, y)) {
                return false;
            }

            // Passe l'evenement au game event manager
            if (ApplicationClient.gameEventManager.onMouseMove(x, y)) {
                return false;
            }

        }

        ApplicationClient.notify(observer -> observer.onMouseMove(x, y, -1));

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount < 0) {
            ApplicationClient.onMouseEvent(GameEventListener.Action.RELEASED, Buttons.FORWARD, _lastPosX, _lastPosY, false);
            return true;
        }
        if (amount > 0) {
            ApplicationClient.onMouseEvent(GameEventListener.Action.RELEASED, Buttons.BACK, _lastPosX, _lastPosY, false);
            return true;
        }
        return false;
    }

    public int getMouseX() { return _lastPosX; }
    public int getMouseY() { return _lastPosY; }

    public int getTouchDownX() { return _touchDownX; }
    public int getTouchDownY() { return _touchDownY; }

    public int getTouchDragX() { return _touchDragX; }
    public int getTouchDragY() { return _touchDragY; }

    public boolean getTouchDrag() { return _touchDrag; }

    public boolean[] getDirection() {
        return _keyDirection;
    }
}
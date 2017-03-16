package org.smallbox.faraway.client.manager;

import com.badlogic.gdx.InputProcessor;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.MouseEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.Application;
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

        GameEventListener.Key key = GameEventListener.Key.UNKNOWN;
        switch (keycode) {
            case Keys.UNKNOWN:
            case Keys.A: key = GameEventListener.Key.A; break;
            case Keys.B: key = GameEventListener.Key.B; break;
            case Keys.C: key = GameEventListener.Key.C; break;
            case Keys.D: key = GameEventListener.Key.D; break;
            case Keys.E: key = GameEventListener.Key.E; break;
            case Keys.F: key = GameEventListener.Key.F; break;
            case Keys.G: key = GameEventListener.Key.G; break;
            case Keys.H: key = GameEventListener.Key.H; break;
            case Keys.I: key = GameEventListener.Key.I; break;
            case Keys.J: key = GameEventListener.Key.J; break;
            case Keys.K: key = GameEventListener.Key.K; break;
            case Keys.L: key = GameEventListener.Key.L; break;
            case Keys.M: key = GameEventListener.Key.M; break;
            case Keys.N: key = GameEventListener.Key.N; break;
            case Keys.O: key = GameEventListener.Key.O; break;
            case Keys.P: key = GameEventListener.Key.P; break;
            case Keys.Q: key = GameEventListener.Key.Q; break;
            case Keys.R: key = GameEventListener.Key.R; break;
            case Keys.S: key = GameEventListener.Key.S; break;
            case Keys.T: key = GameEventListener.Key.T; break;
            case Keys.U: key = GameEventListener.Key.U; break;
            case Keys.V: key = GameEventListener.Key.V; break;
            case Keys.W: key = GameEventListener.Key.W; break;
            case Keys.X: key = GameEventListener.Key.X; break;
            case Keys.Y: key = GameEventListener.Key.Y; break;
            case Keys.Z: key = GameEventListener.Key.Z; break;
            case Keys.NUM_0: key = GameEventListener.Key.D_0; break;
            case Keys.NUM_1: key = GameEventListener.Key.D_1; break;
            case Keys.NUM_2: key = GameEventListener.Key.D_2; break;
            case Keys.NUM_3: key = GameEventListener.Key.D_3; break;
            case Keys.NUM_4: key = GameEventListener.Key.D_4; break;
            case Keys.NUM_5: key = GameEventListener.Key.D_5; break;
            case Keys.NUM_6: key = GameEventListener.Key.D_6; break;
            case Keys.NUM_7: key = GameEventListener.Key.D_7; break;
            case Keys.NUM_8: key = GameEventListener.Key.D_8; break;
            case Keys.NUM_9: key = GameEventListener.Key.D_9; break;
            case Keys.ESCAPE: key = GameEventListener.Key.ESCAPE; break;
            case Keys.ENTER: key = GameEventListener.Key.ENTER; break;
            case Keys.BACKSPACE: key = GameEventListener.Key.BACKSPACE; break;
            case Keys.F1: key = GameEventListener.Key.F1; break;
            case Keys.F2: key = GameEventListener.Key.F2; break;
            case Keys.F3: key = GameEventListener.Key.F3; break;
            case Keys.F4: key = GameEventListener.Key.F4; break;
            case Keys.F5: key = GameEventListener.Key.F5; break;
            case Keys.F6: key = GameEventListener.Key.F6; break;
            case Keys.F7: key = GameEventListener.Key.F7; break;
            case Keys.F8: key = GameEventListener.Key.F8; break;
            case Keys.F9: key = GameEventListener.Key.F9; break;
            case Keys.F10: key = GameEventListener.Key.F10; break;
            case Keys.F11: key = GameEventListener.Key.F11; break;
            case Keys.F12: key = GameEventListener.Key.F12; break;
            case Keys.GRAVE: key = GameEventListener.Key.TILDE; break;
//            case Input.Keys.LCONTROL:
//            case Input.Keys.LSHIFT:
//            case Input.Keys.LALT:
//            case Input.Keys.LSYSTEM:
//            case Input.Keys.RCONTROL:
//            case Input.Keys.RSHIFT:
//            case Input.Keys.RALT:
//            case Input.Keys.RSYSTEM:
//            case Input.Keys.MENU:
//            case Input.Keys.LBRACKET:
//            case Input.Keys.RBRACKET:
//            case Keys.SEMICOLON:
            case Keys.COMMA: key = GameEventListener.Key.COMMA; break;
            case Keys.PERIOD: key = GameEventListener.Key.PERIOD; break;
//            case Input.Keys.QUOTE:
//            case Input.Keys.SLASH:
//            case Input.Keys.BACKSLASH:
//            case Input.Keys.EQUAL:
//            case Input.Keys.DASH:
            case Keys.UP: key = GameEventListener.Key.UP; break;
            case Keys.DOWN: key = GameEventListener.Key.DOWN; break;
            case Keys.LEFT: key = GameEventListener.Key.LEFT; break;
            case Keys.RIGHT: key = GameEventListener.Key.RIGHT; break;
            case Keys.SPACE: key = GameEventListener.Key.SPACE; break;
//            case Input.Keys.TILDE: key = GameEventListener.Key.TILDE; break;
            case Keys.TAB: key = GameEventListener.Key.TAB; break;
            case Keys.PAGE_UP: key = GameEventListener.Key.PAGEUP; break;
            case Keys.PAGE_DOWN: key = GameEventListener.Key.PAGEDOWN; break;
            case Keys.END:
            case Keys.HOME:
            case Keys.INSERT:
//            case Input.Keys.DELETE:
            case Keys.PLUS: key = GameEventListener.Key.PLUS; break;
            case Keys.MINUS: key = GameEventListener.Key.MINUS; break;
//            case Input.Keys.MULTIPLY:
//            case Input.Keys.DIVIDE:
//            case Input.Keys.NUMPAD0:
//            case Input.Keys.NUMPAD1:
//            case Input.Keys.NUMPAD2:
//            case Input.Keys.NUMPAD3:
//            case Input.Keys.NUMPAD4:
//            case Input.Keys.NUMPAD5:
//            case Input.Keys.NUMPAD6:
//            case Input.Keys.NUMPAD7:
//            case Input.Keys.NUMPAD8:
//            case Input.Keys.NUMPAD9:
//            case Input.Keys.F13:
//            case Input.Keys.F14:
//            case Input.Keys.F15:
//            case Input.Keys.PAUSE: key = GameEventListener.Key.UNKNOWN; break;
        }

        // Cleat UiEventManager selection listener when escape key is pushed
        if (key == GameEventListener.Key.ESCAPE && ApplicationClient.uiEventManager.getSelectionListener() != null) {
            ApplicationClient.uiEventManager.setSelectionListener(null);
            return false;
        }

        ApplicationClient.onKeyEvent(GameEventListener.Action.RELEASED, key, _modifier);

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
            GameEventListener.MouseButton mouseButton = GameEventListener.MouseButton.LEFT;
            switch (button) {
                case Buttons.LEFT:
                    mouseButton = GameEventListener.MouseButton.LEFT;
                    break;
                case Buttons.RIGHT:
                    mouseButton = GameEventListener.MouseButton.RIGHT;
                    break;
                case Buttons.MIDDLE:
                    mouseButton = GameEventListener.MouseButton.MIDDLE;
                    break;
            }

            ApplicationClient.onMouseEvent(GameEventListener.Action.PRESSED, mouseButton, x, y, false);
        }

        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        _touchDrag = false;

        if (x > 0 && x < Constant.WINDOW_WIDTH && y > 0 && y < Constant.WINDOW_HEIGHT) {
            GameEventListener.MouseButton mouseButton = GameEventListener.MouseButton.LEFT;
            switch (button) {
                case Buttons.LEFT:
                    mouseButton = GameEventListener.MouseButton.LEFT;
                    break;
                case Buttons.RIGHT:
                    mouseButton = GameEventListener.MouseButton.RIGHT;
                    break;
                case Buttons.MIDDLE:
                    mouseButton = GameEventListener.MouseButton.MIDDLE;
                    break;
            }

            ApplicationClient.onMouseEvent(GameEventListener.Action.RELEASED, mouseButton, x, y, false);

        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        _touchDragX = x;
        _touchDragY = y;
        _touchDrag = true;

        if (_lastMouseButton == Buttons.RIGHT) {
            if (Application.gameManager.isLoaded()) {
                ApplicationClient.mainRenderer.getViewport().update(x, y);
                return true;
            }
//        } else if (_lastMouseButton == Buttons.LEFT) {
//            Log.debug("select: " + _touchDownX + "x" + _touchDownY);
//            Log.debug("to: " + x + "x" + y );
//
////            Application.notify(observer -> observer.onSelectParcel(parcels));
//
//            return false;
//        } else {
        }

        ApplicationClient.onMouseEvent(GameEventListener.Action.MOVE, null, x, y, _lastMouseButton == Buttons.RIGHT);

        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        _lastPosX = x;
        _lastPosY = y;
        if (x > 0 && x < Constant.WINDOW_WIDTH && y > 0 && y < Constant.WINDOW_HEIGHT) {
            ApplicationClient.onMouseEvent(GameEventListener.Action.MOVE, null, x, y, false);
        }

        GameEvent event = new GameEvent(new MouseEvent(x, y, null, GameEventListener.Action.MOVE));
        ApplicationClient.notify(observer -> observer.onMouseMove(event));

        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount < 0) {
            ApplicationClient.onMouseEvent(GameEventListener.Action.RELEASED, GameEventListener.MouseButton.WHEEL_UP, _lastPosX, _lastPosY, false);
            return true;
        }
        if (amount > 0) {
            ApplicationClient.onMouseEvent(GameEventListener.Action.RELEASED, GameEventListener.MouseButton.WHEEL_DOWN, _lastPosX, _lastPosY, false);
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
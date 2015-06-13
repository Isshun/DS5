package org.smallbox.farpoint;

import com.badlogic.gdx.InputProcessor;
import org.smallbox.faraway.Application;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.GameTimer;
import org.smallbox.faraway.engine.util.Constant;

import static com.badlogic.gdx.Input.Buttons;
import static com.badlogic.gdx.Input.Keys;

/**
 * Created by Alex on 04/06/2015.
 */
public class GDXInputProcessor implements InputProcessor {
    private final Application   _application;
    private final GameTimer     _timer;
    private GameEventListener.Modifier _modifier;
    private int                 _lastMouseButton;

    public GDXInputProcessor(Application application, GameTimer timer) {
        _application = application;
        _timer = timer;
        _modifier = GameEventListener.Modifier.NONE;
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

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
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
            case Keys.PERIOD: key = GameEventListener.Key.PERIOD; break;
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
//            case Input.Keys.SEMICOLON:
//            case Input.Keys.COMMA:
//            case Input.Keys.PERIOD:
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
//            case Input.Keys.ADD: key = GameEventListener.Key.ADD; break;
//            case Input.Keys.SUBTRACT: key = GameEventListener.Key.SUBTRACT; break;
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
        _application.onKeyEvent(_timer, GameEventListener.Action.RELEASED, key, _modifier);

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        _lastMouseButton = button;

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

            _application.onMouseEvent(_timer, GameEventListener.Action.PRESSED, mouseButton, x, y, false);
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
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

            _application.onMouseEvent(_timer, GameEventListener.Action.RELEASED, mouseButton, x, y, false);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        _application.onMouseEvent(_timer, GameEventListener.Action.MOVE, null, x, y, _lastMouseButton == Buttons.RIGHT);
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        if (x > 0 && x < Constant.WINDOW_WIDTH && y > 0 && y < Constant.WINDOW_HEIGHT) {
            _application.onMouseEvent(_timer, GameEventListener.Action.MOVE, null, x, y, false);
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

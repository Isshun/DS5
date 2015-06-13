import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.window.VideoMode;
import org.jsfml.window.WindowStyle;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;
import org.smallbox.faraway.*;
import org.smallbox.faraway.Color;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;

/**
 * Created by Alex on 27/05/2015.
 */
public class SFMLRenderer implements GFXRenderer {
    private final RenderWindow  _window;
    private final GameTimer     _timer;
    private GameEventListener   _listener;
    private Shader              _blur;
    private SpriteModel         _lightSpriteCache;

    public SFMLRenderer(RenderWindow window) {
        _window = window;

        _timer = new GameTimer() {
            private final Clock _clock = new Clock();
            @Override
            public long getElapsedTime() {
                return _clock.getElapsedTime().asMilliseconds();
            }
        };
    }

    public void draw(RectangleShape background, RenderEffect effect) {
        if (effect != null) {
            _window.draw(background, ((SFMLRenderEffect)effect).getRender());
        } else {
            _window.draw(background);
        }
    }

    public void draw(Text text, RenderEffect effect) {
        if (effect != null) {
            _window.draw(text, ((SFMLRenderEffect)effect).getRender());
        } else {
            _window.draw(text);
        }
    }

    @Override
    public void draw(SpriteModel sprite, RenderEffect effect) {
        if (effect != null) {
            if (((SFMLRenderEffect)effect).getViewport() != null) {
                _window.draw(((SFMLSprite)sprite).getData(), ((SFMLViewport)effect.getViewport()).getRender());
            }
            _window.draw(((SFMLSprite)sprite).getData(), ((SFMLRenderEffect)effect).getRender());
        } else {
            _window.draw(((SFMLSprite)sprite).getData());
        }
    }

    @Override
    public void draw(ColorView view, RenderEffect effect) {
        view.draw(this, effect);
    }

    public void draw(Text text) {
        _window.draw(text);
    }

    public void draw(Drawable drawable) {
        _window.draw(drawable);
    }

    public void draw(Sprite spriteCache, RenderEffect effect) {
        if (effect != null) {
            _window.draw(spriteCache, ((SFMLRenderEffect)((SFMLRenderEffect)effect).getViewport().getRenderEffect()).getRender());
        } else {
            _window.draw(spriteCache);
        }
    }

    public void draw(SpriteModel sprite, RenderStates renderStates) {
        _window.draw(((SFMLSprite)sprite).getData(), renderStates);
    }

    public void clear(Color color) {
        _window.clear(new org.jsfml.graphics.Color(color.r, color.g, color.b));
    }

    public void clear() {
        _window.clear(org.jsfml.graphics.Color.BLACK);
    }

    @Override
    public void display() {
        _window.display();
    }

    @Override
    public void close() {
        _window.close();
    }

    @Override
    public boolean isOpen() {
        return _window.isOpen();
    }

    @Override
    public void refresh() {
        // Events
        Event event = null;
        while ((event = _window.pollEvent()) != null) {

            // Close window
            if (event.type == Event.Type.CLOSED) {
                _window.close();
                _listener.onWindowEvent(_timer, GameEventListener.Action.EXIT);
                return;
            }

            // Mouse button event
            if (event.type == Event.Type.MOUSE_BUTTON_PRESSED || event.type == Event.Type.MOUSE_BUTTON_RELEASED) {
                int x = event.asMouseButtonEvent().position.x;
                int y = event.asMouseButtonEvent().position.y;
                if (x > 0 && x < _window.getSize().x && y > 0 && y < _window.getSize().y) {
                    GameEventListener.Action action = null;
                    switch (event.type) {
                        case MOUSE_BUTTON_PRESSED:
                            action = GameEventListener.Action.PRESSED;
                            break;
                        case MOUSE_BUTTON_RELEASED:
                            action = GameEventListener.Action.RELEASED;
                            break;
                    }

                    GameEventListener.MouseButton button = null;
                    switch (event.asMouseButtonEvent().button) {
                        case LEFT:
                            button = GameEventListener.MouseButton.LEFT;
                            break;
                        case RIGHT:
                            button = GameEventListener.MouseButton.RIGHT;
                            break;
                        case MIDDLE:
                            button = GameEventListener.MouseButton.MIDDLE;
                            break;
                    }

                    _listener.onMouseEvent(_timer, action, button, x, y, true);
                }
            }

            // Mouse moved event
            if (event.type == Event.Type.MOUSE_MOVED) {
                int x = event.asMouseEvent().position.x;
                int y = event.asMouseEvent().position.y;
                if (x > 0 && x < _window.getSize().x && y > 0 && y < _window.getSize().y) {
                    _listener.onMouseEvent(_timer, GameEventListener.Action.MOVE, null, x, y, true);
                }
            }

            // Key event
            if (event.type == Event.Type.KEY_RELEASED ||
                    event.type == Event.Type.KEY_PRESSED) {
                KeyEvent keyEvent = event.asKeyEvent();

                GameEventListener.Action action = event.type == Event.Type.KEY_RELEASED ?
                        GameEventListener.Action.RELEASED : GameEventListener.Action.PRESSED;

                GameEventListener.Modifier modifier = GameEventListener.Modifier.NONE;
                if (keyEvent.control) modifier = GameEventListener.Modifier.CONTROL;
                if (keyEvent.alt) modifier = GameEventListener.Modifier.ALT;
                if (keyEvent.shift) modifier = GameEventListener.Modifier.SHIFT;

                GameEventListener.Key key = GameEventListener.Key.UNKNOWN;
                switch (keyEvent.key) {
                    case UNKNOWN:
                    case A: key = GameEventListener.Key.A; break;
                    case B: key = GameEventListener.Key.B; break;
                    case C: key = GameEventListener.Key.C; break;
                    case D: key = GameEventListener.Key.D; break;
                    case E: key = GameEventListener.Key.E; break;
                    case F: key = GameEventListener.Key.F; break;
                    case G: key = GameEventListener.Key.G; break;
                    case H: key = GameEventListener.Key.H; break;
                    case I: key = GameEventListener.Key.I; break;
                    case J: key = GameEventListener.Key.J; break;
                    case K: key = GameEventListener.Key.K; break;
                    case L: key = GameEventListener.Key.L; break;
                    case M: key = GameEventListener.Key.M; break;
                    case N: key = GameEventListener.Key.N; break;
                    case O: key = GameEventListener.Key.O; break;
                    case P: key = GameEventListener.Key.P; break;
                    case Q: key = GameEventListener.Key.Q; break;
                    case R: key = GameEventListener.Key.R; break;
                    case S: key = GameEventListener.Key.S; break;
                    case T: key = GameEventListener.Key.T; break;
                    case U: key = GameEventListener.Key.U; break;
                    case V: key = GameEventListener.Key.V; break;
                    case W: key = GameEventListener.Key.W; break;
                    case X: key = GameEventListener.Key.X; break;
                    case Y: key = GameEventListener.Key.Y; break;
                    case Z: key = GameEventListener.Key.Z; break;
                    case NUM0: key = GameEventListener.Key.D_0; break;
                    case NUM1: key = GameEventListener.Key.D_1; break;
                    case NUM2: key = GameEventListener.Key.D_2; break;
                    case NUM3: key = GameEventListener.Key.D_3; break;
                    case NUM4: key = GameEventListener.Key.D_4; break;
                    case NUM5: key = GameEventListener.Key.D_5; break;
                    case NUM6: key = GameEventListener.Key.D_6; break;
                    case NUM7: key = GameEventListener.Key.D_7; break;
                    case NUM8: key = GameEventListener.Key.D_8; break;
                    case NUM9: key = GameEventListener.Key.D_9; break;
                    case ESCAPE: key = GameEventListener.Key.ESCAPE; break;
                    case RETURN: key = GameEventListener.Key.ENTER; break;
                    case BACKSPACE: key = GameEventListener.Key.BACKSPACE; break;
                    case F1: key = GameEventListener.Key.F1; break;
                    case F2: key = GameEventListener.Key.F2; break;
                    case F3: key = GameEventListener.Key.F3; break;
                    case F4: key = GameEventListener.Key.F4; break;
                    case F5: key = GameEventListener.Key.F5; break;
                    case F6: key = GameEventListener.Key.F6; break;
                    case F7: key = GameEventListener.Key.F7; break;
                    case F8: key = GameEventListener.Key.F8; break;
                    case F9: key = GameEventListener.Key.F9; break;
                    case F10: key = GameEventListener.Key.F10; break;
                    case F11: key = GameEventListener.Key.F11; break;
                    case F12: key = GameEventListener.Key.F12; break;
                    case LCONTROL:
                    case LSHIFT:
                    case LALT:
                    case LSYSTEM:
                    case RCONTROL:
                    case RSHIFT:
                    case RALT:
                    case RSYSTEM:
                    case MENU:
                    case LBRACKET:
                    case RBRACKET:
                    case SEMICOLON:
                    case COMMA:
                    case PERIOD:
                    case QUOTE:
                    case SLASH:
                    case BACKSLASH:
                    case EQUAL:
                    case DASH:
                    case UP: key = GameEventListener.Key.UP; break;
                    case DOWN: key = GameEventListener.Key.DOWN; break;
                    case LEFT: key = GameEventListener.Key.LEFT; break;
                    case RIGHT: key = GameEventListener.Key.RIGHT; break;
                    case SPACE: key = GameEventListener.Key.SPACE; break;
                    case TILDE: key = GameEventListener.Key.TILDE; break;
                    case TAB: key = GameEventListener.Key.TAB; break;
                    case PAGEUP: key = GameEventListener.Key.PAGEUP; break;
                    case PAGEDOWN: key = GameEventListener.Key.PAGEDOWN; break;
                    case END:
                    case HOME:
                    case INSERT:
                    case DELETE:
                    case ADD: key = GameEventListener.Key.ADD; break;
                    case SUBTRACT: key = GameEventListener.Key.SUBTRACT; break;
                    case MULTIPLY:
                    case DIVIDE:
                    case NUMPAD0:
                    case NUMPAD1:
                    case NUMPAD2:
                    case NUMPAD3:
                    case NUMPAD4:
                    case NUMPAD5:
                    case NUMPAD6:
                    case NUMPAD7:
                    case NUMPAD8:
                    case NUMPAD9:
                    case F13:
                    case F14:
                    case F15:
                    case PAUSE:
                        key = GameEventListener.Key.UNKNOWN; break;
                }
                _listener.onKeyEvent(_timer, action, key, modifier);
            }
        }
    }

    @Override
    public void setFullScreen(boolean isFullscreen) {
        _window.create(new VideoMode(Constant.WINDOW_WIDTH, Constant.WINDOW_HEIGHT), "FarPoint", isFullscreen ? WindowStyle.NONE : WindowStyle.DEFAULT);
    }

    @Override
    public void drawLight() {
    }

    @Override
    public GameTimer getTimer() {
        return _timer;
    }

    @Override
    public int getWidth() {
        return _window.getSize().x;
    }

    @Override
    public int getHeight() {
        return _window.getSize().y;
    }

    public void setGameEventListener(GameEventListener listener) {
        _listener = listener;
    }
}

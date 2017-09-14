package org.smallbox.faraway.common;

/**
 * Created by Alex on 27/05/2015.
 */
public interface GameEventListener {
//    enum Key {
//        UNKNOWN, ENTER, BACKSPACE, ESCAPE, SPACE(" "), PAGEUP, PAGEDOWN, ADD, SUBTRACT, TAB, TILDE, UP, DOWN, LEFT, RIGHT,
//        F1, F2, F3, F4,F5, F6, F7, F8, F9, F10, F11, F12,
//        A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
//        D_0, D_1, D_2, D_3, D_4, D_5, D_6, D_7, D_8, D_9,
//        PERIOD("."), COMMA(","), PLUS, MINUS;
//
//
//        private final String _text;
//
//        Key(String text) {
//            _text = text;
//        }
//
//        Key() {
//            _text = name().toLowerCase();
//        }
//
//        public String getText() {
//            return _text;
//        }
//    }
    enum Action {PRESSED, RELEASED, MOVE, EXIT}

    enum MouseButton {LEFT, MIDDLE, WHEEL_UP, WHEEL_DOWN, RIGHT}

    enum Modifier {NONE, CONTROL, ALT, SHIFT}

//    void onKeyEvent(Action action, Key key, Modifier modifier);
//    void onMouseEvent(Action action, MouseButton button, int x, int y, boolean b);
//    void onWindowEvent(Action action);
}

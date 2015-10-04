package org.smallbox.faraway.modules.ui;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UIImage;

/**
 * Created by Alex on 01/09/2015.
 */
public class SpeedModule extends GameUIModule {
    private SpeedModuleWindow   _window;
    private int                 _lastSpeed;
    private boolean             _isPaused;

    private class SpeedModuleWindow extends UIWindow {
        @Override
        protected void onCreate(UIWindow window, FrameLayout content) {
            ((UIImage) findById("img_speed")).setImagePath("data/res/ic_speed_1.png");
        }

        @Override
        protected void onRefresh(int update) {
        }

        @Override
        protected String getContentLayout() {
            return "panels/speed";
        }

        public void setSpeed(int speed) {
            switch (speed) {
                case 3:
                    ((UIImage) findById("img_speed")).setImagePath("data/res/ic_speed_3.png");
                    break;
                case 2:
                    ((UIImage) findById("img_speed")).setImagePath("data/res/ic_speed_2.png");
                    break;
                case 1:
                    ((UIImage) findById("img_speed")).setImagePath("data/res/ic_speed_1.png");
                    break;
                default:
                    ((UIImage) findById("img_speed")).setImagePath("data/res/ic_speed_0.png");
                    break;
            }
        }
    }

    @Override
    protected void onLoaded() {
        _window = new SpeedModuleWindow();
        addWindow(_window);
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public boolean onKey(GameEventListener.Key key) {
        if (key == GameEventListener.Key.SPACE) {
            _isPaused = !_isPaused;
            if (_isPaused) {
                Application.getInstance().setSpeed(0);
                _window.setSpeed(0);
            } else {
                Application.getInstance().setSpeed(_lastSpeed);
                _window.setSpeed(_lastSpeed);
            }
            return true;
        }
        if (key == GameEventListener.Key.D_1) {
            Application.getInstance().setSpeed(1);
            _lastSpeed = 1;
            _window.setSpeed(1);
            return true;
        }
        if (key == GameEventListener.Key.D_2) {
            Application.getInstance().setSpeed(2);
            _lastSpeed = 2;
            _window.setSpeed(2);
            return true;
        }
        if (key == GameEventListener.Key.D_3) {
            Application.getInstance().setSpeed(3);
            _lastSpeed = 3;
            _window.setSpeed(3);
            return true;
        }
        return false;
    }

    public int getPriority() {
        return -1;
    }

}

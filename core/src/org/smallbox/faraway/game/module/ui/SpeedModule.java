package org.smallbox.faraway.game.module.ui;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.core.ui.GDXLabel;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.game.module.extra.ResourceModule;
import org.smallbox.faraway.ui.LinkFocusListener;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.Colors;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UIImage;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 01/09/2015.
 */
public class SpeedModule extends GameUIModule {
    private SpeedModuleWindow   _window;

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
            Application.getInstance().setSpeed(0);
            _window.setSpeed(0);
            return true;
        }
        if (key == GameEventListener.Key.D_1) {
            Application.getInstance().setSpeed(1);
            _window.setSpeed(1);
            return true;
        }
        if (key == GameEventListener.Key.D_2) {
            Application.getInstance().setSpeed(2);
            _window.setSpeed(2);
            return true;
        }
        if (key == GameEventListener.Key.D_3) {
            Application.getInstance().setSpeed(3);
            _window.setSpeed(3);
            return true;
        }
        return false;
    }

}

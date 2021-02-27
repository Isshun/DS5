package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.math.Interpolation;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLongUpdate;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameUpdate;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.util.transition.IntegerTransition;

@GameObject
public class InfoWeatherController extends LuaController {
    @Inject private GameTime gameTime;

    @BindLua private View test;

    private IntegerTransition transition;

    @OnGameLongUpdate
    private void onGameLongUpdate() {
        transition = new IntegerTransition(0, 600);
        transition.setDuration(1000);
        transition.setInterpolation(Interpolation.pow2);
    }

    @OnGameUpdate(runOnMainThread = true)
    protected void onControllerUpdate() {
//        if (transition != null) {
////            test.setPosition(transition.getValue(gameTime.now()), 220);
//            test.setPosition(transition.getValue(), 220);
//        }
    }

}

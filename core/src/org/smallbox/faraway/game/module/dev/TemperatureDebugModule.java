package org.smallbox.faraway.game.module.dev;

import org.smallbox.faraway.game.module.*;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.world.TemperatureModule;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.util.StringUtils;

/**
 * Created by Alex on 30/08/2015.
 */
public class TemperatureDebugModule extends GameUIModule {
    @Override
    protected boolean loadOnStart() {
        return false;
    }

    @Override
    protected void onCreate() {
        DebugWindow window = (DebugWindow)WindowDebugBuilder.create().setTitle("Temperature Debug").build(new WindowListener() {
            @Override
            public void onCreate(UIWindow window, FrameLayout view) {
                window.setPosition(500, 500);
            }

            @Override
            public void onRefresh(int update) {
            }

            @Override
            public void onClose() {
            }
        });

        TemperatureModule module = (TemperatureModule)Game.getInstance().getModule(TemperatureModule.class);

        window.addDebugView("increase", view -> module.increaseTemperature());
        window.addDebugView("decrease", view -> module.decreaseTemperature());
        window.addDebugView("normalize", view -> module.normalize());
        window.addDebugView("temperature: " + StringUtils.formatNumber(module.getTemperature()));
        window.addDebugView("temperature target: " + StringUtils.formatNumber(module.getTemperatureTarget()));
        window.addDebugView("temperature offset: " + StringUtils.formatNumber(module.getTemperatureOffset()));

        addWindow(window);
    }

    @Override
    protected void onUpdate(int tick) {
    }
}

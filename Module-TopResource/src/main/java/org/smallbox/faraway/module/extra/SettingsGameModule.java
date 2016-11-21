package org.smallbox.faraway.module.extra;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Log;

/**
 * Created by Alex on 05/07/2015.
 */
public class SettingsGameModule extends GameModule {

    @Override
    protected void onGameUpdate(Game game, int tick) {
    }

    @Override
    public void onReloadUI() {
    }

    @Override
    public void onCustomEvent(String tag, Object object) {
        if ("game_settings.apply".equals(tag)) {
            Log.info("Apply settings");

            LuaValue values = (LuaValue)object;
            Log.info(values.get("ratio").toString());

//            String screenMode = values.get("screen_mode").toString();
//            if (screenMode != null) {
//                Application.config._configChangeListener.onScreeMode(screenMode);
//            }
        }
    }
}
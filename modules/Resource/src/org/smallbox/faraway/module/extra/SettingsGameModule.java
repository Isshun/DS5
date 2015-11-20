package org.smallbox.faraway.module.extra;

import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;

/**
 * Created by Alex on 05/07/2015.
 */
public class SettingsGameModule extends GameModule {
    @Override
    protected boolean loadOnStart() {
        return true;
    }

    @Override
    protected void onLoaded(Game game) {
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public void onReloadUI() {
    }

    private void load() {
    }

    @Override
    public void onCustomEvent(String tag, Object object) {
        if ("game_settings.apply".equals(tag)) {
            System.out.println("Apply settings");

            LuaValue values = (LuaValue)object;
            System.out.println(values.get("ratio").toString());

            String screenMode = values.get("screen_mode").toString();
            if (screenMode != null) {
                Application.getInstance()._configChangeListener.onScreeMode(screenMode);
            }

            System.out.println(values.get("resolution").get(1).toint());
        }
    }
}
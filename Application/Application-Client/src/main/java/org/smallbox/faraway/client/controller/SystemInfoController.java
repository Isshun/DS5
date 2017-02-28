package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;

/**
 * Created by Alex on 27/02/2017.
 */
public class SystemInfoController extends LuaController {

    @BindLua
    private UILabel lbTime;

    @BindLua
    private UILabel lbDay;

    @BindLua
    private UILabel lbTick;

    @BindLua
    private UIImage icSpeed;

    @Override
    protected void onNewGameUpdate(Game game) {
        lbTime.setText(game.getHour() + "H");
        lbDay.setText("Jour " + game.getDay());

        lbTick.setText("Tick " + game.getTick());

        icSpeed.setImage("[base]/graphics/ic_speed_" + game.getSpeed() + ".png");
    }

    @Override
    public void onGamePaused() {
        icSpeed.setImage("[base]/graphics/ic_speed_0.png");
    }

    @Override
    public void onGameResume() {
        icSpeed.setImage("[base]/graphics/ic_speed_" + Application.gameManager.getGame().getSpeed() + ".png");
    }
}

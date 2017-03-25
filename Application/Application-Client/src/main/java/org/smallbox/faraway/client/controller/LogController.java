package org.smallbox.faraway.client.controller;

/**
 * Created by Alex on 26/04/2016.
 */
public class LogController extends LuaController {
//
//    @BindLua
//    private UILabel lbLog1;
//
//    @BindLua
//    private UILabel lbLog2;
//
//    @BindLua
//    private UILabel lbLog3;
//
//    @BindLua
//    private UILabel lbLog4;

    @Override
    protected void onControllerUpdate() {
    }

    @Override
    public void onGamePaused() {
        setVisible(true);
    }

    @Override
    public void onGameResume() {
        setVisible(false);
    }

}

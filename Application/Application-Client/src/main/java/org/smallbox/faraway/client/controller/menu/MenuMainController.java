//package org.smallbox.faraway.client.controller.menu;
//
//import org.smallbox.faraway.client.controller.LuaController;
//import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
//import org.smallbox.faraway.client.ui.engine.views.widgets.View;
//import org.smallbox.faraway.common.dependencyInjector.GameObject;
//import org.smallbox.faraway.core.Application;
//
///**
// * Created by Alex on 24/07/2016.
// */
//@GameObject
//public class MenuMainController extends LuaController {
//
//    @BindLuaAction
//    private void onActionNewGame(View view) {
//        Application.gameManager.createGame("base.planet.corrin", "mountain", 14, 20, 2, null);
//    }
//
//    @BindLuaAction
//    private void onActionExit(View view) {
//        Application.setRunning(false);
//    }
//
//    @BindLuaAction
//    private void onActionContinue(View view) {
//        Application.gameManager.loadLastGame();
//    }
//
//    @BindLuaAction
//    private void onActionLoad(View view) {
//        System.out.println("gg");
//    }
//
//    @BindLuaAction
//    private void onActionSave(View view) {
//        System.out.println("gg");
//    }
//
//    @BindLuaAction
//    private void onActionSettings(View view) {
//    }
//
//}

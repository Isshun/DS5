//package org.smallbox.faraway.client.controller;
//
//import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
//import org.smallbox.faraway.client.ui.UIManager;
//import org.smallbox.faraway.client.ui.engine.views.widgets.View;
//import org.smallbox.faraway.core.Application;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.GameInfo;
//import org.smallbox.faraway.core.game.GameObserverPriority;
//import org.smallbox.faraway.core.game.GameSaveManager;
//
///**
// * Created by Alex
// */
//@GameObserverPriority(GameObserverPriority.Priority.LOW)
//
//@GameObject
//public class GameMenuController extends LuaController {
//
//    @Inject
//    private GameSaveManager gameSaveManager;
//
//    @Inject
//    private UIManager uiManager;
//
//    @Override
//    public boolean onKeyPress(int key) {
////        if (key == Input.Keys.ESCAPE && uiManager.findById("base.ui.right_panel").isVisible()) {
////            setVisible(!isVisible());
////            return true;
////        }
//        return false;
//    }
//
//    @BindLuaAction
//    public void onResume(View view) {
//    }
//
//    @BindLuaAction
//    public void onSave(View view) {
//        uiManager.findById("base.ui.menu_pause").setVisible(false);
////        uiManager.findById("base.ui.menu_save").setVisible(true);
//
//        Game game = Application.gameManager.getGame();
//        gameSaveManager.saveGame(game, game.getInfo(), GameInfo.Type.FAST);
//    }
//
//    @BindLuaAction
//    public void onLoad(View view) {
//        uiManager.findById("base.ui.menu_pause").setVisible(false);
//        uiManager.findById("base.ui.menu_load").setVisible(true);
//    }
//
//    @BindLuaAction
//    public void onSettings(View view) {
//        uiManager.findById("base.ui.menu_pause").setVisible(false);
//        uiManager.findById("base.ui.menu_settings").setVisible(true);
//    }
//
//}

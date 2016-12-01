package org.smallbox.faraway.module.area;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.UIPanelModule;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.module.mainPanel.MainPanelController;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaController extends LuaController {
    @BindModule
    private UIPanelModule       uiPanelModule;

    @BindLua private UILabel    btAddStorage;
    @BindLua private UILabel    btRemoveStorage;

    @BindLua private UILabel    btAddDump;
    @BindLua private UILabel    btRemoveDump;

    @BindLua private UILabel    btAddHome;
    @BindLua private UILabel    btRemoveHome;

    @BindLua private UILabel    btAddSector;
    @BindLua private UILabel    btRemoveSector;

    @BindLua private UILabel    btAddGarden;
    @BindLua private UILabel    btRemoveGarden;

    @BindLuaController
    private MainPanelController mainPanelController;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Areas", (GameEvent event) -> setVisible(true));

        // TODO
//        btAddStorage.setOnClickListener((GameEvent event) -> {
//            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.STORAGE);
//        });
//
//        btRemoveStorage.setOnClickListener((GameEvent event) -> {
//            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.STORAGE);
//        });
//
////        btAddDump.setOnClickListener(() -> {
////            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.STORAGE);
////        });
////
////        btRemoveDump.setOnClickListener(() -> {
////            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.STORAGE);
////        });
//
//        btAddHome.setOnClickListener((GameEvent event) -> {
//            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.HOME);
//        });
//
//        btRemoveHome.setOnClickListener((GameEvent event) -> {
//            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.HOME);
//        });
//
////        btAddSector.setOnClickListener(() -> {
////            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.STORAGE);
////        });
////
////        btRemoveSector.setOnClickListener(() -> {
////            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.STORAGE);
////        });
//
//        btAddGarden.setOnClickListener((GameEvent event) -> {
//            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.GARDEN);
//        });
//
//        btRemoveGarden.setOnClickListener((GameEvent event) -> {
//            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
//        });
    }
}
